package org.aquariux.tradingsystem.order.service.impl;

import org.aquariux.tradingsystem.core.domain.order.OrderHistory;
import org.aquariux.tradingsystem.core.repository.OrderHistoryRepository;
import org.aquariux.tradingsystem.order.models.responses.TradeHistoryResponse;
import org.aquariux.tradingsystem.order.service.TradeHistoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TradeHistoryServiceImpl implements TradeHistoryService {
    private final OrderHistoryRepository orderHistoryRepository;

    public TradeHistoryServiceImpl(OrderHistoryRepository orderHistoryRepository) {
        this.orderHistoryRepository = orderHistoryRepository;
    }

    @Override
    public List<TradeHistoryResponse> getTradeHistory(long accountId) {
        return orderHistoryRepository.findAllByAccountIdOrderByIdDesc(accountId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private TradeHistoryResponse toResponse(OrderHistory history) {
        return TradeHistoryResponse.builder()
                .tradeId(String.valueOf(history.getId()))
                .marketId(String.valueOf(history.getMarketId()))
                .side(history.getSide().name())
                .quantity(String.valueOf(history.getQty()))
                .price(String.valueOf(history.getFilledPrice()))
                .createdAt(history.getCreatedAt() != null
                        ? history.getCreatedAt().toString() : null)
                .build();
    }
}
