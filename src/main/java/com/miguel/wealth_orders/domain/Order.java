package com.miguel.wealth_orders.domain;

import com.miguel.wealth_orders.domain.enums.ClientRiskProfile;
import com.miguel.wealth_orders.domain.enums.OrderStatus;
import com.miguel.wealth_orders.domain.enums.OrderType;
import com.miguel.wealth_orders.exception.order.InvalidRequestOrderQuantityAndLimitException;
import com.miguel.wealth_orders.exception.order.OrderCanNotBeExecutedException;
import com.miguel.wealth_orders.exception.order.OrderNotPendingException;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Entity
@Table(name = "T_ORDER")
public class Order extends Auditable {

    private static final BigDecimal MAX_RISK_LIMIT = BigDecimal.valueOf(100000);

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @NotBlank
    @Pattern(regexp = "[A-Z]{2}[A-Z0-9]{9}[0-9]")
    @Column(name = "isin", nullable = false)
    private String isin;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "order_type", nullable = false)
    private OrderType orderType;

    @NotNull
    @Positive
    @Digits(integer = 19, fraction = 4)
    @Column(name = "quantity", nullable = false, precision = 19, scale = 4)
    private BigDecimal quantity;

    @NotNull
    @Positive
    @Digits(integer = 19, fraction = 4)
    @Column(name = "limit_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal limitPrice;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus = OrderStatus.PENDING;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    public Order() {
    }

    public Order(Client client, String isin, OrderType orderType, BigDecimal quantity, BigDecimal limitPrice) {
        assertWithinRiskLimit(client, quantity, limitPrice);
        this.client = client;
        this.isin = isin;
        this.orderType = orderType;
        this.quantity = quantity;
        this.limitPrice = limitPrice;
    }

    private void assertWithinRiskLimit(Client client, BigDecimal quantity, BigDecimal limitPrice) {
        if (quantity == null || limitPrice == null) {
            throw new InvalidRequestOrderQuantityAndLimitException();
        }

        BigDecimal total = quantity.multiply(limitPrice);
        if (ClientRiskProfile.CONSERVATIVE.equals(client.getClientRiskProfile()) && total.compareTo(MAX_RISK_LIMIT) > 0) {
            throw new OrderCanNotBeExecutedException(client.getId());
        }
    }

    public void setNewOrderInformation(BigDecimal quantity, BigDecimal limitPrice) {
        if (OrderStatus.PENDING != this.orderStatus) {
            throw new OrderNotPendingException(this.getId(), orderStatus, "changed");
        }

        BigDecimal finalQuantity = quantity != null ? quantity : this.quantity;
        BigDecimal finalLimitPrice = limitPrice != null ? limitPrice : this.limitPrice;

        assertWithinRiskLimit(client, finalQuantity, finalLimitPrice);

        this.quantity = finalQuantity;
        this.limitPrice = finalLimitPrice;
    }

    public void setNewOrderStatus(OrderStatus newOrderStatus) {
        if (!this.getOrderStatus().canTransactionTo(newOrderStatus)) {
            throw new OrderNotPendingException(this.getId(), this.getOrderStatus(), newOrderStatus.name().toLowerCase());
        }

        this.orderStatus = newOrderStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getLimitPrice() {
        return limitPrice;
    }

    public void setLimitPrice(BigDecimal limitPrice) {
        this.limitPrice = limitPrice;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Long getVersion() {
        return version;
    }
}
