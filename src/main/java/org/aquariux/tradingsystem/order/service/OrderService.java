package org.aquariux.tradingsystem.order.service;

import org.aquariux.tradingsystem.order.models.requests.CreateOrderRequest;
import org.aquariux.tradingsystem.order.models.responses.CreateOrderResponse;
import org.aquariux.tradingsystem.order.models.responses.OrderResponse;

import java.util.List;

public interface OrderService {
    CreateOrderResponse createOrder(CreateOrderRequest request);
    List<OrderResponse> listOrders(long accountId, String status);
}
