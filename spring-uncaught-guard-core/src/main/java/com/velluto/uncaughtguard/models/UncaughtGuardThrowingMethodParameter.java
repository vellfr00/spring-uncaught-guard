package com.velluto.uncaughtguard.models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.velluto.uncaughtguard.serializers.UncaughtGuardMethodParameterValueJsonSerializer;

public class UncaughtGuardThrowingMethodParameter {
    private final String typeClassName;
    @JsonSerialize(using = UncaughtGuardMethodParameterValueJsonSerializer.class)
    private final Object value;

    public UncaughtGuardThrowingMethodParameter(Object parameter) {
        if (parameter == null) {
            this.typeClassName = null;
            this.value = null;
        } else {
            this.typeClassName = parameter.getClass().getName();
            this.value = parameter;
        }
    }

    public String getTypeClassName() {
        return typeClassName;
    }

    public Object getValue() {
        return value;
    }
}
