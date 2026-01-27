package org.aquariux.tradingsystem.market.models.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class MarketTickBySideResponse {
    private long marketId;
    private String symbol;
    private List<SideQuote> quotes;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class SideQuote {
        private String side;
        private double price;
        private double size;
    }
}
