package com.example.application.ai;

import com.example.application.error.ErrorResponse;
import com.example.application.error.ResponseFacilitator;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice(basePackageClasses = AiController.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
class AiExceptionHandler {

    private final ResponseFacilitator responses;

    AiExceptionHandler(ResponseFacilitator responses) {
        this.responses = responses;
    }

    @ExceptionHandler(AiUnavailableException.class)
    ResponseEntity<ErrorResponse> unavailable(AiUnavailableException exception, HttpServletRequest request) {
        return responses.serviceUnavailable(request, List.of(exception.getMessage()));
    }
}
