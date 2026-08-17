package com.example.application.authentication;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserAuthenticationRepository extends JpaRepository<UserAuthentication, UUID> {

    Optional<UserAuthentication> findByProviderAndProviderSubject(String provider, String providerSubject);

    boolean existsByProviderAndProviderSubject(String provider, String providerSubject);
}
