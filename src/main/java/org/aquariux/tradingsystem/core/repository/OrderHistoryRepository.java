package org.aquariux.tradingsystem.core.repository;

import org.aquariux.tradingsystem.core.domain.order.OrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long> {
}
