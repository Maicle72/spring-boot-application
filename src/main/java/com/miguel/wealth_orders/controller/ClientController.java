package com.miguel.wealth_orders.controller;

import com.miguel.wealth_orders.controller.dto.ClientRequest;
import com.miguel.wealth_orders.controller.dto.ClientResponse;
import com.miguel.wealth_orders.service.ClientService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.*;

@RestController("/api/v1")
public class ClientController {

    private ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping("/clients")
    public ClientResponse createClient(@RequestBody ClientRequest clientRequest) {
        // create client | **201** + `Location: /api/v1/clients/{id}` + body | 400 validation, 409 duplicate email |

        var client = this.clientService.createClient(clientRequest);
        return ClientResponse.from(client);
    }

    @GetMapping("/clients/{id}")
    public ClientResponse getClientById(@RequestParam Long clientId) {
        // get one | **200** | 404 |

        var client = this.clientService.getClientById(clientId);
        return ClientResponse.from(client);
    }

    @GetMapping("/clients")
    public PagedModel<ClientResponse> getClientList(@PageableDefault(size = 20, sort = "fullName") Pageable pageable) {
        return new PagedModel<>(this.clientService.getPageableClientList(pageable).map(ClientResponse::from));
    }

    @PutMapping("/clients/{id}")
    public ClientResponse updateClientInformation(@RequestParam Long id, @RequestBody ClientRequest clientRequest) {
        // full update | **200** | 400, 404, 409

        var client = this.clientService.updateClientInformation(id, clientRequest);
        return ClientResponse.from(client);
    }

    @DeleteMapping("/clients/{id}")
    public void deleteClient(@RequestParam Long id) {
        // | delete | **204** no body | 404, 409 (R4) |

        this.clientService.deleteClient(id);
    }


}
