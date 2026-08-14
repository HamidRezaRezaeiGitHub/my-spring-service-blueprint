package com.example.application.account;

import com.example.application.error.ErrorResponse;
import com.example.application.error.ResponseFacilitator;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice(basePackageClasses = AccountController.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
class AccountExceptionHandler {

    private final ResponseFacilitator responses;

    AccountExceptionHandler(ResponseFacilitator responses) {
        this.responses = responses;
    }

    @ExceptionHandler(AccountNotFoundException.class)
    ResponseEntity<ErrorResponse> notFound(AccountNotFoundException exception, HttpServletRequest request) {
        return responses.notFound(request, List.of(exception.getMessage()));
    }
}
