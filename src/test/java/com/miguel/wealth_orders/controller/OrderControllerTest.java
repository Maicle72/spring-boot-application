package com.miguel.wealth_orders.controller;

import com.miguel.wealth_orders.controller.dto.OrderRequest;
import com.miguel.wealth_orders.domain.Client;
import com.miguel.wealth_orders.domain.enums.ClientRiskProfile;
import com.miguel.wealth_orders.exception.order.OrderCanNotBeExecutedException;
import com.miguel.wealth_orders.service.ClientService;
import com.miguel.wealth_orders.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @MockitoBean
    private OrderService orderService;

    @Autowired
    private MockMvc mockMvc;

    private Client prepareClientEntity(Long id) {
        Client client = new Client("Ana Silva", "ana@x.pt", ClientRiskProfile.CONSERVATIVE);
        ReflectionTestUtils.setField(client, "id", id);
        return client;
    }

    @Test
    void createOrderReturns400() throws Exception {
        mockMvc.perform(post("/api/v1/clients/{id}/orders", 20L).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "isin": "!!!!US0378331005", "orderType": "BUY", "quantity": 10, "limitPrice": 150.00 }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"));
    }

    @Test
    void createOrderReturn422() throws Exception {
        when(orderService.placeOrder(eq(1L), any(OrderRequest.class))).thenThrow(new OrderCanNotBeExecutedException(1L));

        mockMvc.perform(post("/api/v1/clients/{id}/orders", 1L).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "isin": "US0378331005", "orderType": "BUY", "quantity": 1000000, "limitPrice": 150.00 }
                                """))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.title").value("Unprocessable Content"));
    }
}
