package com.miguel.wealth_orders.controller;

import com.miguel.wealth_orders.controller.dto.OrderPathRequest;
import com.miguel.wealth_orders.controller.dto.OrderRequest;
import com.miguel.wealth_orders.controller.dto.OrderResponse;
import com.miguel.wealth_orders.domain.Order;
import com.miguel.wealth_orders.domain.enums.OrderStatus;
import com.miguel.wealth_orders.exception.order.OrderPatchInvalidRequestException;
import com.miguel.wealth_orders.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/clients/{id}/orders")
    public ResponseEntity<OrderResponse> createNewOrderForClient(@PathVariable Long id, @Valid @RequestBody OrderRequest orderRequest) {
        Order order = this.orderService.placeOrder(id, orderRequest);

        URI uriLocation = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/orders/{id}").buildAndExpand(order.getId()).toUri();
        return ResponseEntity.created(uriLocation).eTag(String.valueOf(order.getVersion())).body(OrderResponse.from(order));
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderResponse> getOrderByOrderId(@PathVariable Long id) {
        Order order = this.orderService.getOrder(id);
        return ResponseEntity.ok(OrderResponse.from(order));
    }

    @GetMapping("clients/{id}/orders")
    public ResponseEntity<List<OrderResponse>> getClientOrders(@PathVariable Long id,
                                                               @RequestParam(required = false) OrderStatus orderStatus) {
        List<Order> orderList = this.orderService.getClientOrders(id, orderStatus);
        return ResponseEntity.ok(orderList.stream().map(OrderResponse::from).toList());
    }

    @PatchMapping("orders/{id}")
    public ResponseEntity<OrderResponse> patchOrderByOrderId(@PathVariable Long id, @Valid @RequestBody OrderPathRequest orderPathRequest) {
        if (orderPathRequest.limitPrice() == null && orderPathRequest.quantity() == null) {
            throw new OrderPatchInvalidRequestException();
        }

        Order order = this.orderService.patchOrderByOrderId(id, orderPathRequest);
        return ResponseEntity.ok(OrderResponse.from(order));
    }

    @PostMapping("orders/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrderByOrderId(@PathVariable Long id) {
        Order order = this.orderService.cancelByOrderId(id);

        return ResponseEntity.ok(OrderResponse.from(order));
    }

    @PostMapping("orders/{id}/execute")
    public ResponseEntity<OrderResponse> executeOrderByOrderId(@PathVariable Long id) {
        Order order = this.orderService.executeOrderByOrderId(id);

        return ResponseEntity.ok(OrderResponse.from(order));
    }
}
