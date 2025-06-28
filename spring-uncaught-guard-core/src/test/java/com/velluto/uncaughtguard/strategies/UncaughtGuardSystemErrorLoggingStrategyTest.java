package com.velluto.uncaughtguard.strategies;

import com.velluto.uncaughtguard.models.UncaughtGuardExceptionTrace;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class UncaughtGuardSystemErrorLoggingStrategyTest {
    private UncaughtGuardExceptionTrace trace;
    private final PrintStream originalErr = System.err;
    private ByteArrayOutputStream errContent;

    @BeforeEach
    void setUp() {
        trace = mock(UncaughtGuardExceptionTrace.class);
        errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));
    }

    @Test
    void getLoggableExceptionStackTraceString_returnsStackTrace() throws Exception {
        UncaughtGuardSystemErrorLoggingStrategy strategy = new UncaughtGuardSystemErrorLoggingStrategy();
        RuntimeException ex = new RuntimeException("Test stack trace");
        java.lang.reflect.Method method = UncaughtGuardSystemErrorLoggingStrategy.class.getDeclaredMethod("getLoggableExceptionStackTraceString", RuntimeException.class);
        method.setAccessible(true);
        String stackTrace = (String) method.invoke(strategy, ex);
        assertNotNull(stackTrace);
        assertTrue(stackTrace.contains("Test stack trace"));
        assertTrue(stackTrace.contains("java.lang.RuntimeException"));
    }

    @Test
    void log_writesToSystemErr() {
        when(trace.getTraceId()).thenReturn(java.util.UUID.randomUUID());
        when(trace.getIncidentTimestamp()).thenReturn(java.time.LocalDateTime.now());
        when(trace.getMethod()).thenReturn("GET");
        when(trace.getPath()).thenReturn("/test");
        when(trace.getQueryParams()).thenReturn(java.util.Collections.emptyMap());
        when(trace.getHeaders()).thenReturn(java.util.Collections.emptyMap());
        when(trace.getBody()).thenReturn("");
        when(trace.getException()).thenReturn(new RuntimeException("Test Exception"));

        UncaughtGuardSystemErrorLoggingStrategy strategy = Mockito.spy(new UncaughtGuardSystemErrorLoggingStrategy());
        doReturn("An uncaught exception occurred").when(strategy).getLogErrorMessage();
        strategy.log(trace);
        Mockito.verify(strategy, times(1)).log(trace);
        String output = errContent.toString();
        assertTrue(output.contains("Exception"));
    }

    @AfterEach
    void tearDown() {
        System.setErr(originalErr);
    }
}

