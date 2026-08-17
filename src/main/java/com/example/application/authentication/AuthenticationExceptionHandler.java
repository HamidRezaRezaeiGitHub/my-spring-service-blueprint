package com.example.application.authentication;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.example.application.error.ProblemDetails.create;

@RestControllerAdvice(basePackageClasses = AuthenticationController.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
class AuthenticationExceptionHandler {

    @ExceptionHandler(TokenVerificationException.class)
    ProblemDetail rejected(TokenVerificationException exception) {
        return create(HttpStatus.UNAUTHORIZED, "Bearer token was rejected");
    }

    @ExceptionHandler(IdentityAlreadyRegisteredException.class)
    ProblemDetail duplicate(IdentityAlreadyRegisteredException exception) {
        return create(HttpStatus.CONFLICT, exception.getMessage());
    }
}
