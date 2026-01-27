package org.aquariux.tradingsystem.core.repository;

import org.aquariux.tradingsystem.core.domain.market.MarketPair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarketPairRepository extends JpaRepository<MarketPair, Long> {
    Optional<MarketPair> findBySymbol(String symbol);
}
