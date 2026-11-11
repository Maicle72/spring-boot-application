package com.miguel.wealth_orders.exception;

public abstract class PreconditionException extends RuntimeException { // 412
    public PreconditionException(String errorMessage) {
        super(errorMessage);
    }
}
