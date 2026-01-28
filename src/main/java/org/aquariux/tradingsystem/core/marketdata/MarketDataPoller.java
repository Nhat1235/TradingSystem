package org.aquariux.tradingsystem.core.marketdata;

import lombok.extern.log4j.Log4j2;
import org.aquariux.tradingsystem.core.domain.market.MarketPair;
import org.aquariux.tradingsystem.core.domain.market.MarketQuoteSnapshot;
import org.aquariux.tradingsystem.core.domain.order.OrderBook;
import org.aquariux.tradingsystem.core.domain.order.OrderSide;
import org.aquariux.tradingsystem.core.domain.order.OrderState;
import org.aquariux.tradingsystem.core.domain.order.OrderType;
import org.aquariux.tradingsystem.core.engine.TradeExecutionEngine;
import org.aquariux.tradingsystem.core.marketdata.binance.BinanceTicker;
import org.aquariux.tradingsystem.core.marketdata.huobi.HuobiTicker;
import org.aquariux.tradingsystem.core.marketdata.huobi.HuobiTickerResponse;
import org.aquariux.tradingsystem.core.repository.MarketPairRepository;
import org.aquariux.tradingsystem.core.repository.MarketQuoteSnapshotRepository;
import org.aquariux.tradingsystem.core.repository.TradeOrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Log4j2
public class MarketDataPoller {
    private static final long POLL_INTERVAL_MS = 10_000L;
    private static final String BINANCE = "Binance";
    private static final String HUOBI = "Huobi";

    private final RestTemplate restTemplate;
    private final MarketPairRepository marketPairRepository;
    private final MarketQuoteSnapshotRepository marketQuoteSnapshotRepository;
    private final TradeOrderRepository tradeOrderRepository;
    private final TradeExecutionEngine tradeExecutionEngine;
    private final String binanceEndpoint;
    private final String huobiEndpoint;
    private final Map<Long, MarketTicks> marketTickMap;

    public MarketDataPoller(RestTemplate restTemplate,
                            MarketPairRepository marketPairRepository,
                            MarketQuoteSnapshotRepository marketQuoteSnapshotRepository,
                            TradeOrderRepository tradeOrderRepository,
                            TradeExecutionEngine tradeExecutionEngine,
                            @Value("${scheduler.binance.url}") String binanceEndpoint,
                            @Value("${scheduler.huobi.url}") String huobiEndpoint) {
        this.restTemplate = restTemplate;
        this.marketPairRepository = marketPairRepository;
        this.marketQuoteSnapshotRepository = marketQuoteSnapshotRepository;
        this.tradeOrderRepository = tradeOrderRepository;
        this.tradeExecutionEngine = tradeExecutionEngine;
        this.binanceEndpoint = binanceEndpoint;
        this.huobiEndpoint = huobiEndpoint;
        this.marketTickMap = new HashMap<>();
    }

    @Scheduled(fixedRate = POLL_INTERVAL_MS, initialDelay = 500)
    public void pollMarketTicks() {
        List<MarketPair> marketPairs = marketPairRepository.findAll();
        if (marketPairs.isEmpty()) {
            return;
        }

        Set<String> symbols = marketPairs.stream()
                .map(MarketPair::getSymbol)
                .collect(Collectors.toSet());

        Map<String, BinanceTicker> binanceTickers = fetchBinance(symbols);
        Map<String, HuobiTicker> huobiTickers = fetchHuobi(symbols);

        for (MarketPair market : marketPairs) {
            String symbolKey = market.getSymbol().toUpperCase();
            HuobiTicker huobiTicker = huobiTickers.getOrDefault(symbolKey, new HuobiTicker());
            BinanceTicker binanceTicker = binanceTickers.getOrDefault(symbolKey, new BinanceTicker());
            MarketTicks marketTick = MarketTicks.builder()
                    .marketId(market.getId())
                    .baseAssetId(market.getBaseAssetId())
                    .quoteAssetId(market.getQuoteAssetId())
                    .symbol(market.getSymbol())
                    .marketVenue(market.getMarketVenue())
                    .bidPrice(Math.max(huobiTicker.getBid(), binanceTicker.getBidPrice()))
                    .askPrice(Math.min(huobiTicker.getAsk(), binanceTicker.getAskPrice()))
                    .bidSize(huobiTicker.getBid() > binanceTicker.getBidPrice() ? huobiTicker.getBidSize() : binanceTicker.getBidQty())
                    .askSize(huobiTicker.getAsk() < binanceTicker.getAskPrice() ? huobiTicker.getAskSize() : binanceTicker.getAskQty())
                    .build();
            marketTickMap.put(market.getId(), marketTick);
            persistMarketTickSnapshot(marketTick);

            // After saving, try to match any pending orders instantly
            tryMatchInstantly(marketTick);
        }
        log.debug("MarketTicker: {}", marketTickMap);
    }

