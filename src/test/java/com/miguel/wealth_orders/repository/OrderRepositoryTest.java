package com.miguel.wealth_orders.repository;

import com.miguel.wealth_orders.config.JpaConfig;
import com.miguel.wealth_orders.domain.Client;
import com.miguel.wealth_orders.domain.Order;
import com.miguel.wealth_orders.domain.enums.ClientRiskProfile;
import com.miguel.wealth_orders.domain.enums.OrderStatus;
import com.miguel.wealth_orders.domain.enums.OrderType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConfig.class)
public class OrderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderRepository orderRepository;

    private Client ana;
    private Client rui;

    @BeforeEach
    void setUp() {
        ana = entityManager.persist(new Client("Ana Silva", "ana@x.pt", ClientRiskProfile.BALANCED));
        rui = entityManager.persist(new Client("Rui Costa", "rui@x.pt", ClientRiskProfile.CONSERVATIVE));
    }

    private Order order(Client client, OrderStatus status) {
        Order order = new Order(client, "US0378331005", OrderType.BUY,
                BigDecimal.TEN, new BigDecimal("150.00"));
        ReflectionTestUtils.setField(order, "orderStatus", status);
        return entityManager.persist(order);
    }

    @Test
    void returnsOnlyOrdersOfThatClientWithThatStatus() {
        Order a = order(ana, OrderStatus.PENDING);
        Order b = order(ana, OrderStatus.PENDING);
        order(ana, OrderStatus.EXECUTED);
        order(rui, OrderStatus.PENDING);

        entityManager.flush();
        entityManager.clear();

        List<Order> orderList = this.orderRepository.findByClientIdAndOrderStatus(ana.getId(), OrderStatus.PENDING);

        assertThat(orderList).extracting(Order::getId).containsExactlyInAnyOrder(a.getId(), b.getId());
    }

    @Test
    void returnsEmptyListOrderOfThatClientWithStatus() {
        boolean result = this.orderRepository.existsByClientIdAndOrderStatus(ana.getId(), OrderStatus.PENDING);

        assertThat(result).isEqualTo(false);
    }

    @Test
    void returnsListOrderOfThatClientWithStatus() {
        Order a = order(ana, OrderStatus.PENDING);

        entityManager.flush();
        entityManager.clear();

        boolean result = this.orderRepository.existsByClientIdAndOrderStatus(ana.getId(), OrderStatus.PENDING);

        assertThat(result).isEqualTo(true);
    }
}
