package com.velluto.uncaughtguard.strategies;

import com.velluto.uncaughtguard.models.UncaughtGuardExceptionTrace;
import com.velluto.uncaughtguard.properties.UncaughtGuardProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class UncaughtGuardLoggingStrategyTest {
    private UncaughtGuardExceptionTrace trace;

    @Mock
    private UncaughtGuardProperties properties;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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

    @Test
    void getLogErrorMessage_returnsConfiguredMessage() {
        String expectedMessage = "Test error message";
        when(properties.getLogErrorMessage()).thenReturn(expectedMessage);
        UncaughtGuardLoggingStrategy strategy = new UncaughtGuardLoggingStrategy() {
            @Override
            protected void log(UncaughtGuardExceptionTrace exceptionTrace) {
            }
        };
        // Inject mock properties
        java.lang.reflect.Field field;
        try {
            field = UncaughtGuardLoggingStrategy.class.getDeclaredField("properties");
            field.setAccessible(true);
            field.set(strategy, properties);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String result = strategy.getLogErrorMessage();
        org.junit.jupiter.api.Assertions.assertEquals(expectedMessage, result);
    }

    @Test
    void getThrowingClassName_returnsClassNameFromStackTrace() {
        RuntimeException ex = new RuntimeException();
        StackTraceElement element = new StackTraceElement("com.example.MyClass", "myMethod", "MyClass.java", 123);
        ex.setStackTrace(new StackTraceElement[]{element});
        when(trace.getException()).thenReturn(ex);
        UncaughtGuardLoggingStrategy strategy = new UncaughtGuardLoggingStrategy() {
            @Override
            protected void log(UncaughtGuardExceptionTrace exceptionTrace) {
            }
        };
        String result = strategy.getThrowingClassName(trace);
        org.junit.jupiter.api.Assertions.assertEquals("com.example.MyClass", result);
    }

    @Test
    void getThrowingClassName_returnsCurrentClassIfNull() {
        when(trace.getException()).thenReturn(null);
        UncaughtGuardLoggingStrategy strategy = new UncaughtGuardLoggingStrategy() {
            @Override
            protected void log(UncaughtGuardExceptionTrace exceptionTrace) {
            }
        };
        String result = strategy.getThrowingClassName(trace);
        org.junit.jupiter.api.Assertions.assertEquals(strategy.getClass().getName(), result);
    }

    @Test
    void getThrowingMethodName_returnsMethodNameFromStackTrace() {
        RuntimeException ex = new RuntimeException();
        StackTraceElement element = new StackTraceElement("com.example.MyClass", "myMethod", "MyClass.java", 123);
        ex.setStackTrace(new StackTraceElement[]{element});
        when(trace.getException()).thenReturn(ex);
        UncaughtGuardLoggingStrategy strategy = new UncaughtGuardLoggingStrategy() {
            @Override
            protected void log(UncaughtGuardExceptionTrace exceptionTrace) {
            }
        };
        String result = strategy.getThrowingMethodName(trace);
        org.junit.jupiter.api.Assertions.assertEquals("myMethod", result);
    }

    @Test
    void getThrowingMethodName_returnsLogIfNull() {
        when(trace.getException()).thenReturn(null);
        UncaughtGuardLoggingStrategy strategy = new UncaughtGuardLoggingStrategy() {
            @Override
            protected void log(UncaughtGuardExceptionTrace exceptionTrace) {
            }
        };
        String result = strategy.getThrowingMethodName(trace);
        org.junit.jupiter.api.Assertions.assertEquals("log", result);
    }
}
