package com.miguel.wealth_orders.exception.order;

import com.miguel.wealth_orders.exception.CanNotBeExecutedException;

public class OrderCanNotBeExecutedException extends CanNotBeExecutedException { // 422
    public OrderCanNotBeExecutedException(Long clientId) {
        super("The client id %d can't create this order.".formatted(clientId));
    }
}
