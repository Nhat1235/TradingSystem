package org.aquariux.tradingsystem.core.repository;

import org.aquariux.tradingsystem.core.domain.user.AccountProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountProfileRepository extends JpaRepository<AccountProfile, Long> {
}
