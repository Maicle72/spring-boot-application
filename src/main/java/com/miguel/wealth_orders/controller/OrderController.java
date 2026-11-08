package com.miguel.wealth_orders.controller;

import com.miguel.wealth_orders.entity.Order;
import com.miguel.wealth_orders.enums.OrderType;
import org.springframework.web.bind.annotation.*;

@RestController("/api/v1")
public class OrderController {

    @PostMapping("/clients/{clientId}/orders")
    public Order createNewOrderForClient(@RequestParam Long id) {
        // | place order | **201** + Location | 400, 404 client, 422 (R3) |
        return null;
    }

    @GetMapping("/orders/{id}")
    public Order getOrderByOrderId(@RequestParam Long id) {
        // | get one order | **200** | 404 |
        return null;
    }

    @GetMapping("clients/{clientId}/orders")
    public Order getClientOrders(@RequestParam OrderType orderType) {
        // | client's orders, optional status filter | **200** | 404 client |
        return null;
    }

    @PatchMapping("orders/{id}")
    public Order patchOrderByOrderId(@RequestParam Long id) {
        // | change quantity and/or limitPrice | **200** | 400, 404, 409 (R2) |
        return null;
    }

    @PostMapping("orders/{id}/cancel")
    public Order cancelOrderByOrderId(@RequestParam Long id) {
        // | cancel (an action, not a CRUD update) | **200** | 404, 409 (R2) |
        return null;
    }

    @PostMapping("orders/{id}/execute")
    public Order executeOrderByOrderId(@RequestParam Long id) {
        // | mark executed | **200** | 404, 409 (R2) |
        return null;
    }
}
