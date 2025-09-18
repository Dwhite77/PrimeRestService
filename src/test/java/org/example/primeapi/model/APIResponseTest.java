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

    @Test
    void testErrorPayloadSerializationInsideApiResponseJson() throws JsonProcessingException {
        ErrorPayload error = ErrorPayload.builder()
                .status(400)
                .error("Bad Request")
                .message("Missing required parameter 'limit'")
                .path("/api/primes")
                .build();

        APIResponse response = APIResponse.error(error, 400);
        ObjectMapper mapper = new ObjectMapper();

        String json = mapper.writeValueAsString(response);
        log.info("Serialized error JSON: {}", json);

        assertTrue(json.contains("\"error\""));
        assertTrue(json.contains("\"status\":400"));
        assertTrue(json.contains("\"message\":\"Missing required parameter 'limit'\""));
        assertTrue(json.contains("\"path\":\"/api/primes\""));
    }

    @Test
    void testErrorPayloadSerializationInsideApiResponseXml() throws JsonProcessingException {
        ErrorPayload error = ErrorPayload.builder()
                .status(500)
                .error("Internal Server Error")
                .message("Unexpected error occurred")
                .path("/api/failure")
                .build();

        APIResponse response = APIResponse.error(error, 500);
        XmlMapper xmlMapper = new XmlMapper();

        String xml = xmlMapper.writeValueAsString(response);
        log.info("Serialized error XML: {}", xml);

        assertTrue(xml.contains("<error>"));
        assertTrue(xml.contains("<status>500</status>"));
        assertTrue(xml.contains("<message>Unexpected error occurred</message>"));
        assertTrue(xml.contains("<path>/api/failure</path>"));
    }

    @Test
    void testPrimePayloadSerializationInsideApiResponseJson() throws JsonProcessingException {
        PrimePayload payload = PrimePayload.builder()
                .algorithm("sieve")
                .limit(100)
                .threads(4)
                .primes(List.of(2, 3, 5, 7))
                .total(4)
                .durationMs(12)
                .build();

        APIResponse response = APIResponse.success(payload, 200);
        ObjectMapper mapper = new ObjectMapper();

        String json = mapper.writeValueAsString(response);
        log.info("Serialized PrimePayload JSON: {}", json);

        assertTrue(json.contains("\"data\""));
        assertTrue(json.contains("\"algorithm\":\"sieve\""));
        assertTrue(json.contains("\"limit\":100"));
        assertTrue(json.contains("\"threads\":4"));
        assertTrue(json.contains("\"primes\":[2,3,5,7]"));
        assertTrue(json.contains("\"total\":4"));
        assertTrue(json.contains("\"durationMs\":12"));
    }

    @Test
    void testPrimePayloadSerializationInsideApiResponseXml() throws JsonProcessingException {
        PrimePayload payload = PrimePayload.builder()
                .algorithm("trial")
                .limit(50)
                .threads(2)
                .primes(List.of(2, 3, 5))
                .total(3)
                .durationMs(8)
                .build();

        APIResponse response = APIResponse.success(payload, 200);
        XmlMapper xmlMapper = new XmlMapper();

        String xml = xmlMapper.writeValueAsString(response);
        log.info("Serialized PrimePayload XML: {}", xml);

        assertTrue(xml.contains("<data>"));
        assertTrue(xml.contains("<algorithm>trial</algorithm>"));
        assertTrue(xml.contains("<limit>50</limit>"));
        assertTrue(xml.contains("<threads>2</threads>"));
        assertTrue(xml.contains("<primes>"));
        assertTrue(xml.contains("<prime>2</prime>"));
        assertTrue(xml.contains("<prime>3</prime>"));
        assertTrue(xml.contains("<prime>5</prime>"));
        assertTrue(xml.contains("<total>3</total>"));
        assertTrue(xml.contains("<durationMs>8</durationMs>"));
    }
}