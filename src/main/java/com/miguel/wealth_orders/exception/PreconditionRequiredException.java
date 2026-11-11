package com.miguel.wealth_orders.exception;

public abstract class PreconditionRequiredException extends RuntimeException { // 428
    public PreconditionRequiredException(String errorMessage) {
        super(errorMessage);
    }
}
