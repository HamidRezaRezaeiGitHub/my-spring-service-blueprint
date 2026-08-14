package com.example.application.authentication;

import com.example.application.authentication.exception.TokenVerificationException;
import com.example.application.error.ErrorResponse;
import com.example.application.error.ResponseFacilitator;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice(basePackageClasses = AuthenticationController.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
class AuthenticationExceptionHandler {

    private final ResponseFacilitator responses;

    AuthenticationExceptionHandler(ResponseFacilitator responses) {
        this.responses = responses;
    }

    @ExceptionHandler(TokenVerificationException.class)
    ResponseEntity<ErrorResponse> rejected(TokenVerificationException exception, HttpServletRequest request) {
        return responses.unauthorized(request, List.of("Bearer token was rejected"));
    }

    @ExceptionHandler(IdentityAlreadyRegisteredException.class)
    ResponseEntity<ErrorResponse> duplicate(IdentityAlreadyRegisteredException exception,
                                            HttpServletRequest request) {
        return responses.conflict(request, List.of(exception.getMessage()));
    }
}
