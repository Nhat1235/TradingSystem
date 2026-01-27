package org.aquariux.tradingsystem.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.aquariux.tradingsystem.common.ApiResponse;
import org.aquariux.tradingsystem.common.Constants;
import org.aquariux.tradingsystem.order.models.requests.CreateOrderRequest;
import org.aquariux.tradingsystem.order.models.responses.CreateOrderResponse;
import org.aquariux.tradingsystem.order.models.responses.OrderResponse;
import org.aquariux.tradingsystem.order.models.responses.TradeHistoryResponse;
import org.aquariux.tradingsystem.order.service.OrderService;
import org.aquariux.tradingsystem.order.service.TradeHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(Constants.API_BASE_PATH + "/orders")
@Tag(name = "Orders", description = "Order trade endpoints")
public class OrderController {
    private final OrderService orderService;
    private final TradeHistoryService tradeHistoryService;

    public OrderController(OrderService orderService,
                           TradeHistoryService tradeHistoryService) {
        this.orderService = orderService;
        this.tradeHistoryService = tradeHistoryService;
    }

    @PostMapping
    @Operation(summary = "Create order", description = "Execute a crypto market order")
    public ResponseEntity<ApiResponse<CreateOrderResponse>> createOrder(
            @RequestBody CreateOrderRequest request) {
        CreateOrderResponse response = orderService.createOrder(request);
        return ResponseEntity.ok(ApiResponse.success("Order executed", response));
    }

    /*
    This API is here for ease of access
    */
    @GetMapping
    @Operation(summary = "List orders", description = "List orders filtered by status")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> listOrders(
            @RequestParam("accountId") long accountId,
            @RequestParam(value = "status", required = false) String status) {
        List<OrderResponse> response = orderService.listOrders(accountId, status);
        return ResponseEntity.ok(ApiResponse.success("Orders retrieved", response));
    }

    @GetMapping("/history")
    @Operation(summary = "Get trade history", description = "Retrieve user trading history")
    public ResponseEntity<ApiResponse<List<TradeHistoryResponse>>> getTradeHistory(
            @RequestParam("accountId") long accountId) {
        List<TradeHistoryResponse> response = tradeHistoryService.getTradeHistory(accountId);
        return ResponseEntity.ok(ApiResponse.success("Trade history retrieved", response));
    }
}
