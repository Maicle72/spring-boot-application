package com.miguel.wealth_orders.exception.client;

import com.miguel.wealth_orders.exception.NotFoundException;

public class ClientNotFoundException extends NotFoundException { // 404
    public ClientNotFoundException(Long clientId) {
        super("Client id %d doesn't exist.".formatted(clientId));
    }
}
