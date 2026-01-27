package org.aquariux.tradingsystem.core.engine;

import org.aquariux.tradingsystem.core.domain.order.OrderBook;
import org.aquariux.tradingsystem.order.models.responses.CreateOrderResponse;

public interface TradeExecutionEngine {
    CreateOrderResponse executeOrder(OrderBook order);
}
