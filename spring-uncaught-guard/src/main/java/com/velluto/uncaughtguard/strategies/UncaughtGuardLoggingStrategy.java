package com.velluto.uncaughtguard.strategies;

import com.velluto.uncaughtguard.models.UncaughtGuardExceptionTrace;
import com.velluto.uncaughtguard.properties.UncaughtGuardProperties;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.logging.Logger;

public abstract class UncaughtGuardLoggingStrategy {
    private final Logger logger = Logger.getLogger(UncaughtGuardLoggingStrategy.class.getName());

    @Autowired
    protected UncaughtGuardProperties properties;

    /**
     * Returns the error message to be logged when an uncaught exception occurs and before all the details of the exception trace.
     * It is configured in the annotation property named "logErrorMessage" of @EnableUncaughtGuard.
     *
     * @return the error message to be logged
     */
    protected String getLogErrorMessage() {
        return properties.getLogErrorMessage();
    }

    /**
     * Returns the class name that threw the exception.
     * This is determined by the first element in the stack trace of the throwable.
     *
     * @param exceptionTrace the exception trace containing the throwable
     * @return the class name that threw the exception, or null if the stack trace is empty
     */
    protected final String getThrowingClassName(UncaughtGuardExceptionTrace exceptionTrace) {
        if (exceptionTrace == null || exceptionTrace.getException() == null) {
            logger.warning("Exception trace or exception is null. Returning the current class.");
            return this.getClass().getName();
        }

        Throwable exception = exceptionTrace.getException();
        if (exception == null || exception.getStackTrace() == null || exception.getStackTrace().length == 0) {
            logger.warning("Exception is null or has no stack trace. Returning the current class.");
            return this.getClass().getName();
        }

        StackTraceElement[] stackTrace = exception.getStackTrace();
        if (stackTrace.length > 0)
            return stackTrace[0].getClassName();

        logger.warning("Stack trace is empty. Returning the current class.");
        return this.getClass().getName();
    }

    /**
     * Calls the logging strategy to log the uncaught exception trace.
     * This is a wrapper method that handles any exceptions that may occur during the logging process.
     * It ensures that even if the logging fails, the application does not crash and provides a meaningful error message.
     *
     * @param exceptionTrace the full exception trace to log
     * @return true if the logging was successful, false otherwise
     */
    public final boolean callLog(UncaughtGuardExceptionTrace exceptionTrace) {
        try {
            log(exceptionTrace);
            return true;
        } catch (Exception e) {
            // get the name of the concrete class that extends this abstract class
            String className = this.getClass().getName();

            logger.warning("Could not log uncaught exception with assigned Trace Id: " + exceptionTrace.getTraceId() + " with specified logging strategy " + className + ". Exception: " + e);
            return false;
        }
    }

    /**
     * The actual implementation of the logging strategy, that actually logs the exception trace.
     *
     * @param exceptionTrace the full exception trace to log
     */
    protected abstract void log(UncaughtGuardExceptionTrace exceptionTrace);
}
