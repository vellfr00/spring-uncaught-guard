package com.velluto.uncaughtguard.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UncaughtGuardExceptionTraceExceptionJsonSerializerTest {
    private UncaughtGuardExceptionTraceExceptionJsonSerializer serializer;
    private JsonGenerator gen;
    private SerializerProvider serializers;

    @BeforeEach
    void setUp() {
        serializer = new UncaughtGuardExceptionTraceExceptionJsonSerializer();
        gen = mock(JsonGenerator.class);
        serializers = mock(SerializerProvider.class);
    }

    @Test
    void serialize_simpleException() throws IOException {
        RuntimeException ex = new RuntimeException("test message");
        serializer.serialize(ex, gen, serializers);
        verify(gen, atLeastOnce()).writeStringField(eq("thrownException"), eq(ex.getClass().getName()));
        verify(gen, atLeastOnce()).writeStringField(eq("message"), eq("test message"));
        verify(gen).writeArrayFieldStart(eq("stackTrace"));
        verify(gen).writeEndArray();
        verify(gen).writeEndObject();
    }

    @Test
    void serialize_exceptionWithCause() throws IOException {
        Throwable cause = new IllegalArgumentException("cause message");
        RuntimeException ex = new RuntimeException("main message", cause);
        serializer.serialize(ex, gen, serializers);
        verify(gen, atLeastOnce()).writeStringField(eq("thrownException"), eq(ex.getClass().getName()));
        verify(gen, atLeastOnce()).writeStringField(eq("message"), eq("main message"));
        verify(gen, atLeastOnce()).writeFieldName(eq("cause"));
        verify(gen, atLeastOnce()).writeStringField(eq("thrownException"), eq(cause.getClass().getName()));
        verify(gen, atLeastOnce()).writeStringField(eq("message"), eq("cause message"));
    }

    @Test
    void serialize_nestedCauses() throws IOException {
        Throwable root = new NullPointerException("root");
        Throwable mid = new IllegalStateException("mid", root);
        RuntimeException ex = new RuntimeException("top", mid);
        serializer.serialize(ex, gen, serializers);
        verify(gen, atLeastOnce()).writeStringField(eq("thrownException"), eq(ex.getClass().getName()));
        verify(gen, atLeastOnce()).writeStringField(eq("thrownException"), eq(mid.getClass().getName()));
        verify(gen, atLeastOnce()).writeStringField(eq("thrownException"), eq(root.getClass().getName()));
    }

    @Test
    void serialize_cycleInCauses() throws IOException {
        RuntimeException ex = new RuntimeException("cycle");
        Throwable cause = new IllegalArgumentException("cause");
        // introduce cycle
        ex.initCause(cause);
        cause.initCause(ex);
        serializer.serialize(ex, gen, serializers);
        verify(gen, atLeastOnce()).writeStringField(eq("thrownException"), eq(ex.getClass().getName()));
        verify(gen, atLeastOnce()).writeFieldName(eq("cause"));
        verify(gen, atLeastOnce()).writeStringField(eq("error"), eq("Cycle detected in cause chain"));
    }
}

