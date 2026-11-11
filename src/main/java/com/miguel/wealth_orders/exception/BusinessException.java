package com.miguel.wealth_orders.exception;

public abstract class BusinessException extends RuntimeException {
    public BusinessException(String errorMessage) {
        super(errorMessage);
    }
}
