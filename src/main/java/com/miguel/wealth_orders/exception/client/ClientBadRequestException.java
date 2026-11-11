package com.miguel.wealth_orders.exception.client;

import com.miguel.wealth_orders.exception.NotFoundException;

public class ClientBadRequestException extends NotFoundException { // 400
    public ClientBadRequestException() {
        super("Bad request for client.");
    }
}
