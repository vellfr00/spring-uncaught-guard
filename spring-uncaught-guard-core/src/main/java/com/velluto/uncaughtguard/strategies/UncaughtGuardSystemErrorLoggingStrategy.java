package com.velluto.uncaughtguard.strategies;

import com.velluto.uncaughtguard.models.UncaughtGuardExceptionTrace;

/**
 * A logging strategy for uncaught exceptions that logs the details to the standard error output.
 * It is the default logging strategy used by the Uncaught Guard framework.
 * If many logging strategies are defined, and they all fail to log, this one will be used as a fallback.
 */
public class UncaughtGuardSystemErrorLoggingStrategy extends UncaughtGuardLoggingStrategy {
    @Override
    public void log(UncaughtGuardExceptionTrace exceptionTrace) {
        System.err.println(
                getLogErrorMessage() + '\n' + '\n' +
                        "Trace ID     : " + exceptionTrace.getTraceId() + '\n' +
                        "Timestamp    : " + exceptionTrace.getIncidentTimestamp() + '\n' +
                        "Method       : " + exceptionTrace.getMethod() + '\n' +
                        "Path         : " + exceptionTrace.getPath() + '\n' +
                        "Query Params : " + exceptionTrace.getQueryParams().toString() + '\n' +
                        "Headers      : " + exceptionTrace.getHeaders().toString() + '\n' +
                        "Body         : " + '\n' + exceptionTrace.getBody() + '\n' +
                        "Methods      : " + '\n' + exceptionTrace.getJSONSerializedThrowingMethods() + '\n' +
                        "Exception    : " + '\n' + exceptionTrace.getLoggableExceptionStackTrace()
        );
    }
}
