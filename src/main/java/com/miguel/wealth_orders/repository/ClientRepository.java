package com.miguel.wealth_orders.repository;

import com.miguel.wealth_orders.domain.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    boolean existsByEmail(String email);

    List<Client> findAllByActiveTrue();

    Page<Client> findAllByActiveTrue(Pageable pageable);

    Optional<Client> findByIdAndActiveTrue(Long id);
}
