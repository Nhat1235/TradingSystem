package org.aquariux.tradingsystem.order.service;

import org.aquariux.tradingsystem.order.models.responses.TradeHistoryResponse;

import java.util.List;

public interface TradeHistoryService {
    List<TradeHistoryResponse> getTradeHistory(long accountId);
}
