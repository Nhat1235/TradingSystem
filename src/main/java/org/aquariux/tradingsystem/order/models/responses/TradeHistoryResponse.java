package org.aquariux.tradingsystem.order.models.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TradeHistoryResponse {
    private String tradeId;
    private String marketId;
    private String side;
    private String quantity;
    private String price;
    private String createdAt;
}
