package com.velluto.uncaughtguardtestapp.logging.strategies;

import com.velluto.uncaughtguard.strategies.UncaughtGuardFileSystemAbstractLoggingStrategy;

public class UncaughtGuardFileSystemStrategy extends UncaughtGuardFileSystemAbstractLoggingStrategy {
    @Override
    public String filePath() {
        return "C:\\uncaught-logs";
    }
}
