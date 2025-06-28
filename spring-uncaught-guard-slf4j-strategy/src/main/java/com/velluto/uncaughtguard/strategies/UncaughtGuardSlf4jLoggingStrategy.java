package com.velluto.uncaughtguard.strategies;

import com.velluto.uncaughtguard.models.UncaughtGuardExceptionTrace;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class UncaughtGuardSlf4jLoggingStrategy extends UncaughtGuardLoggingStrategy {
    private static final Logger logger = Logger.getLogger(UncaughtGuardSlf4jLoggingStrategy.class.getName());

    public String getLogErrorMessage() {
        return properties.getLogErrorMessage();
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
                exceptionTrace.getException()
        );
    }
}
