package com.miguel.wealth_orders.controller.dto;

import com.miguel.wealth_orders.domain.enums.ClientRiskProfile;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ClientRequest(
        @NotNull
        String fullName,

        @NotBlank
        @Email(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
        String email,

        @NotNull
        @Enumerated(EnumType.STRING)
        ClientRiskProfile clientRiskProfile) {
}
