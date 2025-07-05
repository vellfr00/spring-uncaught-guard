package com.velluto.uncaughtguard.properties;

import com.velluto.uncaughtguard.strategies.UncaughtGuardLoggingStrategy;

public class UncaughtGuardProperties {
    private Class<? extends UncaughtGuardLoggingStrategy>[] loggingStrategies;
    private Class<? extends RuntimeException>[] excludedExceptions;
    private String httpResponseErrorMessage;
    private String logErrorMessage;
    private boolean keepThrowingExceptions;
    private boolean enableLogRequestBody;
    private boolean enableLogThrowingMethodParameters;

    public Class<? extends UncaughtGuardLoggingStrategy>[] getLoggingStrategies() {
        return loggingStrategies;
    }

    public void setLoggingStrategies(Class<? extends UncaughtGuardLoggingStrategy>[] loggingStrategies) {
        this.loggingStrategies = loggingStrategies;
    }

    public Class<? extends RuntimeException>[] getExcludedExceptions() {
        return excludedExceptions;
    }

    public void setExcludedExceptions(Class<? extends RuntimeException>[] excludedExceptions) {
        this.excludedExceptions = excludedExceptions;
    }

    public String getHttpResponseErrorMessage() {
        return httpResponseErrorMessage;
    }

    public void setHttpResponseErrorMessage(String httpResponseErrorMessage) {
        this.httpResponseErrorMessage = httpResponseErrorMessage;
    }

    public String getLogErrorMessage() {
        return logErrorMessage;
    }

    public void setLogErrorMessage(String logErrorMessage) {
        this.logErrorMessage = logErrorMessage;
    }

    public boolean isKeepThrowingExceptions() {
        return keepThrowingExceptions;
    }

    public void setKeepThrowingExceptions(boolean keepThrowingExceptions) {
        this.keepThrowingExceptions = keepThrowingExceptions;
    }

    public boolean isEnableLogRequestBody() {
        return enableLogRequestBody;
    }

    public void setEnableLogRequestBody(boolean enableLogRequestBody) {
        this.enableLogRequestBody = enableLogRequestBody;
    }

    public boolean isEnableLogThrowingMethodParameters() {
        return enableLogThrowingMethodParameters;
    }

    public void setEnableLogThrowingMethodParameters(boolean enableLogThrowingMethodParameters) {
        this.enableLogThrowingMethodParameters = enableLogThrowingMethodParameters;
    }
}

