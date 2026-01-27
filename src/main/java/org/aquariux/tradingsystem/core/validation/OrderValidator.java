package org.aquariux.tradingsystem.core.validation;

import org.aquariux.tradingsystem.core.domain.order.OrderBook;
import org.aquariux.tradingsystem.core.domain.order.OrderType;
import org.aquariux.tradingsystem.exception.BusinessException;

public class OrderValidator {
    public void validateNewOrder(OrderBook order) {
        if (order == null) {
            throw new BusinessException("Order payload is required.");
        }
        if (order.getAccountId() == null) {
            throw new BusinessException("User account is required.");
        }
        if (order.getMarketId() <= 0) {
            throw new BusinessException("Market is required.");
        }
        if (order.getQty() <= 0) {
            throw new BusinessException("Order quantity must be positive.");
        }
        if (order.getType() == null) {
            throw new BusinessException("Order type is required.");
        }
        if (order.getSide() == null) {
            throw new BusinessException("Order side is required.");
        }
        if (order.getType() == OrderType.LIMIT && order.getLimitPrice() <= 0) {
            throw new BusinessException("Limit price must be positive.");
        }
    }
}
