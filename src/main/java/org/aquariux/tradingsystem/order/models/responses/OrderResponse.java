package org.aquariux.tradingsystem.order.models.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class OrderResponse {
    private String orderId;
    private String limitPrice;
    private String marketSymbol;
    private String orderQuantity;
    private String filledQuantity;
    private String averageFilledPrice;
    private String remainingQuantity;
    private String orderType;
    private String orderStatus;
    private String orderSide;
    private String createdAt;
    private List<String> tradeIds;
}
