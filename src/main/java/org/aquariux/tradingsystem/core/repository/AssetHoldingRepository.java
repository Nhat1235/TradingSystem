package org.aquariux.tradingsystem.core.repository;

import org.aquariux.tradingsystem.core.domain.asset.AssetHolding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface AssetHoldingRepository extends JpaRepository<AssetHolding, Long> {
    Optional<AssetHolding> findByAccountIdAndAssetId(long userAccountId, long assetId);
    List<AssetHolding> findAllByAccountId(long accountId);
}
