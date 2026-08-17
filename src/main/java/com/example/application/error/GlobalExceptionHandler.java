package com.example.application.error;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

import static com.example.application.error.ProblemDetails.create;

/**
 * Renders framework and cross-cutting failures as RFC 9457 problem details.
 * Feature-owned exceptions retain focused advice in their owning packages.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected @Nullable ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        List<String> errors = exception.getBindingResult().getAllErrors().stream()
                .map(error -> error instanceof FieldError fieldError
                        ? fieldError.getField() + ": " + safeValidationMessage(error.getDefaultMessage())
                        : safeValidationMessage(error.getDefaultMessage()))
                .toList();
        return handleExceptionInternal(exception, ProblemDetails.validation(errors), headers, status, request);
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleHandlerMethodValidationException(
            HandlerMethodValidationException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        List<String> errors = exception.getAllErrors().stream()
                .map(error -> safeValidationMessage(error.getDefaultMessage()))
                .toList();
        return handleExceptionInternal(exception, ProblemDetails.validation(errors), headers, status, request);
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        ProblemDetail problemDetail = create(HttpStatus.BAD_REQUEST, "Request body is malformed or unreadable");
        return handleExceptionInternal(exception, problemDetail, headers, status, request);
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleServletRequestBindingException(
            ServletRequestBindingException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        ProblemDetail problemDetail = create(HttpStatus.BAD_REQUEST, "A required request value is missing or invalid");
        return handleExceptionInternal(exception, problemDetail, headers, status, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail badRequest(IllegalArgumentException exception) {
        return create(HttpStatus.BAD_REQUEST, safeMessage(exception, "Request is invalid"));
    }

    @ExceptionHandler(AuthenticationException.class)
    ProblemDetail authentication(AuthenticationException exception) {
        log.debug("Authentication rejected: {}", exception.getClass().getSimpleName());
        return create(HttpStatus.UNAUTHORIZED, "Authentication is required or was rejected");
    }

    @ExceptionHandler(AccessDeniedException.class)
    ProblemDetail accessDenied(AccessDeniedException exception) {
        log.debug("Authorization rejected: {}", exception.getClass().getSimpleName());
        return create(HttpStatus.FORBIDDEN, "Access is denied");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ProblemDetail dataConflict(DataIntegrityViolationException exception) {
        log.warn("Persistence constraint rejected a request: {}", exception.getClass().getSimpleName());
        return create(HttpStatus.CONFLICT, "The requested state conflicts with existing data");
    }

    @ExceptionHandler(Exception.class)
    ProblemDetail unexpected(Exception exception) {
        log.error("Unhandled request failure", exception);
        return create(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }

    private static String safeValidationMessage(@Nullable String message) {
        return message == null || message.isBlank() ? "Invalid value" : message;
    }

    @SuppressWarnings("SameParameterValue")
    private static String safeMessage(Exception exception, String fallback) {
        return exception.getMessage() == null || exception.getMessage().isBlank()
                ? fallback
                : exception.getMessage();
    }
}
