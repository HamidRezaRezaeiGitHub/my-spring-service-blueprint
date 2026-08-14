package com.example.application.error;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import static com.example.application.error.ResponseErrorType.*;
import static org.springframework.http.HttpStatus.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class ResponseFacilitator {

    private final ObjectMapper objectMapper;

    // =========================
    // Error Response Builders
    // =========================

    public ResponseEntity<ErrorResponse> unauthorized(HttpServletRequest request, List<String> errors) {
        ErrorResponse errorResponse = createErrorResponse(UNAUTHORIZED, AUTHENTICATION_REQUIRED, errors, request);

        return ResponseEntity.status(UNAUTHORIZED).body(errorResponse);
    }

    public ResponseEntity<ErrorResponse> forbidden(HttpServletRequest request, List<String> errors) {
        ErrorResponse errorResponse = createErrorResponse(FORBIDDEN, ACCESS_DENIED, errors, request);

        return ResponseEntity.status(FORBIDDEN).body(errorResponse);
    }

    public ResponseEntity<ErrorResponse> badRequest(HttpServletRequest request, List<String> errors) {
        return badRequest(request, BAD_REQUEST_ERROR, errors);
    }

    public ResponseEntity<ErrorResponse> badRequest(HttpServletRequest request, ResponseErrorType errorType, List<String> errors) {
        ErrorResponse errorResponse = createErrorResponse(BAD_REQUEST, errorType, errors, request);
        return ResponseEntity.badRequest().body(errorResponse);
    }

    public ResponseEntity<ErrorResponse> conflict(HttpServletRequest request, List<String> errors) {
        ErrorResponse errorResponse = createErrorResponse(HttpStatus.CONFLICT, CONFLICT_ERROR, errors, request);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    public ResponseEntity<ErrorResponse> notFound(HttpServletRequest request, List<String> errors) {
        ErrorResponse errorResponse = createErrorResponse(NOT_FOUND, NOT_FOUND_ERROR, errors, request);
        return ResponseEntity.status(NOT_FOUND).body(errorResponse);
    }

    public ResponseEntity<ErrorResponse> internalServerError(HttpServletRequest request, List<String> errors) {
        ErrorResponse errorResponse = createErrorResponse(INTERNAL_SERVER_ERROR, INTERNAL_ERROR, errors, request);
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    public ResponseEntity<ErrorResponse> serviceUnavailable(HttpServletRequest request, List<String> errors) {
        ErrorResponse errorResponse = createErrorResponse(HttpStatus.SERVICE_UNAVAILABLE,
                ResponseErrorType.SERVICE_UNAVAILABLE, errors, request);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }


    /**
     * Creates a standard error response using default message from error type
     */
    public ErrorResponse createErrorResponse(HttpStatus status, ResponseErrorType errorType, List<String> errors,
                                             HttpServletRequest request) {
        return createErrorResponse(status, errorType.getDefaultMessage(), errors, request, errorType);
    }

    /**
     * Creates a standard error response with custom message
     */
    public ErrorResponse createErrorResponse(HttpStatus status, String message, List<String> errors, HttpServletRequest request,
                                             ResponseErrorType errorType) {
        return ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(status.toString())
                .message(message)
                .errors(errors)
                .path(request.getRequestURI())
                .method(HttpMethod.valueOf(request.getMethod()))
                .errorType(errorType)
                .build();
    }

    /**
     * Writes error response to HTTP response for security handlers
     */
    public void writeErrorResponse(HttpServletResponse response, ErrorResponse errorResponse, HttpStatus status) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
    }

    // =========================
    // Message Response Builders
    // =========================

    public ResponseEntity<MessageResponse> message(HttpStatus status, String message) {
        MessageResponse messageResponse = MessageResponse.builder()
                .timestamp(Instant.now())
                .success(status.is2xxSuccessful())
                .status(status.toString())
                .message(message)
                .build();

        return ResponseEntity.status(status).body(messageResponse);
    }

    public ResponseEntity<MessageResponse> ok(String message) {
        return message(HttpStatus.OK, message);
    }
}
