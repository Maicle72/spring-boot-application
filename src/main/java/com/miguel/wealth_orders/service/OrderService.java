package com.miguel.wealth_orders.service;

import com.miguel.wealth_orders.controller.dto.OrderRequest;
import com.miguel.wealth_orders.domain.Order;
import com.miguel.wealth_orders.domain.enums.OrderStatus;
import com.miguel.wealth_orders.repository.ClientRepository;
import com.miguel.wealth_orders.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private OrderRepository orderRepository;
    private ClientRepository clientRepository;

    public OrderService(OrderRepository orderRepository, ClientRepository clientRepository) {
        this.orderRepository = orderRepository;
        this.clientRepository = clientRepository;
    }

    public Order createNewOrderForClient(Long id, OrderRequest orderRequest) {
        var clientFetched = this.clientRepository.getReferenceById(id);

        var orderCreated = this.orderRepository.saveAndFlush(new Order(clientFetched, orderRequest.isin(), orderRequest.orderType(), orderRequest.quantity(), orderRequest.limitPrice(), orderRequest.orderStatus()));
        return orderCreated;
    }

    public Order getOrderByOrderId(Long id) {
        var orderFetched = this.orderRepository.getReferenceById(id);
        return orderFetched;
    }

    public List<Order> getClientOrders(Long clientId, OrderStatus orderStatus) {
        var clientFetched = this.clientRepository.getReferenceById(clientId);
        var clientOrderList = this.orderRepository.getClientOrdersByClientIdAndOrderStatus(clientFetched, orderStatus);
        return clientOrderList;
    }

    public Order patchOrderByOrderId(Long id, OrderRequest orderRequest) {
        var orderFetched = this.orderRepository.getReferenceById(id);

        setNewOrderInformation(orderFetched, orderRequest.orderStatus());

        return orderFetched;
    }

    public Order cancelOrderByOrderId(Long id) {
        var orderFetched = this.orderRepository.getReferenceById(id);

        setNewOrderInformation(orderFetched, OrderStatus.CANCELLED);
        return orderFetched;
    }

    public Order executeOrderByOrderId(Long id) {
        var orderFetched = this.orderRepository.getReferenceById(id);

        setNewOrderInformation(orderFetched, OrderStatus.EXECUTED);
        return orderFetched;
    }

    private void setNewOrderInformation(Order order, OrderStatus orderStatus) {
        order.setOrderStatus(orderStatus);
    }

}
