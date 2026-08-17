package com.example.application.account;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.example.application.error.ProblemDetails.create;

@RestControllerAdvice(basePackageClasses = AccountController.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
class AccountExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    ProblemDetail notFound(AccountNotFoundException exception) {
        return create(HttpStatus.NOT_FOUND, exception.getMessage());
    }
}
