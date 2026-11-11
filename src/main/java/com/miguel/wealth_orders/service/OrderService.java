package com.miguel.wealth_orders.service;

import com.miguel.wealth_orders.controller.dto.OrderPathRequest;
import com.miguel.wealth_orders.controller.dto.OrderRequest;
import com.miguel.wealth_orders.domain.Client;
import com.miguel.wealth_orders.domain.Order;
import com.miguel.wealth_orders.domain.enums.OrderStatus;
import com.miguel.wealth_orders.exception.client.ClientNotFoundException;
import com.miguel.wealth_orders.exception.order.OrderNotFoundException;
import com.miguel.wealth_orders.repository.ClientRepository;
import com.miguel.wealth_orders.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;

    public OrderService(OrderRepository orderRepository, ClientRepository clientRepository) {
        this.orderRepository = orderRepository;
        this.clientRepository = clientRepository;
    }

    @Transactional
    public Order placeOrder(Long clientId, OrderRequest orderRequest) {
        Client client = this.clientRepository.findById(clientId).orElseThrow(() -> new ClientNotFoundException(clientId));

        return this.orderRepository.save(new Order(client, orderRequest.isin(), orderRequest.orderType(),
                orderRequest.quantity(), orderRequest.limitPrice()));
    }

    @Transactional(readOnly = true)
    public Order getOrder(Long orderId) {
        return this.orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    @Transactional(readOnly = true)
    public List<Order> getClientOrders(Long clientId, OrderStatus orderStatus) {
        if (!this.clientRepository.existsById(clientId)) {
            throw new ClientNotFoundException(clientId);
        }

        return Objects.isNull(orderStatus) ? this.orderRepository.findByClientId(clientId)
                : this.orderRepository.findByClientIdAndOrderStatus(clientId, orderStatus);
    }

    @Transactional
    public Order patchOrderByOrderId(Long orderId, OrderPathRequest orderRequest) {
        Order order = this.getOrder(orderId);

        order.setNewOrderInformation(orderRequest.quantity(), orderRequest.limitPrice());

        this.orderRepository.save(order);
        return order;
    }

    @Transactional
    public Order cancelByOrderId(Long orderId) {
        Order order = this.getOrder(orderId);

        order.setNewOrderStatus(OrderStatus.CANCELLED);
        return order;
    }

    @Transactional
    public Order executeOrderByOrderId(Long orderId) {
        Order order = this.getOrder(orderId);

        order.setNewOrderStatus(OrderStatus.EXECUTED);
        return order;
    }

}
