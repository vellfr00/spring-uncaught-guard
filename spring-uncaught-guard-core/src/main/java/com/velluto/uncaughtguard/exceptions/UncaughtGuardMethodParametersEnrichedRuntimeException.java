package com.velluto.uncaughtguard.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.velluto.uncaughtguard.models.UncaughtGuardThrowingMethod;

import java.util.LinkedList;
import java.util.List;

/**
 * This exception is used to enrich the original RuntimeException with the method signature and parameters
 * that caused the exception to be thrown, allowing for better debugging and tracing of issues.
 * <p>
 * It extends RuntimeException and maintains the original exception's class name, message,
 * and stack trace, along with a list of methods that threw the exception.
 * Since the original exception class will be lost in the process of wrapping,
 * this exception also stores the original exception class name and appends it by default
 * in the exception message. The original exception message is therefore also stored.
 */
public class UncaughtGuardMethodParametersEnrichedRuntimeException extends RuntimeException {
    private final List<UncaughtGuardThrowingMethod> throwingMethods;
    private final String originalExceptionClassName;
    private final String originalExceptionMessage;

    public UncaughtGuardMethodParametersEnrichedRuntimeException(
            RuntimeException originalException,
            String throwingMethodSignature,
            Object[] throwingMethodArgs
    ) {
        super(buildMessage(originalException));
        cloneOriginalException(originalException);

        this.throwingMethods = buildThrowingMethodsTrace(originalException, throwingMethodSignature, throwingMethodArgs);
        this.originalExceptionClassName = getOriginalExceptionClassName(originalException);
        this.originalExceptionMessage = originalException.getMessage();
    }

    private static List<UncaughtGuardThrowingMethod> buildThrowingMethodsTrace(
            RuntimeException originalException,
            String throwingMethodSignature,
            Object[] throwingMethodArgs
    ) {
        List<UncaughtGuardThrowingMethod> throwingMethods;
        if (originalException instanceof UncaughtGuardMethodParametersEnrichedRuntimeException)
            throwingMethods = ((UncaughtGuardMethodParametersEnrichedRuntimeException) originalException).getThrowingMethods();
        else
            throwingMethods = new LinkedList<>();

        throwingMethods.add(new UncaughtGuardThrowingMethod(throwingMethodSignature, throwingMethodArgs));
        return throwingMethods;
    }

    private static String buildMessage(RuntimeException originalException) {
        if (originalException instanceof UncaughtGuardMethodParametersEnrichedRuntimeException) {
            return originalException.getMessage();
        } else {
            return "[ACTUAL Exception: " + originalException.getClass().getName() + "] " + originalException.getMessage();
        }
    }

    private static String getOriginalExceptionClassName(RuntimeException originalException) {
        if (originalException instanceof UncaughtGuardMethodParametersEnrichedRuntimeException) {
            return ((UncaughtGuardMethodParametersEnrichedRuntimeException) originalException).getOriginalExceptionClassName();
        } else {
            return originalException.getClass().getName();
        }
    }

    private void cloneOriginalException(RuntimeException originalException) {
        this.setStackTrace(originalException.getStackTrace());
        this.initCause(originalException.getCause());

        for (Throwable suppressed : originalException.getSuppressed())
            this.addSuppressed(suppressed);
    }

    public List<UncaughtGuardThrowingMethod> getThrowingMethods() {
        return throwingMethods;
    }

    public String getOriginalExceptionClassName() {
        return originalExceptionClassName;
    }

    public String getOriginalExceptionMessage() {
        return originalExceptionMessage;
    }

    public String getJSONSerializedThrowingMethods() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            return objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(this.throwingMethods);
        } catch (Exception e) {
            return "Error serializing throwing methods: " + e.getMessage();
        }
    }
}
