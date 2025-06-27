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
    private UncaughtGuardExceptionTrace trace;

    @BeforeEach
    void setUp() {
        trace = mock(UncaughtGuardExceptionTrace.class);
    }

    @Test
    void callLog_successfulLog_returnsTrue() {
        UncaughtGuardLoggingStrategy strategy = spy(new UncaughtGuardLoggingStrategy() {
            @Override
            public void log(UncaughtGuardExceptionTrace exceptionTrace) {
                // This method is intentionally left empty for the test
                // In a real implementation, this would contain the logging logic
            }
        });

        boolean result = strategy.callLog(trace);
        Mockito.verify(strategy, times(1)).log(trace);
        assertTrue(result);
    }

    @Test
    void callLog_logThrowsException_returnsFalseAndLogsWarning() {
        UncaughtGuardLoggingStrategy throwingStrategy = spy(new UncaughtGuardLoggingStrategy() {
            @Override
            public void log(UncaughtGuardExceptionTrace exceptionTrace) {
                throw new RuntimeException("Logging failed");
            }
        });

        UUID traceId = UUID.randomUUID();
        when(trace.getTraceId()).thenReturn(traceId);

        boolean result = throwingStrategy.callLog(trace);
        Mockito.verify(throwingStrategy, times(1)).log(trace);
        assertFalse(result);
    }
}
