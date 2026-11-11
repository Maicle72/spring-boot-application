package com.miguel.wealth_orders.repository;

import com.miguel.wealth_orders.config.JpaConfig;
import com.miguel.wealth_orders.domain.Client;
import com.miguel.wealth_orders.domain.enums.ClientRiskProfile;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(JpaConfig.class)
public class ClientRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ClientRepository clientRepository;

    private Client ana;
    private Client rui;

    @BeforeEach
    void setUp() {
        ana = entityManager.persist(new Client("Ana Silva", "ana@x.pt", ClientRiskProfile.BALANCED));
        rui = entityManager.persist(new Client("Rui Costa", "rui@x.pt", ClientRiskProfile.CONSERVATIVE));
    }

    @Test
    void returnsEmailAlreadyBeingUsedConstraint() {
        entityManager.flush();
        entityManager.clear();

        Client carlos = new Client("Carlos Garrafão", "ana@x.pt", ClientRiskProfile.BALANCED);
        assertThatThrownBy(() -> {
            this.clientRepository.save(carlos);
            entityManager.flush();
        }).isInstanceOf(ConstraintViolationException.class);
    }
}
