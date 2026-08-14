package com.example.application.error;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * Converts framework and boundary failures into the blueprint's stable error schema.
 * Feature exceptions should either carry an HTTP status themselves or receive a focused
 * handler beside their owning feature; this advice deliberately contains no domain table.
 */
@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final ResponseFacilitator responses;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException exception,
                                             HttpServletRequest request) {
        List<String> errors = exception.getBindingResult().getAllErrors().stream()
                .map(error -> error instanceof FieldError fieldError
                        ? fieldError.getField() + ": " + error.getDefaultMessage()
                        : requireNonNull(error.getDefaultMessage()))
                .toList();
        return responses.badRequest(request, ResponseErrorType.VALIDATION_ERROR, errors);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    ResponseEntity<ErrorResponse> methodValidation(HandlerMethodValidationException exception,
                                                   HttpServletRequest request) {
        List<String> errors = exception.getAllErrors().stream()
                .map(error -> requireNonNull(error.getDefaultMessage()))
                .toList();
        return responses.badRequest(request, ResponseErrorType.VALIDATION_ERROR, errors);
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            MissingServletRequestParameterException.class,
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class
    })
    ResponseEntity<ErrorResponse> badRequest(Exception exception, HttpServletRequest request) {
        return responses.badRequest(request, List.of(safeMessage(exception, "Malformed request")));
    }

    @ExceptionHandler(AuthenticationException.class)
    ResponseEntity<ErrorResponse> authentication(AuthenticationException exception,
                                                 HttpServletRequest request) {
        log.debug("Authentication rejected: {}", exception.getClass().getSimpleName());
        return responses.unauthorized(request, List.of("Authentication failed"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ErrorResponse> accessDenied(AccessDeniedException exception,
                                               HttpServletRequest request) {
        log.debug("Authorization rejected: {}", exception.getClass().getSimpleName());
        return responses.forbidden(request, List.of("Access denied"));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ErrorResponse> dataConflict(DataIntegrityViolationException exception,
                                               HttpServletRequest request) {
        log.warn("Persistence constraint rejected a request: {}", exception.getClass().getSimpleName());
        return responses.conflict(request, List.of("The requested state conflicts with existing data"));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    ResponseEntity<ErrorResponse> notFound(NoResourceFoundException exception,
                                           HttpServletRequest request) {
        return responses.notFound(request, List.of("Resource not found"));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ResponseEntity<ErrorResponse> methodNotSupported(HttpRequestMethodNotSupportedException exception,
                                                     HttpServletRequest request) {
        return responses.badRequest(request, List.of("HTTP method is not supported for this resource"));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> unexpected(Exception exception, HttpServletRequest request) {
        log.error("Unhandled request failure", exception);
        return responses.internalServerError(request, List.of("An unexpected error occurred"));
    }

    public void handleAuthenticationException(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException exception) throws IOException {
        log.debug("Security filter authentication rejected: {}", exception.getClass().getSimpleName());
        ErrorResponse body = requireNonNull(
                responses.unauthorized(request, List.of("Authentication required")).getBody());
        responses.writeErrorResponse(response, body, UNAUTHORIZED);
    }

    public void handleAccessDeniedException(HttpServletRequest request, HttpServletResponse response,
                                            AccessDeniedException exception) throws IOException {
        log.debug("Security filter authorization rejected: {}", exception.getClass().getSimpleName());
        ErrorResponse body = requireNonNull(responses.forbidden(request, List.of("Access denied")).getBody());
        responses.writeErrorResponse(response, body, FORBIDDEN);
    }

    private static String safeMessage(Exception exception, String fallback) {
        return exception.getMessage() == null || exception.getMessage().isBlank()
                ? fallback
                : exception.getMessage();
    }
}
