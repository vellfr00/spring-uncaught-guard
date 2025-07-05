package com.velluto.uncaughtguard.advices;

import com.velluto.uncaughtguard.loggers.UncaughtGuardAsyncLogger;
import com.velluto.uncaughtguard.models.UncaughtGuardExceptionTrace;
import com.velluto.uncaughtguard.models.UncaughtGuardExceptionTraceHttpResponseDTO;
import com.velluto.uncaughtguard.properties.UncaughtGuardProperties;
import com.velluto.uncaughtguard.utils.UncaughtGuardExceptionUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.logging.Logger;

@RestControllerAdvice
public class UncaughtGuardRestControllerAdvice {
    private static final Logger logger = Logger.getLogger(UncaughtGuardRestControllerAdvice.class.getName());

    @Autowired
    private UncaughtGuardProperties properties;
    @Autowired
    private UncaughtGuardAsyncLogger asyncLogger;
    @Autowired
    private UncaughtGuardExceptionUtils exceptionUtils;

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<UncaughtGuardExceptionTraceHttpResponseDTO> handleUncaughtExceptions(RuntimeException e, HttpServletRequest request) throws RuntimeException {
        UncaughtGuardExceptionTrace trace = new UncaughtGuardExceptionTrace(request, e, properties.isEnableLogRequestBody());
        logger.fine("Caught an unhandled exception of type " + e.getClass().getSimpleName() + ", assigned Trace ID: " + trace.getTraceId());

        if (exceptionUtils.isExceptionExcluded(e))
            throw e;

        asyncLogger.logExceptionTraceAsync(trace);

        if (properties.isKeepThrowingExceptions()) {
            logger.fine("Exceptions specified to keep throwing in properties, exception with assigned Trace ID: " + trace.getTraceId() + " will be thrown again");
            throw e;
        }

        logger.fine("Returning HTTP response with reference for exception with Trace ID: " + trace.getTraceId());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(trace.getHttpResponseDTO(properties.getHttpResponseErrorMessage()));
    }
}
