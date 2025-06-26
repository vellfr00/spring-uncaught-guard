package com.velluto.uncaughtguard.advices;

import com.velluto.uncaughtguard.models.UncaughtGuardExceptionTrace;
import com.velluto.uncaughtguard.models.UncaughtGuardExceptionTraceHttpResponseDTO;
import com.velluto.uncaughtguard.properties.UncaughtGuardProperties;
import com.velluto.uncaughtguard.strategies.UncaughtGuardLoggingStrategy;
import com.velluto.uncaughtguard.strategies.UncaughtGuardSystemErrorLoggingStrategy;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UncaughtGuardRestControllerAdviceTest {
    private ApplicationContext context;
    private UncaughtGuardProperties properties;
    private UncaughtGuardRestControllerAdvice advice;
    private HttpServletRequest request;
    private UncaughtGuardLoggingStrategy loggingStrategy;

    @BeforeEach
    void setUp() {
        context = mock(ApplicationContext.class);
        properties = mock(UncaughtGuardProperties.class);
        request = mock(HttpServletRequest.class);

        // fill request with necessary mock data
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/test");
        when(request.getQueryString()).thenReturn("param1=value1&param2=value2");
        when(request.getHeaderNames()).thenReturn(java.util.Collections.enumeration(java.util.Collections.singletonList("Content-Type")));
        when(request.getHeader("Content-Type")).thenReturn("application/json");

        loggingStrategy = mock(UncaughtGuardSystemErrorLoggingStrategy.class);
        when(properties.getLoggingStrategies()).thenReturn(new Class[]{ UncaughtGuardSystemErrorLoggingStrategy.class });
        when(context.getBean(UncaughtGuardSystemErrorLoggingStrategy.class)).thenReturn((UncaughtGuardSystemErrorLoggingStrategy) loggingStrategy);

        advice = new UncaughtGuardRestControllerAdvice(context, properties);
    }

    @Test
    void returns500AndDtoWhenExceptionNotExcluded() {
        when(properties.getExcludedExceptions()).thenReturn(new Class[]{});
        when(properties.isEnableLogRequestBody()).thenReturn(false);
        when(properties.isKeepThrowingExceptions()).thenReturn(false);
        when(properties.getHttpResponseErrorMessage()).thenReturn("Generic error");

        RuntimeException ex = new RuntimeException("Test");
        ResponseEntity<UncaughtGuardExceptionTraceHttpResponseDTO> response = advice.handleUncaughtExceptions(ex, request);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        UncaughtGuardExceptionTraceHttpResponseDTO body = response.getBody();
        assertEquals("Generic error", body.getErrorMessage());
        verify(loggingStrategy, times(1)).log(any(UncaughtGuardExceptionTrace.class));
    }

    @Test
    void throwsExceptionIfExcluded() {
        when(properties.getExcludedExceptions()).thenReturn(new Class[]{NullPointerException.class});
        when(properties.isEnableLogRequestBody()).thenReturn(false);
        NullPointerException ex = new NullPointerException("Test runtime exception");
        assertThrows(NullPointerException.class, () -> advice.handleUncaughtExceptions(ex, request));
    }

    @Test
    void throwsExceptionIfKeepThrowingExceptionsTrue() {
        when(properties.getExcludedExceptions()).thenReturn(new Class[]{});
        when(properties.isEnableLogRequestBody()).thenReturn(false);
        when(properties.isKeepThrowingExceptions()).thenReturn(true);
        RuntimeException ex = new RuntimeException("Test");
        assertThrows(RuntimeException.class, () -> advice.handleUncaughtExceptions(ex, request));
        verify(loggingStrategy, times(1)).log(any(UncaughtGuardExceptionTrace.class));
    }

    @Test
    void loggingStrategyIsInvoked() {
        when(properties.getExcludedExceptions()).thenReturn(new Class[]{});
        when(properties.isEnableLogRequestBody()).thenReturn(false);
        when(properties.isKeepThrowingExceptions()).thenReturn(false);
        when(properties.getHttpResponseErrorMessage()).thenReturn("Errore");
        RuntimeException ex = new RuntimeException("Test");
        advice.handleUncaughtExceptions(ex, request);
        verify(loggingStrategy, times(1)).log(any(UncaughtGuardExceptionTrace.class));
    }
}
