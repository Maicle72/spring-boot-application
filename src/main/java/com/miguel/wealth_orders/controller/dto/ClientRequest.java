package com.miguel.wealth_orders.controller.dto;

import com.miguel.wealth_orders.domain.enums.ClientRiskProfile;

public record ClientRequest(String fullName, String email, ClientRiskProfile clientRiskProfile) {
}
