package com.miguel.wealth_orders.controller.dto;

import com.miguel.wealth_orders.domain.enums.OrderType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record OrderRequest(
        @NotBlank
        @Pattern(regexp = "[A-Z]{2}[A-Z0-9]{9}[0-9]")
        String isin,

        @NotNull
        @Enumerated(EnumType.STRING)
        OrderType orderType,

        @NotNull
        @Positive
        @Digits(integer = 19, fraction = 4)
        BigDecimal quantity,

        @NotNull
        @Positive
        @Digits(integer = 19, fraction = 4)
        BigDecimal limitPrice) {
}
