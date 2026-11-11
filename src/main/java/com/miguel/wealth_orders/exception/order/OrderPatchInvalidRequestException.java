package com.miguel.wealth_orders.exception.order;

import com.miguel.wealth_orders.exception.BusinessException;

public class OrderPatchInvalidRequestException extends BusinessException { // 400
    public OrderPatchInvalidRequestException() {
        super("The order patch endpoint received an empty body.");
    }
}
