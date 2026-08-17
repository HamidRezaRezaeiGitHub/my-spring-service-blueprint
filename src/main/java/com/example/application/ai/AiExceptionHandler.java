package com.example.application.ai;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.example.application.error.ProblemDetails.create;

@RestControllerAdvice(basePackageClasses = AiController.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
class AiExceptionHandler {

    @ExceptionHandler(AiUnavailableException.class)
    ProblemDetail unavailable(AiUnavailableException exception) {
        return create(HttpStatus.SERVICE_UNAVAILABLE, exception.getMessage());
    }
}
