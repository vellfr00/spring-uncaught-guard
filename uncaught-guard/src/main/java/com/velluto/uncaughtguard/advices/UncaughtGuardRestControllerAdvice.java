package com.velluto.uncaughtguard.advices;

import com.velluto.uncaughtguard.models.UncaughtGuardExceptionTrace;
import com.velluto.uncaughtguard.models.UncaughtGuardExceptionTraceHttpResponseDTO;
import com.velluto.uncaughtguard.properties.UncaughtGuardProperties;
import com.velluto.uncaughtguard.strategies.UncaughtGuardLoggingStrategy;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

@RestControllerAdvice
public class UncaughtGuardRestControllerAdvice {
    private static final Logger logger = LoggerFactory.getLogger(UncaughtGuardRestControllerAdvice.class);
    private final ApplicationContext context;
    private final UncaughtGuardProperties properties;

    public UncaughtGuardRestControllerAdvice(ApplicationContext context, UncaughtGuardProperties properties) {
        this.context = context;
        this.properties = properties;
    }

    private void logExceptionTrace(UncaughtGuardExceptionTrace trace) {
        for (Class<? extends UncaughtGuardLoggingStrategy> loggingStrategy : properties.getLoggingStrategies()) {
            logger.debug("Logging exception trace with assigned Trace ID: {} using specified logging strategy {}", trace.getTraceId(), loggingStrategy.getSimpleName());
            context.getBean(loggingStrategy).log(trace);
        }
    }

    private boolean isExceptionExcluded(RuntimeException e) {
        logger.debug("Checking if exception of type {} is excluded from handling", e.getClass().getSimpleName());
        boolean isExcluded = Arrays.stream(properties.getExcludedExceptions()).anyMatch(excluded -> excluded.equals(e.getClass()));

        if(isExcluded)
            logger.debug("Exception of type {} is specified to be excluded from handling, it will be thrown again", e.getClass().getSimpleName());

        return isExcluded;
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<UncaughtGuardExceptionTraceHttpResponseDTO> handleUncaughtExceptions(RuntimeException e, HttpServletRequest request) throws RuntimeException {
        UncaughtGuardExceptionTrace trace = new UncaughtGuardExceptionTrace(request, e, properties.isEnableLogRequestBody());
        logger.debug("Caught an unhandled exception of type {}, assigned Trace ID: {}", e.getClass().getSimpleName(), trace.getTraceId());

        if (isExceptionExcluded(e))
            throw e;

        logExceptionTrace(trace);

        if (properties.isKeepThrowingExceptions()) {
            logger.debug("Exceptions specified to keep throwing in properties, exception with assigned Trace ID: {} will be thrown again", trace.getTraceId());
            throw e;
        }

        logger.debug("Returning HTTP response with reference for exception with Trace ID: {}", trace.getTraceId());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(trace.getHttpResponseDTO(properties.getHttpResponseErrorMessage()));
    }
}
