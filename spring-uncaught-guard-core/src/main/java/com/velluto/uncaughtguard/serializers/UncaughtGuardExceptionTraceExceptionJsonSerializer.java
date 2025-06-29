package com.velluto.uncaughtguard.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * This custom JSON serializer is used to serialize uncaught exceptions.
 * It writes the exception type, message, stack trace,
 * and cause (if any) to the JSON output.
 * It handles cycles in the cause chain by keeping track of seen exceptions.
 * If a cycle is detected, it writes a special message instead of the cycle.
 * The stack trace is written
 * as an array of strings, each representing a stack trace element.
 */
public class UncaughtGuardExceptionTraceExceptionJsonSerializer extends JsonSerializer<RuntimeException> {
    @Override
    public void serialize(RuntimeException value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        Set<Throwable> seen = new HashSet<>();
        writeException(gen, value, seen);
    }

    private void writeException(JsonGenerator gen, Throwable ex, Set<Throwable> seen) throws IOException {
        if (seen.contains(ex)) {
            gen.writeStartObject();
            gen.writeStringField("error", "Cycle detected in cause chain");
            gen.writeEndObject();
            return;
        }
        seen.add(ex);

        gen.writeStartObject();
        gen.writeStringField("thrownException", ex.getClass().getName());
        gen.writeStringField("message", ex.getMessage());
        gen.writeArrayFieldStart("stackTrace");
        for (StackTraceElement element : ex.getStackTrace()) {
            gen.writeString(element.toString());
        }
        gen.writeEndArray();

        Throwable cause = ex.getCause();
        if (cause != null && cause != ex) {
            gen.writeFieldName("cause");
            writeException(gen, cause, seen);
        }

        gen.writeEndObject();
    }
}