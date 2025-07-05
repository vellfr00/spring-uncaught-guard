package com.velluto.uncaughtguard.models;

import java.util.Arrays;

public class UncaughtGuardThrowingMethod {
    private final String methodSignature;
    private final UncaughtGuardThrowingMethodParameter[] passedParameters;

    public UncaughtGuardThrowingMethod(String methodSignature, Object[] passedParameters) {
        this.methodSignature = methodSignature;
        this.passedParameters = buildPassedParameters(passedParameters);
    }

    private UncaughtGuardThrowingMethodParameter[] buildPassedParameters(Object[] parameters) {
        if (parameters == null || parameters.length == 0)
            return new UncaughtGuardThrowingMethodParameter[0];

        return Arrays.stream(parameters)
                        .map(UncaughtGuardThrowingMethodParameter::new)
                        .toArray(UncaughtGuardThrowingMethodParameter[]::new);
    }

    public String getMethodSignature() {
        return methodSignature;
    }

    public UncaughtGuardThrowingMethodParameter[] getPassedParameters() {
        return passedParameters;
    }
}
