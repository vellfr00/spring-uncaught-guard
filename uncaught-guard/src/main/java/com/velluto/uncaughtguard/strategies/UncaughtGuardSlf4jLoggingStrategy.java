package com.velluto.uncaughtguard.strategies;

import com.velluto.uncaughtguard.models.UncaughtGuardExceptionTrace;
import com.velluto.uncaughtguard.properties.UncaughtGuardProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UncaughtGuardSlf4jLoggingStrategy implements UncaughtGuardLoggingStrategy {
    private static final Logger logger = LoggerFactory.getLogger(UncaughtGuardSlf4jLoggingStrategy.class.getName());
    private final UncaughtGuardProperties properties;

    public UncaughtGuardSlf4jLoggingStrategy(UncaughtGuardProperties properties) {
        this.properties = properties;
    }

    public String getLogErrorMessage() {
        return properties.getLogErrorMessage();
    }

    @Override
    public void log(UncaughtGuardExceptionTrace exceptionTrace) {
        logger.error("""
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
