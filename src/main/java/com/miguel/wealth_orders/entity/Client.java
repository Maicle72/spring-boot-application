package com.miguel.wealth_orders.entity;

import com.miguel.wealth_orders.enums.ClientRiskProfile;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "T_CLIENT")
public class Client extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @NotBlank
    @Email(regexp = "^[^@\\s]+@[^@\\s+\\.[^@\\s]+$")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "risk_profile", nullable = false)
    private ClientRiskProfile riskProfile;

    public Client() {
    }

    public Client(String fullName, String email, ClientRiskProfile riskProfile) {
        this.fullName = fullName;
        this.email = email;
        this.riskProfile = riskProfile;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ClientRiskProfile getRiskProfile() {
        return riskProfile;
    }

    public void setRiskProfile(ClientRiskProfile riskProfile) {
        this.riskProfile = riskProfile;
    }
}
