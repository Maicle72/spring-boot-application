package com.miguel.wealth_orders.controller.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record OrderPathRequest(
        @Positive
        @Digits(integer = 19, fraction = 4)
        BigDecimal quantity,

        @Positive
        @Digits(integer = 19, fraction = 4)
        BigDecimal limitPrice) {
}
