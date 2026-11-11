package com.miguel.wealth_orders.exception;

public abstract class ConflictException extends RuntimeException { // 409
    public ConflictException(String errorMessage) {
        super(errorMessage);
    }
}
