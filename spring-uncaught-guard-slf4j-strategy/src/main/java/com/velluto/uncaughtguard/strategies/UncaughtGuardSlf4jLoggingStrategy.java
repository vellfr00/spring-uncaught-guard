package com.velluto.uncaughtguard.strategies;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.velluto.uncaughtguard.exceptions.UncaughtGuardMethodParametersEnrichedRuntimeException;
import com.velluto.uncaughtguard.models.UncaughtGuardExceptionTrace;

/**
 * A logging strategy for uncaught exceptions that uses the SLF4J framework.
 * This strategy logs the exception details using the SLF4J logger associated with the class that threw the exception.
 * It requires the SLF4J library to be included in the project dependencies.
 */
public class UncaughtGuardSlf4jLoggingStrategy extends UncaughtGuardLoggingStrategy {
    private String getLoggableThrowingMethodsString(RuntimeException exception) {
        if (!(exception instanceof UncaughtGuardMethodParametersEnrichedRuntimeException enrichedRuntimeException))
            return "null";

        // use jackson to serialize the throwing methods
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(enrichedRuntimeException.getThrowingMethods());
        } catch (Exception e) {
            return "Error serializing throwing methods: " + e.getMessage();
        }
    }

    @Override
    public void log(UncaughtGuardExceptionTrace exceptionTrace) {
        // Get the class that threw the exception
        String throwingClassName = getThrowingClassName(exceptionTrace);
        // Get the slf4j logger associated with the throwing class
        org.slf4j.Logger throwingClassLogger = org.slf4j.LoggerFactory.getLogger(throwingClassName);

        // Log the exception using the slf4j logger
        throwingClassLogger.error("""
                        {}
                        
                        Trace ID     : {}
                        Timestamp    : {}
                        Method       : {}
                        Path         : {}
                        Query Params : {}
                        Headers      : {}
                        Body         :
                        {}
                        Methods      :
                        {}
                        
                        Exception    :
                        """,
                getLogErrorMessage(),
                exceptionTrace.getTraceId(),
                exceptionTrace.getIncidentTimestamp(),
                exceptionTrace.getMethod(),
                exceptionTrace.getPath(),
                exceptionTrace.getQueryParams().toString(),
                exceptionTrace.getHeaders().toString(),
                exceptionTrace.getBody(),
                getLoggableThrowingMethodsString(exceptionTrace.getException()),
                exceptionTrace.getException()
        );
    }
}
