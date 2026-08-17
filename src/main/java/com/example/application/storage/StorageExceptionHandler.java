package com.example.application.storage;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.example.application.error.ProblemDetails.create;

@RestControllerAdvice(basePackageClasses = StorageController.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
class StorageExceptionHandler {

    @ExceptionHandler(StorageUnavailableException.class)
    ProblemDetail unavailable(StorageUnavailableException exception) {
        return create(HttpStatus.SERVICE_UNAVAILABLE, exception.getMessage());
    }

    @ExceptionHandler(StoredFileNotFoundException.class)
    ProblemDetail notFound(StoredFileNotFoundException exception) {
        return create(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(StoredObjectNotAvailableException.class)
    ProblemDetail objectNotAvailable(StoredObjectNotAvailableException exception) {
        return create(HttpStatus.CONFLICT, exception.getMessage());
    }
}
