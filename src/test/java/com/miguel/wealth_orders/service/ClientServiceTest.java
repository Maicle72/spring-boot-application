package com.miguel.wealth_orders.service;

import com.miguel.wealth_orders.controller.dto.ClientRequest;
import com.miguel.wealth_orders.domain.Client;
import com.miguel.wealth_orders.domain.Order;
import com.miguel.wealth_orders.domain.enums.ClientRiskProfile;
import com.miguel.wealth_orders.domain.enums.OrderStatus;
import com.miguel.wealth_orders.domain.enums.OrderType;
import com.miguel.wealth_orders.exception.client.ClientWithPendingOrdersException;
import com.miguel.wealth_orders.exception.client.EmailExistsConflictException;
import com.miguel.wealth_orders.repository.ClientRepository;
import com.miguel.wealth_orders.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private ClientService clientService;

    @Test
    @DisplayName("creates a new client")
    void createClient() {
        // given
        ClientRequest clientRequest = new ClientRequest("nome nomeDois", "nome@gmail.com", ClientRiskProfile.AGGRESSIVE);
        when(clientRepository.existsByEmail("nome@gmail.com")).thenReturn(false);
        when(clientRepository.saveAndFlush(any(Client.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        Client result = clientService.createClient(clientRequest);

        // then
        assertThat(result.getEmail()).isEqualTo("nome@gmail.com");
        assertThat(result.getFullName()).isEqualTo("nome nomeDois");

        ArgumentCaptor<Client> captor = ArgumentCaptor.forClass(Client.class);
        verify(clientRepository).saveAndFlush(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo("nome@gmail.com");
    }

    @Test
    @DisplayName("reject a creation with duplicated email")
    void duplicatedEmail() {
        // given
        ClientRequest clientRequest = new ClientRequest("três quatro", "nome@gmail.com", ClientRiskProfile.BALANCED);
        when(clientRepository.existsByEmail("nome@gmail.com")).thenReturn(true);

        // when
        assertThatThrownBy(() -> clientService.createClient(clientRequest)).isInstanceOf(EmailExistsConflictException.class)
                .hasMessage("The email %s is already being used.".formatted(clientRequest.email()));

        // then
        verify(clientRepository, never()).saveAndFlush(any());
    }

    @Test
    void deleteClientWithPendingOrders() {
        Client client = new Client("nome nomeDois", "nome@gmail.com", ClientRiskProfile.BALANCED);
        ReflectionTestUtils.setField(client, "id", 42L);
        ReflectionTestUtils.setField(client, "active", true);
        when(clientRepository.findByIdAndActiveTrue(client.getId())).thenReturn(Optional.of(client));

        Order order = new Order(client, "US0378331005", OrderType.BUY, BigDecimal.TEN, BigDecimal.ONE);
        ReflectionTestUtils.setField(order, "id", 42L);
        ReflectionTestUtils.setField(order, "orderStatus", OrderStatus.PENDING);
        when(orderRepository.existsByClientIdAndOrderStatus(client.getId(), OrderStatus.PENDING)).thenReturn(true);

        assertThatThrownBy(() -> clientService.deleteClient(client.getId()))
                .isInstanceOf(ClientWithPendingOrdersException.class)
                .hasMessage("The client id %d has pending orders.".formatted(client.getId()));
    }

    @Test
    void deleteClientWithExecutedOrders() {
        Client client = new Client("nome nomeDois", "nome@gmail.com", ClientRiskProfile.BALANCED);
        ReflectionTestUtils.setField(client, "id", 42L);
        ReflectionTestUtils.setField(client, "active", true);
        when(clientRepository.findByIdAndActiveTrue(client.getId())).thenReturn(Optional.of(client));
        when(orderRepository.existsByClientIdAndOrderStatus(client.getId(), OrderStatus.PENDING)).thenReturn(false);

        clientService.deleteClient(client.getId());

        verify(clientRepository).delete(client);
    }
}
