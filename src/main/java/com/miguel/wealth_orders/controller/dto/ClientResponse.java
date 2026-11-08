package com.miguel.wealth_orders.controller.dto;

import com.miguel.wealth_orders.domain.Client;
import com.miguel.wealth_orders.domain.enums.ClientRiskProfile;

public record ClientResponse(Long id, String fullName, String email, ClientRiskProfile clientRiskProfile) {
    public static ClientResponse from(Client client) {
        return new ClientResponse(client.getId(), client.getFullName(), client.getEmail(), client.getRiskProfile());
    }
}
