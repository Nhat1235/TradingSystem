package org.aquariux.tradingsystem.core.engine;

import org.aquariux.tradingsystem.core.domain.order.OrderHistory;
import org.aquariux.tradingsystem.core.domain.order.OrderState;
import org.aquariux.tradingsystem.core.domain.order.OrderBook;
import org.aquariux.tradingsystem.core.ledger.BalanceLedger;
import org.aquariux.tradingsystem.core.marketdata.MarketTicks;
import org.aquariux.tradingsystem.core.repository.MarketQuoteSnapshotRepository;
import org.aquariux.tradingsystem.core.repository.OrderHistoryRepository;
import org.aquariux.tradingsystem.core.repository.TradeOrderRepository;
import org.aquariux.tradingsystem.core.validation.OrderValidator;
import org.aquariux.tradingsystem.exception.BusinessException;
import org.aquariux.tradingsystem.order.models.responses.CreateOrderResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class TradeExecutionEngineImpl implements TradeExecutionEngine {
    private final TradeOrderRepository tradeOrderRepository;
    private final BalanceLedger balanceLedger;
    private final OrderValidator orderValidator;
    private final OrderHistoryRepository orderHistoryRepository;
    private final MarketQuoteSnapshotRepository marketQuoteSnapshotRepository;

    public TradeExecutionEngineImpl(TradeOrderRepository tradeOrderRepository,
                                    OrderHistoryRepository orderHistoryRepository,
                                    BalanceLedger balanceLedger,
                                    MarketQuoteSnapshotRepository marketQuoteSnapshotRepository) {
        this.tradeOrderRepository = tradeOrderRepository;
        this.balanceLedger = balanceLedger;
        this.orderValidator = new OrderValidator();
        this.orderHistoryRepository = orderHistoryRepository;
        this.marketQuoteSnapshotRepository = marketQuoteSnapshotRepository;
    }

    public CreateOrderResponse executeOrder(OrderBook order) {
        orderValidator.validateNewOrder(order);
        long marketId = order.getMarketId();
        MarketTicks marketTick = marketQuoteSnapshotRepository.findTopByMarketIdOrderByIdDesc(marketId)
                .map(snapshot -> MarketTicks.builder()
                        .marketId(snapshot.getMarketId())
                        .baseAssetId(snapshot.getBaseAssetId())
                        .quoteAssetId(snapshot.getQuoteAssetId())
                        .symbol(snapshot.getSymbol())
                        .marketVenue(snapshot.getMarketVenue())
                        .bidPrice(snapshot.getBestBidPrice())
                        .askPrice(snapshot.getBestAskPrice())
                        .bidSize(snapshot.getBidSize())
                        .askSize(snapshot.getAskSize())
                        .build())
                .orElseThrow(() -> new BusinessException("Market data unavailable for market id " + marketId));

        balanceLedger.verifyFunding(order, marketTick);

        OrderHistory trade = new OrderHistory();
        trade.setMarketId(order.getMarketId());
        trade.setSide(order.getSide());
        order.setState(OrderState.EXECUTED);
        BigDecimal orderQty = roundQty(order.getQty());
        switch (order.getSide()) {
            case BUY -> {
                BigDecimal fillQty = minQty(orderQty, roundQty(marketTick.getAskSize()));
                order.setFilledQuantity(fillQty.doubleValue());
                double fillPrice = roundPrice(marketTick.getAskPrice());
                order.setAverageFilledPrice(fillPrice);
                trade.setFilledPrice(fillPrice);
            }
            case SELL -> {
                BigDecimal fillQty = minQty(orderQty, roundQty(marketTick.getBidSize()));
                order.setFilledQuantity(fillQty.doubleValue());
                double fillPrice = roundPrice(marketTick.getBidPrice());
                order.setAverageFilledPrice(fillPrice);
                trade.setFilledPrice(fillPrice);
            }
        }
        BigDecimal remaining = orderQty.subtract(BigDecimal.valueOf(order.getFilledQuantity()));
        order.setRemainingQuantity(remaining.max(BigDecimal.ZERO).doubleValue());

        trade.setQty(order.getFilledQuantity());
        OrderBook successfulOrder = tradeOrderRepository.save(order);

        trade.setAccountId(order.getAccountId());
        trade.setOrder(order);

        OrderHistory successfulTrade = orderHistoryRepository.save(trade);

        // If the trade only handles a portion of the original quantity, create a new OrderBook to await for a Hit from the CronJob once another oder matches
        if (order.getRemainingQuantity() > 0) {
            OrderBook pendingOrder = getOrderBook(order);
            tradeOrderRepository.save(pendingOrder);
        }
        balanceLedger.applyExecution(successfulTrade, marketTick);

        return CreateOrderResponse.builder()
                .orderId(String.valueOf(successfulOrder.getId()))
                .orderStatus(successfulOrder.getState().name())
                .createdAt(successfulOrder.getCreatedAt().toString())
                .build();
    }

    private static OrderBook getOrderBook(OrderBook order) {
        OrderBook pendingOrder = new OrderBook();
        pendingOrder.setAccountId(order.getAccountId());
        pendingOrder.setMarketId(order.getMarketId());
        pendingOrder.setQty(order.getRemainingQuantity());
        pendingOrder.setLimitPrice(order.getLimitPrice());
        pendingOrder.setType(order.getType());
        pendingOrder.setSide(order.getSide());
        pendingOrder.setState(OrderState.NEW);
        pendingOrder.setFilledQuantity(0);
        pendingOrder.setAverageFilledPrice(0);
        pendingOrder.setRemainingQuantity(order.getRemainingQuantity());
        return pendingOrder;
    }

    private static BigDecimal roundQty(double value) {
        return BigDecimal.valueOf(value).setScale(8, RoundingMode.DOWN);
    }

    private static double roundPrice(double value) {
        return BigDecimal.valueOf(value).setScale(8, RoundingMode.DOWN).doubleValue();
    }

    private static BigDecimal minQty(BigDecimal left, BigDecimal right) {
        return left.compareTo(right) <= 0 ? left : right;
    }
}
