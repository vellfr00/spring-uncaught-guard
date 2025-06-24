package com.velluto.uncaughtguard.models;

import java.time.LocalDateTime;
import java.util.UUID;

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
