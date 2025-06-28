package com.velluto.uncaughtguard.strategies;

import com.velluto.uncaughtguard.models.UncaughtGuardExceptionTrace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UncaughtGuardJavaLoggerLoggingStrategyTest {
    private UncaughtGuardExceptionTrace trace;
    private RuntimeException testException;

    @BeforeEach
    void setUp() {
        trace = mock(UncaughtGuardExceptionTrace.class);
        testException = new RuntimeException("Test exception");

        when(trace.getTraceId()).thenReturn(UUID.randomUUID());
        when(trace.getIncidentTimestamp()).thenReturn(java.time.LocalDateTime.now());
        when(trace.getMethod()).thenReturn("GET");
        when(trace.getPath()).thenReturn("/test");
        when(trace.getQueryParams()).thenReturn(java.util.Collections.emptyMap());
        when(trace.getHeaders()).thenReturn(java.util.Collections.emptyMap());
        when(trace.getBody()).thenReturn("");
        when(trace.getException()).thenReturn(testException);
    }

    @Test
    void log_callsLoggerLogp() {
        String className = "com.example.MyClass";
        String methodName = "myMethod";
        UncaughtGuardJavaLoggerLoggingStrategy strategy = Mockito.spy(new UncaughtGuardJavaLoggerLoggingStrategy());
        doReturn(className).when(strategy).getThrowingClassName(trace);
        doReturn(methodName).when(strategy).getThrowingMethodName(trace);

        Logger mockLogger = mock(Logger.class);
        try (MockedStatic<Logger> loggerStatic = Mockito.mockStatic(Logger.class)) {
            loggerStatic.when(() -> Logger.getLogger(className)).thenReturn(mockLogger);
            doReturn("An uncaught exception occurred").when(strategy).getLogErrorMessage();
            strategy.log(trace);
            verify(mockLogger, times(1)).logp(eq(Level.SEVERE), eq(className), eq(methodName), anyString(), eq(testException));
        }
    }
}

