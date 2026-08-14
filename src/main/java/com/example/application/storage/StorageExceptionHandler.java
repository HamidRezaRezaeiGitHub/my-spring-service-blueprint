package com.example.application.storage;

import com.example.application.error.ErrorResponse;
import com.example.application.error.ResponseFacilitator;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice(basePackageClasses = StorageController.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
class StorageExceptionHandler {

    private final ResponseFacilitator responses;

    StorageExceptionHandler(ResponseFacilitator responses) {
        this.responses = responses;
    }

    @ExceptionHandler(StorageUnavailableException.class)
    ResponseEntity<ErrorResponse> unavailable(StorageUnavailableException exception, HttpServletRequest request) {
        return responses.serviceUnavailable(request, List.of(exception.getMessage()));
    }
}
