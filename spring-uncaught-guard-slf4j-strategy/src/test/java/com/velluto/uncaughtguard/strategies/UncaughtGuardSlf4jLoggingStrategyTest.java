package com.velluto.uncaughtguard.strategies;

import com.velluto.uncaughtguard.models.UncaughtGuardExceptionTrace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class UncaughtGuardSlf4jLoggingStrategyTest {
    private UncaughtGuardExceptionTrace trace;
    private UUID mockTraceId;
    private LocalDateTime mockIncidentTimestamp;
    private String mockMethod;
    private String mockPath;
    private Map<String, String> mockQueryParams;
    private Map<String, String> mockHeaders;
    private String mockBody;
    private RuntimeException mockException;

    @BeforeEach
    void setUp() {
        trace = mock(UncaughtGuardExceptionTrace.class);

        mockTraceId = UUID.randomUUID();
        mockIncidentTimestamp = LocalDateTime.now();
        mockMethod = "GET";
        mockPath = "/api/test";
        mockQueryParams = Map.of("param1", "value1", "param2", "value2");
        mockHeaders = Map.of("Header1", "Value1", "Header2", "Value2");
        mockBody = "{\"key\":\"value\"}";
        mockException = new RuntimeException("Test exception");

        when(trace.getTraceId()).thenReturn(mockTraceId);
        when(trace.getIncidentTimestamp()).thenReturn(mockIncidentTimestamp);
        when(trace.getMethod()).thenReturn(mockMethod);
        when(trace.getPath()).thenReturn(mockPath);
        when(trace.getQueryParams()).thenReturn(mockQueryParams);
        when(trace.getHeaders()).thenReturn(mockHeaders);
        when(trace.getBody()).thenReturn(mockBody);
        when(trace.getException()).thenReturn(mockException);
    }

    @Test
    void log_callsSlf4jError() {
        String className = "com.example.MyClass";
        UncaughtGuardSlf4jLoggingStrategy strategy = Mockito.spy(new UncaughtGuardSlf4jLoggingStrategy());
        doReturn(className).when(strategy).getThrowingClassName(trace);
        Logger mockLogger = mock(Logger.class);
        try (MockedStatic<LoggerFactory> loggerFactoryStatic = Mockito.mockStatic(LoggerFactory.class)) {
            loggerFactoryStatic.when(() -> LoggerFactory.getLogger(className)).thenReturn(mockLogger);
            doReturn("Error occurred").when(strategy).getLogErrorMessage();
            strategy.log(trace);
            verify(mockLogger, times(1)).error(
                    anyString(),
                    eq("Error occurred"),
                    eq(mockTraceId),
                    eq(mockIncidentTimestamp),
                    eq(mockMethod),
                    eq(mockPath),
                    eq(mockQueryParams.toString()),
                    eq(mockHeaders.toString()),
                    eq(mockBody),
                    anyString(),
                    eq(mockException)
            );
        }
    }
}
