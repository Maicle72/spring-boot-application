package com.miguel.wealth_orders.controller;

import com.miguel.wealth_orders.controller.dto.ClientRequest;
import com.miguel.wealth_orders.controller.dto.ClientResponse;
import com.miguel.wealth_orders.domain.Client;
import com.miguel.wealth_orders.exception.client.ClientBadRequestException;
import com.miguel.wealth_orders.exception.client.IfMatchPreconditionRequiredException;
import com.miguel.wealth_orders.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping("/clients")
    public ResponseEntity<ClientResponse> createClient(@Valid @RequestBody ClientRequest clientRequest) {
        Client client = this.clientService.createClient(clientRequest);

        URI uriLocation = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{id}").buildAndExpand(client.getId()).toUri();
        return ResponseEntity.created(uriLocation).eTag(String.valueOf(client.getVersion())).body(ClientResponse.from(client));
    }

    @GetMapping("/clients/{id}")
    public ResponseEntity<ClientResponse> getClientById(@PathVariable Long id) {
        Client client = this.clientService.getClientById(id);
        return ResponseEntity.ok(ClientResponse.from(client));
    }

    @GetMapping("/clients")
    public ResponseEntity<PagedModel<ClientResponse>> getClientList(@PageableDefault(size = 20, sort = "fullName") Pageable pageable) {
        return ResponseEntity.ok(new PagedModel<>(this.clientService.getPageableClientList(pageable).map(ClientResponse::from)));
    }

    @PutMapping("/clients/{id}")
    public ResponseEntity<ClientResponse> updateClientInformation(@PathVariable Long id,
                                                                  @RequestHeader(HttpHeaders.IF_MATCH) String ifMatch,
                                                                  @Valid @RequestBody ClientRequest clientRequest) {
        if (ifMatch == null || ifMatch.isBlank()) {
            throw new IfMatchPreconditionRequiredException();
        }

        long expectedVersion;
        try {
            expectedVersion = Long.parseLong(ifMatch.replaceAll("^W/|\"", "").trim());
        } catch (NumberFormatException e) {
            throw new ClientBadRequestException();
        }

        Client client = this.clientService.updateClientInformation(id, clientRequest, expectedVersion);

        return ResponseEntity.ok().eTag(String.valueOf(client.getVersion())).body(ClientResponse.from(client));
    }

    @DeleteMapping("/clients/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        this.clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
}
