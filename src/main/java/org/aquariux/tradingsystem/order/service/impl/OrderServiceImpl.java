package org.aquariux.tradingsystem.order.service.impl;

import org.aquariux.tradingsystem.core.domain.market.MarketPair;
import org.aquariux.tradingsystem.core.domain.order.OrderBook;
import org.aquariux.tradingsystem.core.domain.order.OrderSide;
import org.aquariux.tradingsystem.core.domain.order.OrderState;
import org.aquariux.tradingsystem.core.domain.order.OrderType;
import org.aquariux.tradingsystem.core.engine.TradeExecutionEngine;
import org.aquariux.tradingsystem.core.repository.MarketPairRepository;
import org.aquariux.tradingsystem.core.repository.TradeOrderRepository;
import org.aquariux.tradingsystem.exception.BusinessException;
import org.aquariux.tradingsystem.exception.ResourceNotFoundException;
import org.aquariux.tradingsystem.model.BaseEntity;
import org.aquariux.tradingsystem.order.models.requests.CreateOrderRequest;
import org.aquariux.tradingsystem.order.models.responses.CreateOrderResponse;
import org.aquariux.tradingsystem.order.models.responses.OrderResponse;
import org.aquariux.tradingsystem.order.service.OrderService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    private final MarketPairRepository marketPairRepository;
    private final TradeExecutionEngine tradeExecutionEngine;
    private final TradeOrderRepository tradeOrderRepository;

    public OrderServiceImpl(MarketPairRepository marketPairRepository,
                            TradeExecutionEngine tradeExecutionEngine,
                            TradeOrderRepository tradeOrderRepository) {
        this.marketPairRepository = marketPairRepository;
        this.tradeExecutionEngine = tradeExecutionEngine;
        this.tradeOrderRepository = tradeOrderRepository;
    }

    @Override
    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        if (request == null) {
            throw new BusinessException("Order payload is required.");
        }

        String symbol = requireText(request.getSymbol(), "symbol");
        MarketPair marketPair = marketPairRepository.findBySymbol(symbol.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Market", "symbol", symbol));

        long accountId = parseLong(request.getAccountId(), "accountId");
        double quantity = parsePositiveDouble(request.getQuantity(), "quantity");
        OrderType orderType = parseOrderType(request.getType());
        OrderSide orderSide = parseOrderSide(request.getSide());
        double limitPrice = parseLimitPrice(orderType, request.getLimitPrice());

        OrderBook order = new OrderBook();
        order.setAccountId(accountId);
        order.setMarketId(marketPair.getId());
        order.setQty(quantity);
        order.setLimitPrice(limitPrice);
        order.setType(orderType);
        order.setSide(orderSide);
        order.setState(OrderState.NEW);
        order.setFilledQuantity(0);
        order.setAverageFilledPrice(0);
        order.setRemainingQuantity(quantity);

        return tradeExecutionEngine.executeOrder(order);
    }

    @Override
    public List<OrderResponse> listOrders(long accountId, String status) {
        List<OrderBook> orders = status == null || status.trim().isEmpty()
                ? tradeOrderRepository.findAllByAccountId(accountId)
                : tradeOrderRepository.findAllByAccountIdAndState(accountId, parseOrderState(status));

        Map<Long, String> marketSymbolById = marketPairRepository.findAllById(
                        orders.stream().map(OrderBook::getMarketId).distinct().collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(BaseEntity::getId, MarketPair::getSymbol, (a, b) -> a, HashMap::new));

        return orders.stream()
                .map(order -> OrderResponse.builder()
                        .orderId(String.valueOf(order.getId()))
                        .marketSymbol(marketSymbolById.getOrDefault(order.getMarketId(), ""))
                        .orderQuantity(String.valueOf(order.getQty()))
                        .limitPrice(String.valueOf(order.getLimitPrice()))
                        .filledQuantity(String.valueOf(order.getFilledQuantity()))
                        .averageFilledPrice(String.valueOf(order.getAverageFilledPrice()))
                        .remainingQuantity(String.valueOf(order.getRemainingQuantity()))
                        .orderType(order.getType().name())
                        .orderStatus(order.getState().name())
                        .orderSide(order.getSide().name())
                        .createdAt(order.getCreatedAt() != null ? order.getCreatedAt().toString() : null)
                        .tradeIds(order.getFills() == null ? List.of() : order.getFills().stream()
                                .map(fill -> String.valueOf(fill.getId()))
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }

    private String requireText(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new BusinessException(field + " is required.");
        }
        return value.trim();
    }

    private long parseLong(String value, String field) {
        try {
            return Long.parseLong(requireText(value, field));
        } catch (NumberFormatException ex) {
            throw new BusinessException(field + " must be a number.");
        }
    }

    private double parsePositiveDouble(String value, String field) {
        try {
            double parsed = Double.parseDouble(requireText(value, field));
            if (parsed <= 0) {
                throw new BusinessException(field + " must be positive.");
            }
            return parsed;
        } catch (NumberFormatException ex) {
            throw new BusinessException(field + " must be a number.");
        }
    }

    private OrderType parseOrderType(String value) {
        try {
            return OrderType.valueOf(requireText(value, "type").toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BusinessException("Invalid order type.");
        }
    }

    private OrderSide parseOrderSide(String value) {
        try {
            return OrderSide.valueOf(requireText(value, "side").toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BusinessException("Invalid order side.");
        }
    }

    private OrderState parseOrderState(String value) {
        try {
            return OrderState.valueOf(requireText(value, "status").toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BusinessException("Invalid order status.");
        }
    }

    private double parseLimitPrice(OrderType orderType, String limitPrice) {
        if (orderType == OrderType.LIMIT) {
            return parsePositiveDouble(limitPrice, "limitPrice");
        }
        return 0;
    }
}
