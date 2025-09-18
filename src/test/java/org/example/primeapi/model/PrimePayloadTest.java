package org.example.primeapi.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PrimePayloadTest {

    @Test
    void builderSetsAllFieldsCorrectly() {
        PrimePayload payload = PrimePayload.builder()
                .algorithm("sieve")
                .limit(100)
                .threads(4)
                .primes(List.of(2, 3, 5, 7))
                .total(4)
                .durationMs(12)
                .build();

        assertEquals("sieve", payload.getAlgorithm());
        assertEquals(100, payload.getLimit());
        assertEquals(4, payload.getThreads());
        assertEquals(List.of(2, 3, 5, 7), payload.getPrimes());
        assertEquals(4, payload.getTotal());
        assertEquals(12, payload.getDurationMs());
    }

    @Test
    void jsonSerializationIncludesAllFields() throws JsonProcessingException {
        PrimePayload payload = PrimePayload.builder()
                .algorithm("trial")
                .limit(50)
                .threads(2)
                .primes(List.of(2, 3, 5))
                .total(3)
                .durationMs(8)
                .build();

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(payload);

        assertTrue(json.contains("\"algorithm\":\"trial\""));
        assertTrue(json.contains("\"limit\":50"));
        assertTrue(json.contains("\"threads\":2"));
        assertTrue(json.contains("\"primes\":[2,3,5]"));
        assertTrue(json.contains("\"total\":3"));
        assertTrue(json.contains("\"durationMs\":8"));
    }

    @Test
    void xmlSerializationIncludesWrappedPrimesList() throws JsonProcessingException {
        PrimePayload payload = PrimePayload.builder()
                .algorithm("sieve")
                .limit(100)
                .threads(4)
                .primes(List.of(2, 3, 5, 7))
                .total(4)
                .durationMs(12)
                .build();

        XmlMapper xmlMapper = new XmlMapper();
        String xml = xmlMapper.writeValueAsString(payload);

        assertTrue(xml.contains("<algorithm>sieve</algorithm>"));
        assertTrue(xml.contains("<limit>100</limit>"));
        assertTrue(xml.contains("<threads>4</threads>"));
        assertTrue(xml.contains("<primes>"));
        assertTrue(xml.contains("<prime>2</prime>"));
        assertTrue(xml.contains("<prime>3</prime>"));
        assertTrue(xml.contains("<prime>5</prime>"));
        assertTrue(xml.contains("<prime>7</prime>"));
        assertTrue(xml.contains("<total>4</total>"));
        assertTrue(xml.contains("<durationMs>12</durationMs>"));
    }

    @Test
    void nullPrimesListIsExcludedFromJson() throws JsonProcessingException {
        PrimePayload payload = PrimePayload.builder()
                .algorithm("trial")
                .limit(10)
                .threads(1)
                .primes(null)
                .total(0)
                .durationMs(5)
                .build();

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(payload);

        assertFalse(json.contains("\"primes\""));
    }
}