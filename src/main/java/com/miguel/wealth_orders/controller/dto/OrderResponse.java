package com.miguel.wealth_orders.controller.dto;

import com.miguel.wealth_orders.domain.Client;
import com.miguel.wealth_orders.domain.Order;
import com.miguel.wealth_orders.domain.enums.OrderStatus;
import com.miguel.wealth_orders.domain.enums.OrderType;

import java.math.BigDecimal;

public record OrderResponse(Long id, Client client, String isin, OrderType orderType, BigDecimal quantity,
                            BigDecimal limitPrice,
                            OrderStatus orderStatus) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(order.getId(), order.getClient(), order.getIsin(), order.getOrderType(), order.getQuantity(), order.getLimitPrice(), order.getOrderStatus());
    }
}
