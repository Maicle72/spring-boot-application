package com.miguel.wealth_orders.repository;

import com.miguel.wealth_orders.domain.Client;
import com.miguel.wealth_orders.domain.Order;
import com.miguel.wealth_orders.domain.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> getClientOrdersByClientIdAndOrderStatus(Client client, OrderStatus orderStatus);
}
