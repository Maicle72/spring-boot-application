package com.miguel.wealth_orders.exception.order;

import com.miguel.wealth_orders.exception.NotFoundException;

public class OrderNotFoundException extends NotFoundException { // 404
    public OrderNotFoundException(Long orderId) {
        super("Order id %d doesn't exist.".formatted(orderId));
    }
}
