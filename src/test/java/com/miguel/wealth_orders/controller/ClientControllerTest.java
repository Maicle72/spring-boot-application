package com.miguel.wealth_orders.controller;

import com.miguel.wealth_orders.domain.Client;
import com.miguel.wealth_orders.domain.enums.ClientRiskProfile;
import com.miguel.wealth_orders.exception.client.ClientNotFoundException;
import com.miguel.wealth_orders.exception.client.ClientVersionPreconditionException;
import com.miguel.wealth_orders.exception.client.ClientWithPendingOrdersException;
import com.miguel.wealth_orders.service.ClientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClientController.class)
public class ClientControllerTest {

    @MockitoBean
    private ClientService clientService;

    @Autowired
    private MockMvc mockMvc;

    private Client prepareClientEntity(Long id) {
        Client client = new Client("Ana Silva", "ana@x.pt", ClientRiskProfile.CONSERVATIVE);
        ReflectionTestUtils.setField(client, "id", id);
        ReflectionTestUtils.setField(client, "version", 0L);
        return client;
    }

    @Test
    void createClientReturns201WithLocation() throws Exception {
        when(clientService.createClient(any())).thenReturn(prepareClientEntity(1L));

        mockMvc.perform(post("/api/v1/clients").contentType(MediaType.APPLICATION_JSON).content("""
                        {"fullName": "Ana Silva", "email": "ana@x.pt", "clientRiskProfile": "CONSERVATIVE"}
                        """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/1")))
                .andExpect(header().string("ETag", "\"0\""))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("ana@x.pt"))
                .andExpect(jsonPath("$.clientRiskProfile").value(ClientRiskProfile.CONSERVATIVE.name()));
    }

    @Test
    void getClientByIdReturns404() throws Exception {
        when(clientService.getClientById(any())).thenThrow(new ClientNotFoundException(20L));

        mockMvc.perform(get("/api/v1/clients/{id}", 20L).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail").value("Client id 20 doesn't exist."));
    }

    @Test
    void deleteClientByIdReturns409() throws Exception {
        Client client = prepareClientEntity(20L);
        when(clientService.getClientById(any())).thenReturn(prepareClientEntity(20L));
        doThrow(new ClientWithPendingOrdersException(client.getId())).when(clientService).deleteClient(client.getId());


        mockMvc.perform(delete("/api/v1/clients/{id}", 20L).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Conflict"))
                .andExpect(jsonPath("$.detail").value("The client id 20 has pending orders."));
    }

    @Test
    void createClientReturns400() throws Exception {
        mockMvc.perform(post("/api/v1/clients").contentType(MediaType.APPLICATION_JSON).content("""
                        {}
                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateClientReturn428() throws Exception {
        when(clientService.getClientById(any())).thenReturn(prepareClientEntity(20L));

        mockMvc.perform(put("/api/v1/clients/{id}", 20L).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fullName": "Ana Silva", "email": "ana@x.pt", "clientRiskProfile": "CONSERVATIVE"}
                                """)
                        .header("If-Match", ""))
                .andExpect(status().isPreconditionRequired())
                .andExpect(jsonPath("$.title").value("Precondition Required"))
                .andExpect(jsonPath("$.detail").value("The header If Match is mandatory for this endpoint."));
    }

    @Test
    void updateClientReturn412() throws Exception {
        Client client = prepareClientEntity(20L);
        when(clientService.getClientById(any())).thenReturn(prepareClientEntity(client.getId()));
        when(clientService.updateClientInformation(eq(client.getId()), any(), anyLong())).thenThrow(new ClientVersionPreconditionException(client.getId(), client.getVersion(), 2));

        mockMvc.perform(put("/api/v1/clients/{id}", client.getId()).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fullName": "Ana Silva", "email": "ana@x.pt", "clientRiskProfile": "CONSERVATIVE"}
                                """)
                        .header("If-Match", "2"))
                .andExpect(status().isPreconditionFailed())
                .andExpect(jsonPath("$.title").value("Precondition Failed"))
                .andExpect(jsonPath("$.detail").value("The client id %d has version value %d but received %d".formatted(client.getId(), client.getVersion(), 2)));
    }
}
