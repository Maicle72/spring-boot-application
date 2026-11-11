package com.miguel.wealth_orders.exception.order;

import com.miguel.wealth_orders.domain.enums.OrderStatus;
import com.miguel.wealth_orders.exception.ConflictException;

public class OrderNotPendingException extends ConflictException { // 409
    public OrderNotPendingException(Long orderId, OrderStatus orderStatusName, String action) {
        super("Order %d is %s and cannot be %s.".formatted(orderId, orderStatusName, action));
    }
}
