package org.aquariux.tradingsystem.core.repository;

import org.aquariux.tradingsystem.core.domain.order.OrderBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import org.aquariux.tradingsystem.core.domain.order.OrderState;

@Repository
public interface TradeOrderRepository extends JpaRepository<OrderBook, Long> {
    Optional<OrderBook> findByIdAndAccountId(long id, long userAccountId);
    List<OrderBook> findAllByAccountId(long userAccountId);
    List<OrderBook> findAllByAccountIdAndState(long userAccountId, OrderState state);
    List<OrderBook> findAllByMarketIdAndState(long marketId, OrderState state);
}
