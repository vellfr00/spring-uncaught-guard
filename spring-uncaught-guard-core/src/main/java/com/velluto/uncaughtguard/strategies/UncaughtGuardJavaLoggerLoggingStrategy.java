package com.velluto.uncaughtguard.strategies;

import com.velluto.uncaughtguard.models.UncaughtGuardExceptionTrace;
import org.springframework.stereotype.Component;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A logging strategy for uncaught exceptions that uses the java.util.logging framework.
 * This strategy logs the exception details using the Logger associated with the class and method that threw the exception.
 */
@Component
public class UncaughtGuardJavaLoggerLoggingStrategy extends UncaughtGuardLoggingStrategy {
    @Override
    protected void log(UncaughtGuardExceptionTrace exceptionTrace) {
        // get the class that threw the exception
        String throwingClassName = getThrowingClassName(exceptionTrace);
        // get the java.util.logging.Logger associated with the throwing class
        Logger throwingClassLogger = Logger.getLogger(throwingClassName);

        // log the exception using the java.util.logging.Logger
        throwingClassLogger.logp(
                Level.SEVERE,
                throwingClassName,
                getThrowingMethodName(exceptionTrace),
                String.format("""
                                %s
                                \s
                                Trace ID     : %s
                                Timestamp    : %s
                                Method       : %s
                                Path         : %s
                                Query Params : %s
                                Headers      : %s
                                Body         :\s
                                %s
                                \s
                                Exception    :\s
                                \s""",
                        getLogErrorMessage(),
                        exceptionTrace.getTraceId(),
                        exceptionTrace.getIncidentTimestamp(),
                        exceptionTrace.getMethod(),
                        exceptionTrace.getPath(),
                        exceptionTrace.getQueryParams().toString(),
                        exceptionTrace.getHeaders().toString(),
                        exceptionTrace.getBody()
                ),
                exceptionTrace.getException()
        );
    }
}
