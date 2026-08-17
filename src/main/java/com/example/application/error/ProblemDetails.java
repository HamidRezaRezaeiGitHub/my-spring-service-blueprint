package com.example.application.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.util.List;

/**
 * Small factory for the application's RFC 9457 responses.
 */
public final class ProblemDetails {

    private ProblemDetails() {
    }

    public static ProblemDetail create(HttpStatus status, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(status.getReasonPhrase());
        return problem;
    }

    public static ProblemDetail validation(List<String> errors) {
        ProblemDetail problem = create(HttpStatus.BAD_REQUEST, "Request validation failed");
        problem.setProperty("errors", List.copyOf(errors));
        return problem;
    }
}
