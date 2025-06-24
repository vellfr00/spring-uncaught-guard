package com.velluto.uncaughtguard.strategies;

import com.velluto.uncaughtguard.models.UncaughtGuardExceptionTrace;

public interface UncaughtGuardLoggingStrategy {
    void log(UncaughtGuardExceptionTrace exceptionTrace);
}
