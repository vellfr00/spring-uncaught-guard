package com.velluto.uncaughtguard.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * This custom JSON serializer is used to serialize the body of the uncaught exception trace.
 * It attempts to parse the body as JSON and write it as a JSON object.
 * If the body is not valid JSON, it writes the body as a string.
 * If the body is null, it writes null to the JSON output.
 */
public class UncaughtGuardExceptionTraceBodyJsonSerializer extends JsonSerializer<String> {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        // if the value is null or empty, write null to the JSON output
        if (value == null) {
            gen.writeNull();
            return;
        }

        // try to parse the value as JSON
        try {
            Object json = mapper.readTree(value);
            // if parsing is successful, write the JSON object
            gen.writeObject(json);
        } catch (Exception e) {
            // if parsing fails, write the value as a string
            gen.writeString(value);
        }
    }
}