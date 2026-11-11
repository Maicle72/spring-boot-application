package com.miguel.wealth_orders.exception.client;

import com.miguel.wealth_orders.exception.ConflictException;

public class ClientWithPendingOrdersException extends ConflictException { // 409
    public ClientWithPendingOrdersException(Long clientId) {
        super("The client id %d has pending orders.".formatted(clientId));
    }
}
