package com.velluto.uncaughtguard.loggers;

import com.velluto.uncaughtguard.models.UncaughtGuardExceptionTrace;
import com.velluto.uncaughtguard.properties.UncaughtGuardProperties;
import com.velluto.uncaughtguard.strategies.UncaughtGuardLoggingStrategy;
import com.velluto.uncaughtguard.strategies.UncaughtGuardSystemErrorLoggingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.AsyncResult;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UncaughtGuardAsyncLoggerTest {
    private ApplicationContext context;
    private UncaughtGuardProperties properties;
    private UncaughtGuardAsyncLogger logger;
    private UncaughtGuardExceptionTrace trace;

    @BeforeEach
    void setUp() {
        context = mock(ApplicationContext.class);
        properties = mock(UncaughtGuardProperties.class);
        logger = new UncaughtGuardAsyncLogger();
        trace = mock(UncaughtGuardExceptionTrace.class);

        // inject mocks via reflection
        try {
            var ctxField = UncaughtGuardAsyncLogger.class.getDeclaredField("context");
            ctxField.setAccessible(true);
            ctxField.set(logger, context);
            var propField = UncaughtGuardAsyncLogger.class.getDeclaredField("properties");
            propField.setAccessible(true);
            propField.set(logger, properties);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testAllStrategiesAreCalled() {
        UncaughtGuardTestLoggingStrategy strategy = mock(UncaughtGuardTestLoggingStrategy.class);
        when(properties.getLoggingStrategies()).thenReturn(new Class[]{strategy.getClass()});
        when(context.getBean(UncaughtGuardTestLoggingStrategy.class)).thenReturn(strategy);
        when(strategy.callLog(trace)).thenReturn(true);

        logger.logExceptionTraceAsync(trace);

        verify(strategy, times(1)).callLog(trace);
        verify(context, never()).getBean(UncaughtGuardSystemErrorLoggingStrategy.class);
    }

    @Test
    void testFallbackStrategyIsCalled() {
        UncaughtGuardTestLoggingStrategy strategy = mock(UncaughtGuardTestLoggingStrategy.class);
        when(properties.getLoggingStrategies()).thenReturn(new Class[]{strategy.getClass()});
        when(context.getBean(UncaughtGuardTestLoggingStrategy.class)).thenReturn(strategy);
        when(strategy.callLog(trace)).thenReturn(false);

        UncaughtGuardSystemErrorLoggingStrategy fallbackStrategy = mock(UncaughtGuardSystemErrorLoggingStrategy.class);
        when(context.getBean(UncaughtGuardSystemErrorLoggingStrategy.class)).thenReturn(fallbackStrategy);

        logger.logExceptionTraceAsync(trace);

        verify(strategy, times(1)).callLog(trace);
        verify(fallbackStrategy, times(1)).callLog(trace);
    }

    @Test
    void testAsyncAnnotationPresent() throws NoSuchMethodException {
        assertNotNull(UncaughtGuardAsyncLogger.class.getMethod("logExceptionTraceAsync", UncaughtGuardExceptionTrace.class)
                .getAnnotation(org.springframework.scheduling.annotation.Async.class));
    }

    private static class UncaughtGuardTestLoggingStrategy extends UncaughtGuardLoggingStrategy {
        @Override
        public void log(UncaughtGuardExceptionTrace trace) {
            // This is a test implementation, no actual logging
        }
    }
}

