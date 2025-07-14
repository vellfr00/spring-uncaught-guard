package com.velluto.uncaughtguard.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.velluto.uncaughtguard.models.UncaughtGuardThrowingMethod;

import java.util.LinkedList;
import java.util.List;

/**
 * This exception is used to enrich the original RuntimeException with the method signature and parameters
 * that caused the exception to be thrown, allowing for better debugging and tracing of issues.
 * <p>
 * It extends RuntimeException and maintains a reference to the original exception,
 * alongside a list of methods that were involved in throwing the exception.
 */
public class UncaughtGuardMethodParametersEnrichedRuntimeException extends RuntimeException {
    private final List<UncaughtGuardThrowingMethod> throwingMethods;
    private final RuntimeException originalExceptionReference;

    public UncaughtGuardMethodParametersEnrichedRuntimeException(
            RuntimeException exception,
            String throwingMethodSignature,
            Object[] throwingMethodArgs
    ) {
        super();

        this.originalExceptionReference = getOriginalExceptionReference(exception);
        this.throwingMethods = buildThrowingMethodsTrace(exception, throwingMethodSignature, throwingMethodArgs);
    }

    private static RuntimeException getOriginalExceptionReference(RuntimeException receivedException) {
        if (receivedException instanceof UncaughtGuardMethodParametersEnrichedRuntimeException enrichedRuntimeException)
            return enrichedRuntimeException.getOriginalExceptionReference();
        else
            return receivedException;
    }

    private static List<UncaughtGuardThrowingMethod> buildThrowingMethodsTrace(
            RuntimeException receivedException,
            String throwingMethodSignature,
            Object[] throwingMethodArgs
    ) {
        List<UncaughtGuardThrowingMethod> throwingMethods;
        if (receivedException instanceof UncaughtGuardMethodParametersEnrichedRuntimeException enrichedRuntimeException)
            throwingMethods = enrichedRuntimeException.getThrowingMethods();
        else
            throwingMethods = new LinkedList<>();

        throwingMethods.add(new UncaughtGuardThrowingMethod(throwingMethodSignature, throwingMethodArgs));
        return throwingMethods;
    }

    public List<UncaughtGuardThrowingMethod> getThrowingMethods() {
        return throwingMethods;
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

    public RuntimeException getOriginalExceptionReference() {
        return originalExceptionReference;
    }
}
