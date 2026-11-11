package com.miguel.wealth_orders.service;

import com.miguel.wealth_orders.controller.dto.ClientRequest;
import com.miguel.wealth_orders.domain.Client;
import com.miguel.wealth_orders.domain.enums.OrderStatus;
import com.miguel.wealth_orders.exception.client.ClientNotFoundException;
import com.miguel.wealth_orders.exception.client.ClientVersionPreconditionException;
import com.miguel.wealth_orders.exception.client.ClientWithPendingOrdersException;
import com.miguel.wealth_orders.exception.client.EmailExistsConflictException;
import com.miguel.wealth_orders.repository.ClientRepository;
import com.miguel.wealth_orders.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final OrderRepository orderRepository;

    public ClientService(ClientRepository clientRepository, OrderRepository orderRepository) {
        this.clientRepository = clientRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Client createClient(ClientRequest clientRequest) {
        String clientEmail = clientRequest.email();
        if (this.clientRepository.existsByEmail(clientRequest.email())) {
            throw new EmailExistsConflictException(clientEmail);
        }

        return this.clientRepository.saveAndFlush(
                new Client(clientRequest.fullName(), clientEmail, clientRequest.clientRiskProfile()));
    }

    @Transactional(readOnly = true)
    public Client getClientById(Long clientId) {
        return this.clientRepository.findByIdAndActiveTrue(clientId).orElseThrow(() -> new ClientNotFoundException(clientId));
    }

    @Transactional(readOnly = true)
    public Page<Client> getPageableClientList(Pageable pageable) {
        return this.clientRepository.findAllByActiveTrue(pageable);
    }

    @Transactional
    public Client updateClientInformation(Long clientId, ClientRequest clientRequest, long expectedVersion) {
        Client client = this.getClientById(clientId);

        long clientVersion = client.getVersion();
        if (clientVersion != expectedVersion) {
            throw new ClientVersionPreconditionException(clientId, clientVersion, expectedVersion);
        }

        return this.clientRepository.saveAndFlush(setNewClientInformation(client, clientRequest));
    }

    @Transactional
    public void deleteClient(Long clientId) {
        Client client = this.clientRepository.findByIdAndActiveTrue(clientId).orElseThrow(() -> new ClientNotFoundException(clientId));

        boolean existsOrders = this.orderRepository.existsByClientIdAndOrderStatus(client.getId(), OrderStatus.PENDING);
        if (existsOrders) {
            throw new ClientWithPendingOrdersException(clientId);
        }

        this.clientRepository.delete(client);
    }

    private Client setNewClientInformation(Client client, ClientRequest clientRequest) {
        client.setFullName(clientRequest.fullName());
        client.setEmail(clientRequest.email());
        client.setClientRiskProfile(clientRequest.clientRiskProfile());
        return client;
    }
}
