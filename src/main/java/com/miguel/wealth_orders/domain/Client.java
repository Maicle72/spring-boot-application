package com.miguel.wealth_orders.domain;

import com.miguel.wealth_orders.domain.enums.ClientRiskProfile;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "T_CLIENT")
@SQLDelete(sql = "UPDATE T_CLIENT SET active = false where id = ? and version = ?")
public class Client extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @NotBlank
    @Email(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "risk_profile", nullable = false)
    private ClientRiskProfile clientRiskProfile;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    public Client() {
    }

    public Client(String fullName, String email, ClientRiskProfile clientRiskProfile) {
        this.fullName = fullName;
        this.email = email;
        this.clientRiskProfile = clientRiskProfile;
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

    public ClientRiskProfile getClientRiskProfile() {
        return clientRiskProfile;
    }

    public void setClientRiskProfile(ClientRiskProfile clientRiskProfile) {
        this.clientRiskProfile = clientRiskProfile;
    }

    public Long getVersion() {
        return version;
    }

    public boolean isActive() {
        return active;
    }
}
