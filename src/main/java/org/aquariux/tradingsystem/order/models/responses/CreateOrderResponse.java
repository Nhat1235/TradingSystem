package org.aquariux.tradingsystem.order.models.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateOrderResponse {
    private String orderId;
    private String orderStatus;
    private String createdAtDatetime;
}
