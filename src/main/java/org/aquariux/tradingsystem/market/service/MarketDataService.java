package org.aquariux.tradingsystem.market.service;

import org.aquariux.tradingsystem.market.models.response.LatestAggregatedPriceResponse;
import org.aquariux.tradingsystem.market.models.response.MarketTickBySideResponse;

public interface MarketDataService {
    LatestAggregatedPriceResponse getLatestAggregatedPrice(String symbol);
}
