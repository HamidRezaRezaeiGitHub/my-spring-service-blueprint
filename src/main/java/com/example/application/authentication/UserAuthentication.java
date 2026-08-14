package com.example.application.authentication;

import com.example.application.account.Account;
import com.example.application.persistence.UpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "user_authentications", uniqueConstraints =
        @UniqueConstraint(name = "uk_user_authentication_provider_subject", columnNames = {"provider", "provider_subject"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserAuthentication extends UpdatableEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 32)
    private String provider;

    @Column(name = "provider_subject", nullable = false, length = 256)
    private String providerSubject;

    @Column(length = 320)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    public UserAuthentication(String provider, String providerSubject, String email, Account account) {
        this.id = UUID.randomUUID();
        this.provider = provider;
        this.providerSubject = providerSubject;
        this.email = email;
        this.account = account;
    }
}
