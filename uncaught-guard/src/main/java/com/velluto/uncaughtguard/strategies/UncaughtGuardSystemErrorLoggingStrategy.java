package com.velluto.uncaughtguard.strategies;

import com.velluto.uncaughtguard.models.UncaughtGuardExceptionTrace;
import com.velluto.uncaughtguard.properties.UncaughtGuardProperties;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;

@Component
public class UncaughtGuardSystemErrorLoggingStrategy extends UncaughtGuardLoggingStrategy {
    private final UncaughtGuardProperties properties;

    public UncaughtGuardSystemErrorLoggingStrategy(UncaughtGuardProperties properties) {
        this.properties = properties;
    }

    private String getLoggableExceptionStackTraceString(RuntimeException exception) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        exception.printStackTrace(printWriter);
        return stringWriter.toString();
    }

    public String getLoggableExceptionTraceString(UncaughtGuardExceptionTrace exceptionTrace) {

        String stringBuilder = getLogErrorMessage() + '\n' + '\n' +
                "Trace ID     : " + exceptionTrace.getTraceId() + '\n' +
                "Timestamp    : " + exceptionTrace.getIncidentTimestamp() + '\n' +
                "Method       : " + exceptionTrace.getMethod() + '\n' +
                "Path         : " + exceptionTrace.getPath() + '\n' +
                "Query Params : " + exceptionTrace.getQueryParams().toString() + '\n' +
                "Headers      : " + exceptionTrace.getHeaders().toString() + '\n' +
                "Body         : " + '\n' + exceptionTrace.getBody() + '\n' +
                "Exception    : " + '\n' + getLoggableExceptionStackTraceString(exceptionTrace.getException());

        return stringBuilder;
    }

    public String getLogErrorMessage() {
        return properties.getLogErrorMessage();
    }

    @Override
    public void log(UncaughtGuardExceptionTrace exceptionTrace) {
        System.err.println(getLoggableExceptionTraceString(exceptionTrace));
    }
}
