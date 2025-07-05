package com.velluto.uncaughtguard.loggers;

import com.velluto.uncaughtguard.models.UncaughtGuardExceptionTrace;
import com.velluto.uncaughtguard.properties.UncaughtGuardProperties;
import com.velluto.uncaughtguard.strategies.UncaughtGuardLoggingStrategy;
import com.velluto.uncaughtguard.strategies.UncaughtGuardSystemErrorLoggingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class UncaughtGuardAsyncLogger {
    private static final Logger logger = Logger.getLogger(UncaughtGuardAsyncLogger.class.getName());

    @Autowired
    private ApplicationContext context;
    @Autowired
    private UncaughtGuardProperties properties;

    /**
     * Logs the given exception trace asynchronously using the configured logging strategies.
     * This method will attempt to log the trace using each of the specified logging strategies.
     * If none of the strategies succeed, it will fall back to the default system error logging strategy.
     *
     * @param trace the exception trace to log
     */
    @Async
    public void logExceptionTraceAsync(UncaughtGuardExceptionTrace trace) {
        int successfulLoggingCount = 0;
        for (Class<? extends UncaughtGuardLoggingStrategy> loggingStrategy : properties.getLoggingStrategies()) {
            logger.fine("Logging exception trace with assigned Trace ID: " + trace.getTraceId() + " using specified logging strategy " + loggingStrategy.getSimpleName());
            UncaughtGuardLoggingStrategy loggingStrategyBean = context.getBean(loggingStrategy);
            boolean loggingSuccessfull = loggingStrategyBean.callLog(trace);
            if (loggingSuccessfull)
                successfulLoggingCount++;
        }

        if (successfulLoggingCount == 0) {
            logger.warning("No logging strategies were able to log the exception trace con Trace ID assegnato: " + trace.getTraceId() + ", logging con strategia di default");
            UncaughtGuardSystemErrorLoggingStrategy defaultLoggingStrategy = context.getBean(UncaughtGuardSystemErrorLoggingStrategy.class);
            defaultLoggingStrategy.callLog(trace);
        }
    }
}

