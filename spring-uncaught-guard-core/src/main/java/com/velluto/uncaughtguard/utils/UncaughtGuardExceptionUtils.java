package com.velluto.uncaughtguard.utils;

import com.velluto.uncaughtguard.properties.UncaughtGuardProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.logging.Logger;

@Component
public class UncaughtGuardExceptionUtils {
    private static final Logger logger = Logger.getLogger(UncaughtGuardExceptionUtils.class.getName());

    @Autowired
    private UncaughtGuardProperties properties;

    /**
     * Checks if the given RuntimeException is excluded from handling based on the annotation configuration properties.
     *
     * @param e the RuntimeException to check
     * @return true if the exception is excluded, false otherwise
     */
    public boolean isExceptionExcluded(RuntimeException e) {
        logger.fine("Checking if exception of type " + e.getClass().getSimpleName() + " is excluded from handling");
        boolean isExcluded = Arrays.stream(properties.getExcludedExceptions()).anyMatch(excluded -> excluded.equals(e.getClass()));

        if (isExcluded)
            logger.fine("Exception of type " + e.getClass().getSimpleName() + " is specified to be excluded from handling, it will be thrown again");

        return isExcluded;
    }
}

