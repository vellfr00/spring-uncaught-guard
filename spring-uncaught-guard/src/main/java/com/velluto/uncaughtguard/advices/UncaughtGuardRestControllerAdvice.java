package com.velluto.uncaughtguard.advices;

import com.velluto.uncaughtguard.models.UncaughtGuardExceptionTrace;
import com.velluto.uncaughtguard.models.UncaughtGuardExceptionTraceHttpResponseDTO;
import com.velluto.uncaughtguard.properties.UncaughtGuardProperties;
import com.velluto.uncaughtguard.strategies.UncaughtGuardLoggingStrategy;
import com.velluto.uncaughtguard.strategies.UncaughtGuardSystemErrorLoggingStrategy;
import jakarta.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

@RestControllerAdvice
public class UncaughtGuardRestControllerAdvice {
    private static final Logger logger = Logger.getLogger(UncaughtGuardRestControllerAdvice.class.getName());
    private final ApplicationContext context;
    private final UncaughtGuardProperties properties;

    public UncaughtGuardRestControllerAdvice(ApplicationContext context, UncaughtGuardProperties properties) {
        this.context = context;
        this.properties = properties;
    }

    private void logExceptionTrace(UncaughtGuardExceptionTrace trace) {
        int successfulLoggingCount = 0;
        for (Class<? extends UncaughtGuardLoggingStrategy> loggingStrategy : properties.getLoggingStrategies()) {
            logger.fine("Logging exception trace with assigned Trace ID: " + trace.getTraceId() + " using specified logging strategy " + loggingStrategy.getSimpleName());
            UncaughtGuardLoggingStrategy loggingStrategyBean = context.getBean(loggingStrategy);
            boolean loggingSuccessfull = loggingStrategyBean.callLog(trace);
            if(loggingSuccessfull)
                successfulLoggingCount++;
        }

        if (successfulLoggingCount == 0) {
            logger.warning("No logging strategies were able to log the exception trace con Trace ID assegnato: " + trace.getTraceId() + ", logging con strategia di default");
            UncaughtGuardSystemErrorLoggingStrategy defaultLoggingStrategy = context.getBean(UncaughtGuardSystemErrorLoggingStrategy.class);
            defaultLoggingStrategy.callLog(trace);
        }
    }

    private boolean isExceptionExcluded(RuntimeException e) {
        logger.fine("Checking if exception of type " + e.getClass().getSimpleName() + " is excluded from handling");
        boolean isExcluded = Arrays.stream(properties.getExcludedExceptions()).anyMatch(excluded -> excluded.equals(e.getClass()));

        if(isExcluded)
            logger.fine("Exception of type " + e.getClass().getSimpleName() + " is specified to be excluded from handling, it will be thrown again");

        return isExcluded;
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<UncaughtGuardExceptionTraceHttpResponseDTO> handleUncaughtExceptions(RuntimeException e, HttpServletRequest request) throws RuntimeException {
        UncaughtGuardExceptionTrace trace = new UncaughtGuardExceptionTrace(request, e, properties.isEnableLogRequestBody());
        logger.fine("Caught an unhandled exception of type " + e.getClass().getSimpleName() + ", assigned Trace ID: " + trace.getTraceId());

        if (isExceptionExcluded(e))
            throw e;

        logExceptionTrace(trace);

        if (properties.isKeepThrowingExceptions()) {
            logger.fine("Exceptions specified to keep throwing in properties, exception with assigned Trace ID: " + trace.getTraceId() + " will be thrown again");
            throw e;
        }

        logger.fine("Returning HTTP response with reference for exception with Trace ID: " + trace.getTraceId());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(trace.getHttpResponseDTO(properties.getHttpResponseErrorMessage()));
    }
}
