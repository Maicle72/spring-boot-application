package com.miguel.wealth_orders.exception.client;

import com.miguel.wealth_orders.exception.ConflictException;

public class EmailExistsConflictException extends ConflictException { // 409
    public EmailExistsConflictException(String clientEmail) {
        super("The email %s is already being used.".formatted(clientEmail));
    }
}
