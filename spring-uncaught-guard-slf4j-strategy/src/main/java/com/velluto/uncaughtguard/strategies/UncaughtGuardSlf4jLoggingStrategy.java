package com.velluto.uncaughtguard.strategies;

import com.velluto.uncaughtguard.models.UncaughtGuardExceptionTrace;

import java.util.logging.Logger;

public class UncaughtGuardSlf4jLoggingStrategy extends UncaughtGuardLoggingStrategy {
    private static final Logger logger = Logger.getLogger(UncaughtGuardSlf4jLoggingStrategy.class.getName());

    public String getLogErrorMessage() {
        return properties.getLogErrorMessage();
    }

    /**
     * Returns the class that threw the exception.
     * This is determined by the first element in the stack trace of the throwable.
     *
     * @param exception the throwable that was thrown
     * @return the class that threw the exception, or null if the stack trace is empty
     */
    private Class<?> getThrowingClass(RuntimeException exception) {
        if (exception == null || exception.getStackTrace() == null || exception.getStackTrace().length == 0) {
            logger.warning("Exception is null or has no stack trace. Returning the current class.");
            return this.getClass();
        }

        StackTraceElement[] stackTrace = exception.getStackTrace();
        if (stackTrace.length > 0)
            return stackTrace[0].getClass();

        logger.warning("Stack trace is empty. Returning the current class.");
        return this.getClass();
    }

    @Override
    public void log(UncaughtGuardExceptionTrace exceptionTrace) {
        // Get the class that threw the exception
        Class<?> throwingClass = getThrowingClass(exceptionTrace.getException());
        // Get the slf4j logger associated with the throwing class
        org.slf4j.Logger throwingClassLogger = org.slf4j.LoggerFactory.getLogger(throwingClass);

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
