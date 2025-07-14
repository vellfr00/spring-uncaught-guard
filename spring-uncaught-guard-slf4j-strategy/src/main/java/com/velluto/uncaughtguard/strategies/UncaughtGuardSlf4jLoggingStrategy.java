package com.velluto.uncaughtguard.strategies;

import com.velluto.uncaughtguard.exceptions.UncaughtGuardMethodParametersEnrichedRuntimeException;
import com.velluto.uncaughtguard.models.UncaughtGuardExceptionTrace;

/**
 * A logging strategy for uncaught exceptions that uses the SLF4J framework.
 * This strategy logs the exception details using the SLF4J logger associated with the class that threw the exception.
 * It requires the SLF4J library to be included in the project dependencies.
 */
public class UncaughtGuardSlf4jLoggingStrategy extends UncaughtGuardLoggingStrategy {
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
                exceptionTrace.getJSONSerializedThrowingMethods(),
                exceptionTrace.getException()
        );
    }
}
