package com.miguel.wealth_orders.domain.enums;

public enum OrderStatus {
    PENDING,
    EXECUTED,
    CANCELLED;

    public boolean canTransactionTo(OrderStatus newOrderStatus) {
        return switch (this) {
            case PENDING -> newOrderStatus == EXECUTED || newOrderStatus == CANCELLED;
            case EXECUTED, CANCELLED -> false;
        };
    }
}
