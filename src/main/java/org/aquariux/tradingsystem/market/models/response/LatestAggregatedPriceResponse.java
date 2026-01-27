package org.aquariux.tradingsystem.market.models.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LatestAggregatedPriceResponse {
    private long marketId;
    private String symbol;
//    private String marketVenue;
    private double bestBidPrice;
    private double bestAskPrice;
//    private double bidSize;
//    private double askSize;
}