package com.velluto.uncaughtguard.exceptions;

import com.velluto.uncaughtguard.models.UncaughtGuardThrowingMethod;

import java.util.LinkedList;
import java.util.List;

public class UncaughtGuardMethodParametersEnrichedRuntimeException extends RuntimeException {
    private final List<UncaughtGuardThrowingMethod> throwingMethods;
    private final String originalExceptionClassName;

    public UncaughtGuardMethodParametersEnrichedRuntimeException(
            RuntimeException originalException,
            String throwingMethodSignature,
            Object[] throwingMethodArgs
    ) {
        super(buildMessage(originalException));
        cloneOriginalException(originalException);

        this.throwingMethods = buildThrowingMethodsTrace(originalException, throwingMethodSignature, throwingMethodArgs);
        this.originalExceptionClassName = getOriginalExceptionClassName(originalException);
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
        if(originalException instanceof UncaughtGuardMethodParametersEnrichedRuntimeException) {
            return originalException.getMessage();
        } else {
            return "[ACTUAL Exception: " + originalException.getClass().getName() + "] " + originalException.getMessage();
        }
    }

    private static String getOriginalExceptionClassName(RuntimeException originalException) {
        if(originalException instanceof UncaughtGuardMethodParametersEnrichedRuntimeException) {
            return ((UncaughtGuardMethodParametersEnrichedRuntimeException) originalException).getOriginalExceptionClassName();
        } else {
            return originalException.getClass().getName();
        }
    }

    private void cloneOriginalException(RuntimeException originalException) {
        this.setStackTrace(originalException.getStackTrace());
        this.initCause(originalException.getCause());

        for(Throwable suppressed : originalException.getSuppressed())
            this.addSuppressed(suppressed);
    }

    public List<UncaughtGuardThrowingMethod> getThrowingMethods() {
        return throwingMethods;
    }

    public String getOriginalExceptionClassName() {
        return originalExceptionClassName;
    }
}
