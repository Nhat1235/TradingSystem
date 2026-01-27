package org.aquariux.tradingsystem.core.marketdata;

import lombok.Builder;
import lombok.Getter;
import org.aquariux.tradingsystem.core.domain.market.MarketVenue;

@Builder
@Getter
public class MarketTicks {
    private final long marketId;
    private final long baseAssetId;
    private final long quoteAssetId;
    private final String symbol;
    private final MarketVenue marketVenue;
    private final double bidPrice;
    private final double askPrice;
    private final double bidSize;
    private final double askSize;
}
