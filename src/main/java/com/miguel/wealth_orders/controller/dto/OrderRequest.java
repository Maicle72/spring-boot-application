package com.miguel.wealth_orders.controller.dto;

import com.miguel.wealth_orders.domain.enums.OrderStatus;
import com.miguel.wealth_orders.domain.enums.OrderType;

import java.math.BigDecimal;

public record OrderRequest(String isin, OrderType orderType, BigDecimal quantity, BigDecimal limitPrice,
                       OrderStatus orderStatus) {
}
