package org.aquariux.tradingsystem.core.repository;

import org.aquariux.tradingsystem.core.domain.market.MarketQuoteSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MarketQuoteSnapshotRepository extends JpaRepository<MarketQuoteSnapshot, Long> {
    Optional<MarketQuoteSnapshot> findTopBySymbolOrderByIdDesc(String symbol);
    Optional<MarketQuoteSnapshot> findTopByMarketIdOrderByIdDesc(long marketId);
}
