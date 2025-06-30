package com.velluto.uncaughtguard.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UncaughtGuardExceptionTraceBodyJsonSerializerTest {
    private UncaughtGuardExceptionTraceBodyJsonSerializer serializer;
    private JsonGenerator gen;
    private SerializerProvider serializers;

    @BeforeEach
    void setUp() {
        serializer = new UncaughtGuardExceptionTraceBodyJsonSerializer();
        gen = mock(JsonGenerator.class);
        serializers = mock(SerializerProvider.class);
    }

    @Test
    void serialize_nullValue_writesNull() throws IOException {
        serializer.serialize(null, gen, serializers);
        verify(gen).writeNull();
        verifyNoMoreInteractions(gen);
    }

    @Test
    void serialize_validJson_writesJsonObject() throws IOException {
        String json = "{\"foo\":\"bar\",\"num\":123}";
        serializer.serialize(json, gen, serializers);
        verify(gen).writeObject(any());
        verifyNoMoreInteractions(gen);
    }

    @Test
    void serialize_invalidJson_writesString() throws IOException {
        String notJson = "not a json string";
        serializer.serialize(notJson, gen, serializers);
        verify(gen).writeString(notJson);
        verifyNoMoreInteractions(gen);
    }

    @Test
    void serialize_jsonArray_writesJsonArray() throws IOException {
        String jsonArray = "[1,2,3]";
        serializer.serialize(jsonArray, gen, serializers);
        verify(gen).writeObject(any());
        verifyNoMoreInteractions(gen);
    }
}

