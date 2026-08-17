package com.example.application.authentication;

import com.example.application.account.Account;
import com.example.application.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

@Entity
@Table(
        name = "user_authentications",
        uniqueConstraints = @UniqueConstraint(name = "uk_user_authentication_provider_subject", columnNames = {"provider", "provider_subject"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserAuthentication extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Nullable
    private UUID id;

    @Column(nullable = false, length = 32)
    private String provider;

    @Column(name = "provider_subject", nullable = false, length = 256)
    private String providerSubject;

    @Column(length = 320)
    private @Nullable String email;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    public UserAuthentication(String provider, String providerSubject, @Nullable String email, Account account) {
        this.provider = provider;
        this.providerSubject = providerSubject;
        this.email = email;
        this.account = account;
    }
}
