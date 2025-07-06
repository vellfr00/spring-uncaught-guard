package com.velluto.uncaughtguard.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.mockito.Mockito.*;

class UncaughtGuardMethodParameterValueJsonSerializerTest {
    private UncaughtGuardMethodParameterValueJsonSerializer serializer;
    private JsonGenerator gen;
    private SerializerProvider serializers;

    @BeforeEach
    void setUp() {
        serializer = new UncaughtGuardMethodParameterValueJsonSerializer();
        gen = mock(JsonGenerator.class);
        serializers = mock(SerializerProvider.class);
    }

    @Test
    void testSerialize_NullValue() throws IOException {
        serializer.serialize(null, gen, serializers);
        verify(gen).writeNull();
        verifyNoMoreInteractions(gen);
    }

    @Test
    void testSerialize_NormalObject() throws IOException {
        TestObj obj = new TestObj("foo", 42);
        serializer.serialize(obj, gen, serializers);
        verify(gen).writeTree(any());
    }

    @Test
    void testSerialize_FallbackToString() throws IOException {
        Object problematic = new         class SelfRef {
            Object ref = this;
        };
        UncaughtGuardMethodParameterValueJsonSerializer spySerializer = spy(serializer);
        doThrow(new RuntimeException("fail"))
                .when(spySerializer)
                .serialize(any(), any(), any());
        // Forcing fallback by calling the real serializer with a mock that throws in valueToTree
        UncaughtGuardMethodParameterValueJsonSerializer fallbackSerializer = new Object() {
            @Override
            public String toString() {
                return "fallback-value";
            }
        };
        // Actually, to test fallback, we can use a value that Jackson cannot serialize, e.g. a self-referencing object
UncaughtGuardMethodParameterValueJsonSerializer() {
            @Override
            public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                if (value == problematic) throw new RuntimeException("fail");
                super.serialize(value, gen, serializers);
            }
        }
        SelfRef self = new SelfRef();
        fallbackSerializer.serialize(self, gen, serializers);
        verify(gen).writeString(anyString());
    }

    static class TestObj {
        public String foo;
        public int bar;

        public TestObj(String foo, int bar) {
            this.foo = foo;
            this.bar = bar;
        }
    }
}

