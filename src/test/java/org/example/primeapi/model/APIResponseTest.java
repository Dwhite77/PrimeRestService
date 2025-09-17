package org.example.primeapi.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class APIResponseTest {

    @Test
    void testSuccessResponseFields() {
        PrimePayload payload = PrimePayload.builder()
                .algorithm("sieve")
                .limit(100)
                .threads(4)
                .primes(List.of(2, 3, 5, 7))
                .total(4)
                .durationMs(12)
                .build();

        APIResponse response = APIResponse.success(payload, 200);

        log.info("Testing success response: {}", response);
        assertEquals(200, response.getHttpStatus());
        assertEquals(payload, response.getData());
        assertNull(response.getError());

        assertNotNull(response.getTimestamp());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        assertDoesNotThrow(() -> formatter.parse(response.getTimestamp()));
    }

    @Test
    void testErrorResponseFields() {
        ErrorPayload error = ErrorPayload.builder()
                .status(400)
                .error("Bad Request")
                .message("Invalid input")
                .path("/api/primes")
                .build();

        APIResponse response = APIResponse.error(error, 400);

        log.info("Testing error response: {}", response);
        assertEquals(400, response.getHttpStatus());
        assertEquals(error, response.getError());
        assertNull(response.getData());

        assertNotNull(response.getTimestamp());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        assertDoesNotThrow(() -> formatter.parse(response.getTimestamp()));
    }
    @Test
    void testJsonSerialization() throws JsonProcessingException {
        APIResponse response = APIResponse.success(null, 200);
        ObjectMapper mapper = new ObjectMapper();

        String json = mapper.writeValueAsString(response);
        log.info("Serialized JSON: {}", json);

        assertTrue(json.contains("\"timestamp\":\"" + response.getTimestamp() + "\""));
    }

    @Test
    void testXmlSerialization() throws JsonProcessingException {
        APIResponse response = APIResponse.success(null, 200);
        XmlMapper xmlMapper = new XmlMapper();

        String xml = xmlMapper.writeValueAsString(response);
        log.info("Serialized XML: {}", xml);

        assertTrue(xml.contains("<timestamp>" + response.getTimestamp() + "</timestamp>"));
    }
}