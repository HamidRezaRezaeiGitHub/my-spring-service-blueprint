package com.example.application.authentication;

class IdentityAlreadyRegisteredException extends RuntimeException {
    IdentityAlreadyRegisteredException() {
        super("Verified identity is already registered");
    }
}
