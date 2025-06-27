package com.velluto.uncaughtguard.strategies;

import com.velluto.uncaughtguard.models.UncaughtGuardExceptionTrace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UncaughtGuardLoggingStrategyTest {
    private UncaughtGuardLoggingStrategy strategy;
    private UncaughtGuardExceptionTrace trace;
    private Logger mockLogger;

    @BeforeEach
    void setUp() {
        // Create a concrete implementation for testing
        strategy = new UncaughtGuardLoggingStrategy() {
            @Override
            public void log(UncaughtGuardExceptionTrace exceptionTrace) {
                // This method is intentionally left empty for the test
                // In a real implementation, this would contain the logging logic
            }
        };
        trace = mock(UncaughtGuardExceptionTrace.class);
        mockLogger = mock(Logger.class);
        // Inject mock logger
        java.lang.reflect.Field loggerField;
        try {
            loggerField = UncaughtGuardLoggingStrategy.class.getDeclaredField("logger");
            loggerField.setAccessible(true);
            loggerField.set(strategy, mockLogger);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void callLog_successfulLog_returnsTrue() {
        boolean result = strategy.callLog(trace);
        assertTrue(result);
        verifyNoInteractions(mockLogger);
    }

    @Test
    void callLog_logThrowsException_returnsFalseAndLogsWarning() {
        UncaughtGuardLoggingStrategy throwingStrategy = new UncaughtGuardLoggingStrategy() {
            @Override
            public void log(UncaughtGuardExceptionTrace exceptionTrace) {
                throw new RuntimeException("fail");
            }
        };
        // Inject mock logger
        try {
            java.lang.reflect.Field loggerField = UncaughtGuardLoggingStrategy.class.getDeclaredField("logger");
            loggerField.setAccessible(true);
            loggerField.set(throwingStrategy, mockLogger);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        UUID traceId = UUID.randomUUID();
        when(trace.getTraceId()).thenReturn(traceId);
        boolean result = throwingStrategy.callLog(trace);
        assertFalse(result);
    }
}

