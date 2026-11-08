package com.miguel.wealth_orders.controller;

import com.miguel.wealth_orders.dto.ClientResponse;
import com.miguel.wealth_orders.entity.Client;
import com.miguel.wealth_orders.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/api/v1")
public class ClientController {

    @PostMapping("/clients")
    public Client createClient() {
        // create client | **201** + `Location: /api/v1/clients/{id}` + body | 400 validation, 409 duplicate email |
        return null;
    }

    @GetMapping("/clients/{id}")
    public Client getClientById(@RequestParam Long clientId) {
        // get one | **200** | 404 |
        return null;
    }

    @GetMapping("/clients")
    public PagedModel<ClientResponse> getClientList(@PageableDefault(size = 20, sort = "fullName") Pageable pageable) {
        return new PagedModel<>(null);
    }

    @PutMapping("/clients/{id}")
    public Client updateClientInformation(@RequestParam Long id) {
        // full update | **200** | 400, 404, 409

        return null;
    }

    @DeleteMapping("/clients/{id}")
    public Client deleteClient(@RequestParam Long id) {
        // | delete | **204** no body | 404, 409 (R4) |

        return null;
    }



}
