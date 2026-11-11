package com.miguel.wealth_orders.exception.client;

import com.miguel.wealth_orders.exception.PreconditionException;

public class ClientVersionPreconditionException extends PreconditionException { // 412
    public ClientVersionPreconditionException(Long clientId, long clientVersion, long expectedVersion) {
        super("The client id %d has version value %d but received %d".formatted(clientId, clientVersion, expectedVersion));
    }
}
