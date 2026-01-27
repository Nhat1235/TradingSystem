package org.aquariux.tradingsystem.core.repository;

import org.aquariux.tradingsystem.core.domain.asset.AssetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetTokenRepository extends JpaRepository<AssetToken, Long> {

}
