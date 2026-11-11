package com.miguel.wealth_orders.exception.order;

import com.miguel.wealth_orders.exception.BusinessException;

public class InvalidRequestOrderQuantityAndLimitException extends BusinessException { // 422
    public InvalidRequestOrderQuantityAndLimitException() {
        super("Quantity and limit price are mandatory.");
    }
}
