package com.example.application.observability;

import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.Nullable;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Locale;

import static org.springframework.util.StringUtils.hasText;

/**
 * Encapsulates Cloud Logging-specific MDC enrichment for request-scoped logs.
 */
@Component
public class CloudLoggingContextEnricher {

    private static final String TRACEPARENT_HEADER = "traceparent";
    private static final String LEGACY_TRACE_HEADER = "X-Cloud-Trace-Context";
    public static final String GCP_TRACE_MDC_KEY = "logging.googleapis.com/trace";
    public static final String GCP_SPAN_ID_MDC_KEY = "logging.googleapis.com/spanId";

    @Value("${app.observability.gcp-project-id:}")
    private String configuredGcpProjectId;

    public void applyTraceContext(HttpServletRequest request) {
        String gcpProjectId = resolveGcpProjectId();
        TraceContext traceContext = resolveTraceContext(request);

        if (!hasText(gcpProjectId) || traceContext == null) {
            return;
        }

        MDC.put(GCP_TRACE_MDC_KEY, "projects/" + gcpProjectId + "/traces/" + traceContext.traceId());
        if (hasText(traceContext.spanId())) {
            MDC.put(GCP_SPAN_ID_MDC_KEY, traceContext.spanId());
        }
    }

    public void clearTraceContext() {
        MDC.remove(GCP_TRACE_MDC_KEY);
        MDC.remove(GCP_SPAN_ID_MDC_KEY);
    }

    private String resolveGcpProjectId() {
        if (hasText(configuredGcpProjectId)) {
            return configuredGcpProjectId;
        }

        for (String envVar : new String[]{"GOOGLE_CLOUD_PROJECT", "GCP_PROJECT", "GCLOUD_PROJECT"}) {
            String value = System.getenv(envVar);
            if (hasText(value)) {
                return value;
            }
        }

        return "";
    }

    private @Nullable TraceContext resolveTraceContext(HttpServletRequest request) {
        TraceContext traceparentContext = parseTraceparentHeader(request.getHeader(TRACEPARENT_HEADER));
        if (traceparentContext != null) {
            return traceparentContext;
        }

        return parseLegacyTraceHeader(request.getHeader(LEGACY_TRACE_HEADER));
    }

    private @Nullable TraceContext parseTraceparentHeader(@Nullable String traceparentHeader) {
        if (!hasText(traceparentHeader)) {
            return null;
        }

        String[] parts = traceparentHeader.split("-");
        if (parts.length != 4 || !isHex(parts[1], 32) || !isHex(parts[2], 16) || !isHex(parts[3], 2)) {
            return null;
        }

        return new TraceContext(parts[1].toLowerCase(Locale.ROOT), parts[2].toLowerCase(Locale.ROOT));
    }

    private @Nullable TraceContext parseLegacyTraceHeader(@Nullable String legacyTraceHeader) {
        if (!hasText(legacyTraceHeader)) {
            return null;
        }

        String traceSegment = legacyTraceHeader.split(";", 2)[0];
        String[] parts = traceSegment.split("/", 2);
        if (parts.length != 2 || !isHex(parts[0], 32)) {
            return null;
        }

        try {
            long spanId = Long.parseUnsignedLong(parts[1]);
            return new TraceContext(parts[0].toLowerCase(Locale.ROOT), String.format(Locale.ROOT, "%016x", spanId));
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isHex(String value, int expectedLength) {
        if (value.length() != expectedLength) {
            return false;
        }

        for (int index = 0; index < value.length(); index++) {
            if (Character.digit(value.charAt(index), 16) < 0) {
                return false;
            }
        }

        return true;
    }

    private record TraceContext(String traceId, String spanId) {
    }
}