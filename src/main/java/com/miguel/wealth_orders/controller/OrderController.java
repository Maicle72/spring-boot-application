package com.miguel.wealth_orders.controller;

import com.miguel.wealth_orders.controller.dto.OrderRequest;
import com.miguel.wealth_orders.controller.dto.OrderResponse;
import com.miguel.wealth_orders.domain.enums.OrderStatus;
import com.miguel.wealth_orders.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/api/v1")
public class OrderController {

    private OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/clients/{clientId}/orders")
    public OrderResponse createNewOrderForClient(@RequestParam Long id, @RequestBody OrderRequest orderRequest) {
        // | place order | **201** + Location | 400, 404 client, 422 (R3) |
        var order = this.orderService.createNewOrderForClient(id, orderRequest);
        return OrderResponse.from(order);
    }

    @GetMapping("/orders/{id}")
    public OrderResponse getOrderByOrderId(@RequestParam Long id) {
        // | get one order | **200** | 404 |
        var order = this.orderService.getOrderByOrderId(id);
        return OrderResponse.from(order);
    }

    @GetMapping("clients/{clientId}/orders")
    public List<OrderResponse> getClientOrders(@RequestParam Long clientId, @RequestParam OrderStatus orderStatus) {
        // | client's orders, optional status filter | **200** | 404 client |
        var orderList = this.orderService.getClientOrders(clientId, orderStatus);
        return orderList.stream().map(OrderResponse::from).toList();
    }

    @PatchMapping("orders/{id}")
    public OrderResponse patchOrderByOrderId(@RequestParam Long id, @RequestBody OrderRequest orderRequest) {
        // | change quantity and/or limitPrice | **200** | 400, 404, 409 (R2) |

        var order = this.orderService.patchOrderByOrderId(id, orderRequest);
        return OrderResponse.from(order);
    }

    @PostMapping("orders/{id}/cancel")
    public OrderResponse cancelOrderByOrderId(@RequestParam Long id) {
        // | cancel (an action, not a CRUD update) | **200** | 404, 409 (R2) |
        var order = this.orderService.cancelOrderByOrderId(id);
        return OrderResponse.from(order);
    }

    @PostMapping("orders/{id}/execute")
    public OrderResponse executeOrderByOrderId(@RequestParam Long id) {
        // | mark executed | **200** | 404, 409 (R2) |
        var order = this.orderService.executeOrderByOrderId(id);
        return OrderResponse.from(order);
    }
}
