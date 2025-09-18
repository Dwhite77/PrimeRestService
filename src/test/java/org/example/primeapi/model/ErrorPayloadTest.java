package org.example.primeapi.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorPayloadTest {

    @Test
    void builderSetsAllFieldsCorrectly() {
        ErrorPayload payload = ErrorPayload.builder()
                .status(400)
                .error("Bad Request")
                .message("Missing required parameter 'limit'")
                .path("/api/primes")
                .build();

        assertEquals(400, payload.getStatus());
        assertEquals("Bad Request", payload.getError());
        assertEquals("Missing required parameter 'limit'", payload.getMessage());
        assertEquals("/api/primes", payload.getPath());
    }

    @Test
    void pathCanBeNullWithoutBreakingSerialization() throws JsonProcessingException {
        ErrorPayload payload = ErrorPayload.builder()
                .status(500)
                .error("Internal Server Error")
                .message("Unexpected error occurred")
                .path(null)
                .build();

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(payload);

        assertFalse(json.contains("\"path\"")); // path should be excluded
        assertTrue(json.contains("\"status\":500"));
        assertTrue(json.contains("\"error\":\"Internal Server Error\""));
    }

    @Test
    void jsonSerializationPreservesFieldOrder() throws JsonProcessingException {
        ErrorPayload payload = ErrorPayload.builder()
                .status(404)
                .error("Not Found")
                .message("Unknown path")
                .path("/api/unknown")
                .build();

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(payload);

        int statusIndex = json.indexOf("\"status\"");
        int errorIndex = json.indexOf("\"error\"");
        int messageIndex = json.indexOf("\"message\"");
        int pathIndex = json.indexOf("\"path\"");

        assertTrue(statusIndex < errorIndex);
        assertTrue(errorIndex < messageIndex);
        assertTrue(messageIndex < pathIndex);
    }

    @Test
    void xmlSerializationProducesExpectedRootAndFields() throws JsonProcessingException {
        ErrorPayload payload = ErrorPayload.builder()
                .status(400)
                .error("Bad Request")
                .message("Invalid input")
                .path("/api/primes")
                .build();

        XmlMapper xmlMapper = new XmlMapper();
        String xml = xmlMapper.writeValueAsString(payload);

        assertTrue(xml.contains("<ErrorPayload>"));
        assertTrue(xml.contains("<status>400</status>"));
        assertTrue(xml.contains("<error>Bad Request</error>"));
        assertTrue(xml.contains("<message>Invalid input</message>"));
        assertTrue(xml.contains("<path>/api/primes</path>"));
    }
}