    private Map<String, BinanceTicker> fetchBinance(Set<String> symbolSet) {
        try {
            ResponseEntity<BinanceTicker[]> binanceTickerResponse =
                    restTemplate.getForEntity(binanceEndpoint, BinanceTicker[].class);
            BinanceTicker[] binanceTickerArray = binanceTickerResponse.getBody();
            if (binanceTickerArray == null) {
                return Collections.emptyMap();
            }
            return Arrays.stream(binanceTickerArray)
                    .filter(t -> symbolSet.contains(t.getSymbol().toUpperCase()))
                    .collect(Collectors.toMap(BinanceTicker::getSymbol, Function.identity()));
        } catch (Exception e) {
            log.warn("Failed to fetch binace from {}.", BINANCE);
            return Collections.emptyMap();
        }
    }

    private Map<String, HuobiTicker> fetchHuobi(Set<String> symbolSet) {
        try {
            ResponseEntity<HuobiTickerResponse> response =
                    restTemplate.getForEntity(huobiEndpoint, HuobiTickerResponse.class);

            HuobiTickerResponse body = response.getBody();
            if (body == null || body.getData() == null) {
                return Collections.emptyMap();
            }

            return body.getData()
                    .stream()
                    .filter(marketTick -> symbolSet.contains(marketTick.getSymbol().toUpperCase()))
                    .collect(Collectors.toMap(huobiTicker -> huobiTicker.getSymbol().toUpperCase(), Function.identity()));

        } catch (Exception e) {
            log.warn("Failed to fetch huobi from {}.", HUOBI, e);
            return Collections.emptyMap();
        }
    }

    private void persistMarketTickSnapshot(MarketTicks marketTick) {
        MarketQuoteSnapshot marketQuoteSnapshot = MarketQuoteSnapshot.builder()
                .marketId(marketTick.getMarketId())
                .baseAssetId(marketTick.getBaseAssetId())
                .quoteAssetId(marketTick.getQuoteAssetId())
                .symbol(marketTick.getSymbol())
                .marketVenue(marketTick.getMarketVenue())
                .bestBidPrice(marketTick.getBidPrice())
                .bidSize(marketTick.getBidSize())
                .bestAskPrice(marketTick.getAskPrice())
                .askSize(marketTick.getAskSize())
                .build();
        marketQuoteSnapshotRepository.save(marketQuoteSnapshot);
    }

    /*
        This function will try and match then execute with every order that has is valid,
        normally in other trading system there will be a logic to determine the best order to match based on price and time
    */
    private void tryMatchInstantly(MarketTicks marketTick) {
        List<OrderBook> orders = tradeOrderRepository.findAllByMarketIdAndState(
                marketTick.getMarketId(), OrderState.NEW);
        if (orders.isEmpty()) {
            return;
        }
        for (OrderBook order : orders) {
            if (!canExecute(order, marketTick)) {
                continue;
            }
            try {
                tradeExecutionEngine.executeOrder(order);
            } catch (Exception ex) {
                log.warn("Failed to execute order {} for market {}",
                        order.getId(), marketTick.getMarketId(), ex);
            }
        }
    }

    private boolean canExecute(OrderBook order, MarketTicks marketTick) {
    if (order.getState() != OrderState.NEW || order.getRemainingQuantity() <= 0) {
        return false;
    }

    if (order.getType() == OrderType.MARKET) {
        return true;
    }

    // From here on: LIMIT orders only
    BigDecimal limitPrice = BigDecimal.valueOf(order.getLimitPrice());
    if (order.getSide() == OrderSide.BUY) {
        return limitPrice.compareTo(BigDecimal.valueOf(marketTick.getAskPrice())) >= 0;
    }

    return limitPrice.compareTo(BigDecimal.valueOf(marketTick.getBidPrice())) <= 0;
}

}
