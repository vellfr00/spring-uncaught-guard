package com.velluto.uncaughtguard.models;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class UncaughtGuardExceptionTrace {
    private static final Logger logger = Logger.getLogger(UncaughtGuardExceptionTrace.class.getName());

    private final LocalDateTime incidentTimestamp;
    private final UUID traceId;
    private final String method;
    private final String path;
    private final Map<String, String> queryParams;
    private final Map<String, String> headers;
    private final String body;
    private final RuntimeException exception;

    public UncaughtGuardExceptionTrace(HttpServletRequest request, RuntimeException exception, boolean isEnableLogRequestBody) {
        this.incidentTimestamp = LocalDateTime.now();
        this.traceId = UUID.randomUUID();
        this.method = request.getMethod();
        this.path = request.getRequestURI();
        this.queryParams = getQueryParamsFromRequest(request);
        this.headers = getHeadersFromRequest(request);
        this.body = getBodyFromRequest(request, isEnableLogRequestBody);
        this.exception = exception;
    }

    private Map<String, String> getQueryParamsFromRequest(HttpServletRequest request) {
        Map<String, String> queryParams = new HashMap<>();

        String query = request.getQueryString();
        String[] nameValueParams = query.split("&");

        for (String nameValueParam : nameValueParams) {
            String[] splitNameValueParam = nameValueParam.split("=");
            String name = splitNameValueParam[0];
            String value = Arrays.stream(splitNameValueParam).skip(1).collect(Collectors.joining("="));
            queryParams.put(name, value);
        }

        return queryParams;
    }

    private Map<String, String> getHeadersFromRequest(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            String value = request.getHeader(name);
            headers.put(name, value);
        }

        return headers;
    }

    private String readRequestBody(ContentCachingRequestWrapper wrappedRequest) {
        byte[] cached = wrappedRequest.getContentAsByteArray();

        // body can be never read yet or actually empty
        if (cached.length == 0) {
            try {
                wrappedRequest.getInputStream().readAllBytes();
            } catch (IOException e) {
                logger.log(Level.WARNING, "Error reading body from the request that did throw unhandled exception with assigned traceId: " + traceId, e);
            }
        }

        // at this point we are sure the body was read, if this returns nothing then the body is actually empty
        return wrappedRequest.getContentAsString();
    }

    private String getBodyFromRequest(HttpServletRequest request, boolean isEnableLogRequestBody) {
        if (!isEnableLogRequestBody || !(request instanceof ContentCachingRequestWrapper wrappedRequest))
            return "";

        return readRequestBody(wrappedRequest);
    }

    public LocalDateTime getIncidentTimestamp() {
        return incidentTimestamp;
    }

    public UUID getTraceId() {
        return traceId;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public RuntimeException getException() {
        return exception;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public UncaughtGuardExceptionTraceHttpResponseDTO getHttpResponseDTO(String httpResponseErrorMessage) {
        return new UncaughtGuardExceptionTraceHttpResponseDTO(incidentTimestamp, traceId, httpResponseErrorMessage);
    }
}
