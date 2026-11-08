package com.miguel.wealth_orders.entity;

import com.miguel.wealth_orders.enums.OrderStatus;
import com.miguel.wealth_orders.enums.OrderType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Entity
@Table(name = "T_ORDER")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Column(name = "client_id", nullable = false)
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
    @Column(name = "quantity", nullable = false)
    private BigDecimal quantity;

    @NotNull
    @Positive
    @Digits(integer = 10, fraction = 2)
    @Column(name = "limit_price", nullable = false, scale = 2)
    private BigDecimal limitPrice;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus = OrderStatus.PENDING;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    public Order() {}

    public Order(Client client, String isin, OrderType orderType, BigDecimal quantity, BigDecimal limitPrice, OrderStatus orderStatus, Long version) {
        this.client = client;
        this.isin = isin;
        this.orderType = orderType;
        this.quantity = quantity;
        this.limitPrice = limitPrice;
        this.orderStatus = orderStatus;
        this.version = version;
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
