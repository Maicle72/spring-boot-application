package com.miguel.wealth_orders.repository;

import com.miguel.wealth_orders.domain.Order;
import com.miguel.wealth_orders.domain.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByClientId(Long clientId);

    List<Order> findByClientIdAndOrderStatus(Long clientId, OrderStatus orderStatus);

    boolean existsByClientIdAndOrderStatus(Long clientId, OrderStatus orderStatus);
}
