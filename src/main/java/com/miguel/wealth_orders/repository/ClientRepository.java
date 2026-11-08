package com.miguel.wealth_orders.repository;

import com.miguel.wealth_orders.domain.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {

}
