package com.miguel.wealth_orders.service;

import com.miguel.wealth_orders.controller.dto.OrderPathRequest;
import com.miguel.wealth_orders.controller.dto.OrderRequest;
import com.miguel.wealth_orders.domain.Client;
import com.miguel.wealth_orders.domain.Order;
import com.miguel.wealth_orders.domain.enums.ClientRiskProfile;
import com.miguel.wealth_orders.domain.enums.OrderStatus;
import com.miguel.wealth_orders.domain.enums.OrderType;
import com.miguel.wealth_orders.exception.order.OrderCanNotBeExecutedException;
import com.miguel.wealth_orders.exception.order.OrderNotPendingException;
import com.miguel.wealth_orders.repository.ClientRepository;
import com.miguel.wealth_orders.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("cancel executed order exception")
    void cancelExecutedOrder() {
        Client client = new Client("Ana Silva", "ana@x.pt", ClientRiskProfile.CONSERVATIVE);
        Order order = new Order(client, "US0378331005", OrderType.BUY, BigDecimal.TEN, BigDecimal.ONE);
        ReflectionTestUtils.setField(order, "id", 42L);
        ReflectionTestUtils.setField(order, "orderStatus", OrderStatus.EXECUTED);

        when(orderRepository.findById(42L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancelByOrderId(42L))
                .isInstanceOf(OrderNotPendingException.class)
                .hasMessage("Order %d is %s and cannot be %s.".formatted(order.getId(), order.getOrderStatus(), OrderStatus.CANCELLED.name().toLowerCase()));

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.EXECUTED);
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("conservative validation test")
    void conservativeValidationTest() {
        Client client = new Client("Ana Silva", "ana@x.pt", ClientRiskProfile.CONSERVATIVE);
        OrderRequest orderRequest = new OrderRequest("US0378331005", OrderType.BUY, BigDecimal.valueOf(1000), BigDecimal.valueOf(1000));
        ReflectionTestUtils.setField(client, "id", 1L);

        when(clientRepository.findById(client.getId())).thenReturn(Optional.of(client));

        assertThatThrownBy(() -> orderService.placeOrder(client.getId(), orderRequest))
                .isInstanceOf(OrderCanNotBeExecutedException.class)
                .hasMessage("The client id %d can't create this order.".formatted(client.getId()));

        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("conservative validation test")
    void conservativeValidationTestWithExact100000() {
        Client client = new Client("Ana Silva", "ana@x.pt", ClientRiskProfile.CONSERVATIVE);
        OrderRequest orderRequest = new OrderRequest("US0378331005", OrderType.BUY, BigDecimal.valueOf(100), BigDecimal.valueOf(100));
        ReflectionTestUtils.setField(client, "id", 1L);

        when(clientRepository.findById(client.getId())).thenReturn(Optional.of(client));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order order = orderService.placeOrder(client.getId(), orderRequest);

        verify(orderRepository).save(order);
        assertThat(order.getClient()).isSameAs(client);
        assertThat(order.getLimitPrice()).isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(order.getOrderStatus()).isSameAs(OrderStatus.PENDING);
    }

    @Test
    void patchOrderOverTheLimit() {
        Client client = new Client("Ana Silva", "ana@x.pt", ClientRiskProfile.CONSERVATIVE);
        ReflectionTestUtils.setField(client, "id", 1L);
        Order order = new Order(client, "US0378331005", OrderType.BUY, BigDecimal.valueOf(10), BigDecimal.valueOf(1000));
        ReflectionTestUtils.setField(order, "id", 2L);

        OrderPathRequest orderRequest = new OrderPathRequest(BigDecimal.valueOf(100000), BigDecimal.valueOf(10000000));

        when(orderRepository.findById(2L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.patchOrderByOrderId(order.getId(), orderRequest))
                .isInstanceOf(OrderCanNotBeExecutedException.class)
                .hasMessage("The client id %d can't create this order.".formatted(client.getId()));

        assertThat(order.getQuantity()).isEqualTo(BigDecimal.valueOf(10));
    }

    @Test
    @DisplayName("cancel executed order exception")
    void patchExecutedOrder() {
        Client client = new Client("Ana Silva", "ana@x.pt", ClientRiskProfile.CONSERVATIVE);
        Order order = new Order(client, "US0378331005", OrderType.BUY, BigDecimal.valueOf(10), BigDecimal.valueOf(1000));
        ReflectionTestUtils.setField(order, "id", 42L);
        ReflectionTestUtils.setField(order, "orderStatus", OrderStatus.EXECUTED);

        OrderPathRequest orderRequest = new OrderPathRequest(BigDecimal.valueOf(100000), BigDecimal.valueOf(10000000));

        when(orderRepository.findById(42L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.patchOrderByOrderId(42L, orderRequest))
                .isInstanceOf(OrderNotPendingException.class)
                .hasMessage("Order %d is %s and cannot be %s.".formatted(order.getId(), order.getOrderStatus(), "changed"));

        assertThat(order.getLimitPrice()).isEqualTo(BigDecimal.valueOf(1000));
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("cancel executed order exception")
    void executeExecutedOrder() {
        Client client = new Client("Ana Silva", "ana@x.pt", ClientRiskProfile.CONSERVATIVE);
        Order order = new Order(client, "US0378331005", OrderType.BUY, BigDecimal.valueOf(10), BigDecimal.valueOf(1000));
        ReflectionTestUtils.setField(order, "id", 42L);
        ReflectionTestUtils.setField(order, "orderStatus", OrderStatus.EXECUTED);

        when(orderRepository.findById(42L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.executeOrderByOrderId(42L))
                .isInstanceOf(OrderNotPendingException.class)
                .hasMessage("Order %d is %s and cannot be %s.".formatted(order.getId(), order.getOrderStatus(), OrderStatus.EXECUTED.name().toLowerCase()));

        assertThat(order.getLimitPrice()).isEqualTo(BigDecimal.valueOf(1000));
        verify(orderRepository, never()).save(any());
    }
}
