package com.velluto.uncaughtguard.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * A custom JSON serializer for method parameters in the Uncaught Guard framework.
 * This serializer attempts to serialize the parameter value normally,
 * but if it fails (e.g., due to a circular reference or other serialization issues),
 * it falls back to writing a string representation of the value.
 */
public class UncaughtGuardMethodParameterValueJsonSerializer extends JsonSerializer<Object> {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }

        // try to serialize the object normally, if it fails, write a string representation
        try {
            JsonNode node = mapper.valueToTree(value);
            gen.writeTree(node);
        } catch (Exception e) {
            String valueAsString = value.toString();
            gen.writeString(valueAsString);
        }
    }
}