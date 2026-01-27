package org.aquariux.tradingsystem.core.marketdata;

import lombok.extern.log4j.Log4j2;
import org.aquariux.tradingsystem.core.domain.market.MarketPair;
import org.aquariux.tradingsystem.core.domain.market.MarketQuoteSnapshot;
import org.aquariux.tradingsystem.core.marketdata.binance.BinanceTicker;
import org.aquariux.tradingsystem.core.marketdata.huobi.HuobiTicker;
import org.aquariux.tradingsystem.core.marketdata.huobi.HuobiTickerResponse;
import org.aquariux.tradingsystem.core.repository.AssetTokenRepository;
import org.aquariux.tradingsystem.core.repository.MarketPairRepository;
import org.aquariux.tradingsystem.core.repository.MarketQuoteSnapshotRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
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
    private final String binanceEndpoint;
    private final String huobiEndpoint;
    private final Map<Long, MarketTicks> marketTickMap;

    public MarketDataPoller(RestTemplate restTemplate,
                            MarketPairRepository marketPairRepository,
                            MarketQuoteSnapshotRepository marketQuoteSnapshotRepository,
                            @Value("${scheduler.binance.url}") String binanceEndpoint,
                            @Value("${scheduler.huobi.url}") String huobiEndpoint) {
        this.restTemplate = restTemplate;
        this.marketPairRepository = marketPairRepository;
        this.marketQuoteSnapshotRepository = marketQuoteSnapshotRepository;
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
}
