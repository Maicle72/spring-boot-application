package com.miguel.wealth_orders.exception;

public abstract class NotFoundException extends RuntimeException { // 404
    public NotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
