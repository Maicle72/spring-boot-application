package com.miguel.wealth_orders.integration;

import com.jayway.jsonpath.JsonPath;
import com.miguel.wealth_orders.domain.Order;
import com.miguel.wealth_orders.domain.enums.OrderStatus;
import com.miguel.wealth_orders.repository.ClientRepository;
import com.miguel.wealth_orders.repository.OrderRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private OrderRepository orderRepository;

    @AfterEach
    void cleanUp() {
        orderRepository.deleteAll();
        clientRepository.deleteAll();
    }

    private Long createClient(String email) throws Exception {
        MvcResult body = mockMvc.perform(post("/api/v1/clients").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fullName": "Ana Silva", "email": "%s", "clientRiskProfile": "CONSERVATIVE"}
                                """.formatted(email)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.email").value(email.trim().toLowerCase()))
                .andReturn();
        Long clientId = ((Number) JsonPath.read(body.getResponse().getContentAsString(), "$.id")).longValue();

        assertThat(Objects.requireNonNull(body.getResponse().getHeader("Location")).endsWith("/api/v1/clients/" + clientId));

        return clientId;
    }

    private Long createOrder(Long clientId) throws Exception {

        MvcResult body = mockMvc.perform(post("/api/v1/clients/{id}/orders", clientId).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "isin": "US0378331005", "orderType": "BUY", "quantity": 10, "limitPrice": 150.00 }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/1")))
                .andExpect(header().string("ETag", "\"0\""))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.isin").value("US0378331005"))
                .andExpect(jsonPath("$.orderStatus").value(OrderStatus.PENDING.name()))
                .andReturn();

        Long orderId = ((Number) JsonPath.read(body.getResponse().getContentAsString(), "$.id")).longValue();

        assertThat(Objects.requireNonNull(body.getResponse().getHeader("Location")).endsWith("/api/v1/orders/" + orderId));

        return orderId;
    }

    @Test
    void fullFlowCreateClientCreateOrderCancel() throws Exception {
        Long clientId = createClient("ana@x.pt");
        Long orderId = createOrder(clientId);

        mockMvc.perform(post("/api/v1/orders/{id}/cancel", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.orderStatus").value(OrderStatus.CANCELLED.name()));

        assertThat(orderRepository.findById(orderId)).get()
                .extracting(Order::getOrderStatus).isEqualTo(OrderStatus.CANCELLED);

        Order order = this.orderRepository.findById(orderId).orElseThrow();

        mockMvc.perform(post("/api/v1/orders/{id}/cancel", orderId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Conflict"))
                .andExpect(jsonPath("$.detail")
                        .value("Order %d is %s and cannot be cancelled.".formatted(orderId, order.getOrderStatus(), OrderStatus.CANCELLED.name().toLowerCase())));
    }
}
