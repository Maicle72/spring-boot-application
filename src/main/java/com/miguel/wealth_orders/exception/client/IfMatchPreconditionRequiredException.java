package com.miguel.wealth_orders.exception.client;

import com.miguel.wealth_orders.exception.PreconditionRequiredException;

public class IfMatchPreconditionRequiredException extends PreconditionRequiredException { // 428
    public IfMatchPreconditionRequiredException() {
        super("The header If Match is mandatory for this endpoint.");
    }
}
