package org.aquariux.tradingsystem.market.service.impl;

import org.aquariux.tradingsystem.core.domain.market.MarketQuoteSnapshot;
import org.aquariux.tradingsystem.core.domain.order.OrderSide;
import org.aquariux.tradingsystem.core.repository.MarketQuoteSnapshotRepository;
import org.aquariux.tradingsystem.exception.ResourceNotFoundException;
import org.aquariux.tradingsystem.market.models.response.LatestAggregatedPriceResponse;
import org.aquariux.tradingsystem.market.models.response.MarketTickBySideResponse;
import org.aquariux.tradingsystem.market.service.MarketDataService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MarketDataServiceImpl implements MarketDataService {
    private final MarketQuoteSnapshotRepository marketQuoteSnapshotRepository;

    public MarketDataServiceImpl(MarketQuoteSnapshotRepository marketQuoteSnapshotRepository) {
        this.marketQuoteSnapshotRepository = marketQuoteSnapshotRepository;
    }

    @Override
    public LatestAggregatedPriceResponse getLatestAggregatedPrice(String symbol) {
        String normalizedSymbol = symbol.trim().toUpperCase();
        MarketQuoteSnapshot snapshot = marketQuoteSnapshotRepository.findTopBySymbolOrderByIdDesc(normalizedSymbol)
                .orElseThrow(() -> new ResourceNotFoundException("Market snapshot", "symbol", normalizedSymbol));

        return LatestAggregatedPriceResponse.builder()
                .marketId(snapshot.getMarketId())
                .symbol(snapshot.getSymbol())
//                .marketVenue(snapshot.getMarketVenue().name())
                .bestBidPrice(snapshot.getBestBidPrice())
                .bestAskPrice(snapshot.getBestAskPrice())
//                .bidSize(snapshot.getBidSize())
//                .askSize(snapshot.getAskSize())
                .build();
    }

}
