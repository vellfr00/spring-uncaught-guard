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
            String className = this.getClass().getSimpleName();

            logger.warning("Could not log uncaught exception with assigned Trace Id: " + exceptionTrace.getTraceId() + " with specified logging strategy " + className + ". Exception: " + e);
            return false;
        }
    }

    /**
     * The actual implementation of the logging strategy, that actually logs the exception trace.
     *
     * @param exceptionTrace the full exception trace to log
     */
    public abstract void log(UncaughtGuardExceptionTrace exceptionTrace);
}
