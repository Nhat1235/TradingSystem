package org.aquariux.tradingsystem.core.repository;

import org.aquariux.tradingsystem.core.domain.user.WalletLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletLedgerRepository extends JpaRepository<WalletLedger, Long> {
    WalletLedger findByUserAccountId(Long userAccountId);
}
