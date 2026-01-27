package org.aquariux.tradingsystem.core.ledger;


import org.aquariux.tradingsystem.core.domain.order.OrderBook;
import org.aquariux.tradingsystem.core.domain.order.OrderHistory;
import org.aquariux.tradingsystem.core.marketdata.MarketTicks;

public interface BalanceLedger {
    void verifyFunding(OrderBook order, MarketTicks marketTick);
    void applyExecution(OrderHistory transaction, MarketTicks marketTick);
}
