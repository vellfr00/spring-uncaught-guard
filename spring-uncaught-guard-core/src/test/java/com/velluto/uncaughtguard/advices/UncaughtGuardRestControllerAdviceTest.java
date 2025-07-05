package com.velluto.uncaughtguard.advices;

import com.velluto.uncaughtguard.loggers.UncaughtGuardAsyncLogger;
import com.velluto.uncaughtguard.models.UncaughtGuardExceptionTrace;
import com.velluto.uncaughtguard.models.UncaughtGuardExceptionTraceHttpResponseDTO;
import com.velluto.uncaughtguard.properties.UncaughtGuardProperties;
import com.velluto.uncaughtguard.utils.UncaughtGuardExceptionUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UncaughtGuardRestControllerAdviceTest {
    private UncaughtGuardRestControllerAdvice advice;
    private UncaughtGuardProperties properties;
    private UncaughtGuardAsyncLogger asyncLogger;
    private HttpServletRequest request;
    private RuntimeException exception;
    private UncaughtGuardExceptionUtils exceptionUtils;

    @BeforeEach
    void setUp() {
        properties = mock(UncaughtGuardProperties.class);
        asyncLogger = mock(UncaughtGuardAsyncLogger.class);
        request = mock(HttpServletRequest.class);
        exception = new RuntimeException("Test exception");
        exceptionUtils = mock(UncaughtGuardExceptionUtils.class);
        advice = new UncaughtGuardRestControllerAdvice();

        // fill request with necessary mock data
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/test");
        when(request.getQueryString()).thenReturn("param1=value1&param2=value2");
        when(request.getHeaderNames()).thenReturn(java.util.Collections.enumeration(java.util.Collections.singletonList("Content-Type")));
        when(request.getHeader("Content-Type")).thenReturn("application/json");

        try {
            var propField = UncaughtGuardRestControllerAdvice.class.getDeclaredField("properties");
            propField.setAccessible(true);
            propField.set(advice, properties);
            var loggerField = UncaughtGuardRestControllerAdvice.class.getDeclaredField("asyncLogger");
            loggerField.setAccessible(true);
            loggerField.set(advice, asyncLogger);
            var utilsField = UncaughtGuardRestControllerAdvice.class.getDeclaredField("exceptionUtils");
            utilsField.setAccessible(true);
            utilsField.set(advice, exceptionUtils);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testHandleUncaughtExceptions_logsAndReturnsResponse() {
        when(exceptionUtils.isExceptionExcluded(exception)).thenReturn(false);
        when(properties.isEnableLogRequestBody()).thenReturn(false);
        when(properties.isKeepThrowingExceptions()).thenReturn(false);
        when(properties.getHttpResponseErrorMessage()).thenReturn("Errore generico");

        ResponseEntity<UncaughtGuardExceptionTraceHttpResponseDTO> response = advice.handleUncaughtExceptions(exception, request);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(asyncLogger, times(1)).logExceptionTraceAsync(any(UncaughtGuardExceptionTrace.class));
    }

    @Test
    void testHandleUncaughtExceptions_excludedExceptionIsRethrown() {
        when(exceptionUtils.isExceptionExcluded(exception)).thenReturn(true);
        when(properties.isEnableLogRequestBody()).thenReturn(false);
        when(properties.isKeepThrowingExceptions()).thenReturn(false);
        assertThrows(RuntimeException.class, () -> advice.handleUncaughtExceptions(exception, request));
        verify(asyncLogger, never()).logExceptionTraceAsync(any());
    }

    @Test
    void testHandleUncaughtExceptions_keepThrowingExceptions() {
        when(exceptionUtils.isExceptionExcluded(exception)).thenReturn(false);
        when(properties.isEnableLogRequestBody()).thenReturn(false);
        when(properties.isKeepThrowingExceptions()).thenReturn(true);
        assertThrows(RuntimeException.class, () -> advice.handleUncaughtExceptions(exception, request));
        verify(asyncLogger, times(1)).logExceptionTraceAsync(any(UncaughtGuardExceptionTrace.class));
    }
}
