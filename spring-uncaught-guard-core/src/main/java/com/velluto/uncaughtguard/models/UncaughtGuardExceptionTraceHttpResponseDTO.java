package com.velluto.uncaughtguard.models;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * This class represents the HTTP response DTO for an uncaught exception trace in the Uncaught Guard framework.
 * It contains the incident timestamp, a unique trace ID, and an error message.
 * It is the HTTP response body that will be sent back to the client when an uncaught exception occurs.
 */
public class UncaughtGuardExceptionTraceHttpResponseDTO {
    private final LocalDateTime incidentTimestamp;
    private final UUID traceId;
    private final String errorMessage;

    public UncaughtGuardExceptionTraceHttpResponseDTO(LocalDateTime incidentTimestamp, UUID traceId, String errorMessage) {
        this.incidentTimestamp = incidentTimestamp;
        this.traceId = traceId;
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public LocalDateTime getIncidentTimestamp() {
        return incidentTimestamp;
    }

    public UUID getTraceId() {
        return traceId;
    }
}
