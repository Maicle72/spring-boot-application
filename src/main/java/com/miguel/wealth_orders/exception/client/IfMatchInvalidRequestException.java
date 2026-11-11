package com.miguel.wealth_orders.exception.client;

import com.miguel.wealth_orders.exception.BusinessException;

public class IfMatchInvalidRequestException extends BusinessException { // 400
    public IfMatchInvalidRequestException() {
        super("The header If Match is mandatory for this endpoint.");
    }
}
