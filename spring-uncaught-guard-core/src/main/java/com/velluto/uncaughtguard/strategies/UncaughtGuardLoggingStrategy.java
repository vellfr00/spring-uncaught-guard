package com.velluto.uncaughtguard.strategies;

import com.velluto.uncaughtguard.models.UncaughtGuardExceptionTrace;
import com.velluto.uncaughtguard.properties.UncaughtGuardProperties;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract class for logging strategies in the Uncaught Guard framework.
 * This class provides a template for logging uncaught exceptions with a specific strategy.
 * It includes methods to retrieve the error message, throwing class name and method name.
 * Developers can extend this class to implement their own logging strategies and use them in the UncaughtGuard framework.
 * All implementations must provide a concrete implementation of the `log` method and use the `@Component` annotation to register the strategy as a Spring bean.
 * <p>
 * The class also handles exceptions that may occur during the logging process, ensuring that the application does not crash.
 * <p>
 * It is configured through the `UncaughtGuardProperties` class, which allows customization of the logging behavior,
 * including the error message to be logged when an uncaught exception occurs.
 */
public abstract class UncaughtGuardLoggingStrategy {
    private final Logger logger = Logger.getLogger(UncaughtGuardLoggingStrategy.class.getName());

    @Autowired
    private UncaughtGuardProperties properties;

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
     * If the stack trace is empty or the exception is null, it returns the name of the current class.
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
     * Returns the name of the method that threw the exception.
     * This is determined by the first element in the stack trace of the throwable.
     * If the stack trace is empty or the exception is null, it returns "log".
     *
     * @param exceptionTrace the exception trace containing the throwable
     * @return the name of the method that threw the exception, or "UnknownMethod" if the stack trace is empty
     */
    protected final String getThrowingMethodName(UncaughtGuardExceptionTrace exceptionTrace) {
        if (exceptionTrace == null || exceptionTrace.getException() == null) {
            logger.warning("Exception trace or exception is null. Returning the current method.");
            return "log";
        }

        Throwable exception = exceptionTrace.getException();
        if (exception == null || exception.getStackTrace() == null || exception.getStackTrace().length == 0) {
            logger.warning("Exception is null or has no stack trace. Returning the current method.");
            return "log";
        }

        StackTraceElement[] stackTrace = exception.getStackTrace();
        if (stackTrace.length > 0)
            return stackTrace[0].getMethodName();

        logger.warning("Stack trace is empty. Returning the current method.");
        return "log";
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

            logger.logp(
                    Level.WARNING,
                    className,
                    "callLog",
                    "Could not log uncaught exception with assigned Trace Id: " + exceptionTrace.getTraceId() + " with specified logging strategy " + className,
                    e);

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
