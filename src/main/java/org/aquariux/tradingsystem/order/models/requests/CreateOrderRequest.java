package org.aquariux.tradingsystem.order.models.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CreateOrderRequest {
   /*
    Ideally user information can be fetched if a token is present,
    but based on the Assumption that User has already authenticated and authorised to access the APIs,
    I will use accountId here as parameter
    */
    @Schema(example = "1")
    private String accountId;
    @Schema(example = "BTCUSDT")
    private String symbol;
    @Schema(example = "BUY")
    private String side;
    @Schema(example = "MARKET")
    private String type;
    @Schema(example = "1")
    private String quantity;
    @Schema(nullable = true)
    private String limitPrice;
}
