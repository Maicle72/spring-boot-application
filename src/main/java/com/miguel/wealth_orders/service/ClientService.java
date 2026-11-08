package com.miguel.wealth_orders.service;

import com.miguel.wealth_orders.controller.dto.ClientRequest;
import com.miguel.wealth_orders.domain.Client;
import com.miguel.wealth_orders.repository.ClientRepository;
import com.miguel.wealth_orders.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientService {

    private ClientRepository clientRepository;
    private OrderRepository orderRepository;

    public ClientService(ClientRepository clientRepository, OrderRepository orderRepository) {
        this.clientRepository = clientRepository;
        this.orderRepository = orderRepository;
    }

    public Client createClient(ClientRequest clientRequest) {
        var clientFlushed = clientRepository.saveAndFlush(new Client(clientRequest.fullName(), clientRequest.email(), clientRequest.clientRiskProfile()));

        return clientFlushed;
    }

    public Client getClientById(Long id) {
        var client = clientRepository.getReferenceById(id);
        return client;
    }

    @Transactional(readOnly = true)
    public Page<Client> getPageableClientList(Pageable pageable) {
        return this.clientRepository.findAll(pageable);
    }

    public Client updateClientInformation(Long id, ClientRequest clientRequest) {
        var clientFetched = this.clientRepository.getReferenceById(id);

        return this.clientRepository.saveAndFlush(setNewClientInformation(clientFetched, clientRequest));
    }

    public void deleteClient(Long id) {
        var clientFetched = this.clientRepository.getReferenceById(id);
        this.clientRepository.delete(clientFetched);
    }


    private Client setNewClientInformation(Client client, ClientRequest clientRequest) {
        client.setFullName(clientRequest.fullName());
        client.setEmail(clientRequest.email());
        client.setRiskProfile(clientRequest.clientRiskProfile());
        return client;
    }


}
