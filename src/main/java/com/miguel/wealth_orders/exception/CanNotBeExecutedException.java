package com.miguel.wealth_orders.exception;

public abstract class CanNotBeExecutedException extends RuntimeException { // 422
    public CanNotBeExecutedException(String errorMessage) {
        super(errorMessage);
    }
}
