package org.example.primeapi.util;

import lombok.extern.slf4j.Slf4j;
import org.example.primeapi.model.ErrorPayload;
import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpServletRequest;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
class ErrorResponseBuilderTest {

    @Test
    void testBuildCreatesCorrectPayload() {
        String message = "Invalid input";
        String path = "/api/primes";
        int status = 422;
        String label = "Unprocessable Entity";

        log.info("Testing build() with status={}, label='{}'", status, label);
        ErrorPayload payload = ErrorResponseBuilder.build(message, path, status, label);

        log.info("Generated payload: {}", payload);
        assertEquals(status, payload.getStatus());
        assertEquals(label, payload.getError());
        assertEquals(message, payload.getMessage());
        assertEquals(path, payload.getPath());

    }

    @Test
    void testBadRequestUsesRequestUri() {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getRequestURI()).thenReturn("/api/test");

        String message = "Missing parameters";
        log.info("Testing badRequest() with URI='/api/test'");
        ErrorPayload payload = ErrorResponseBuilder.badRequest(message, mockRequest);

        log.info("Generated payload: {}", payload);
        assertEquals(400, payload.getStatus());
        assertEquals("Bad Request", payload.getError());
        assertEquals(message, payload.getMessage());
        assertEquals("/api/test", payload.getPath());

    }

    @Test
    void testBuildWithNullValues() {
        log.info("Testing build() with null values");
        ErrorPayload payload = ErrorResponseBuilder.build(null, null, 500, null);

        log.info("Generated payload: {}", payload);
        assertEquals(500, payload.getStatus());
        assertNull(payload.getError());
        assertNull(payload.getMessage());
        assertNull(payload.getPath());

    }

    @Test
    void testNotFoundPayload() {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getRequestURI()).thenReturn("/api/missing");

        log.info("Testing notFound() with URI='/api/missing'");
        ErrorPayload payload = ErrorResponseBuilder.notFound("Resource not found", mockRequest);

        log.info("Generated payload: {}", payload);
        assertEquals(404, payload.getStatus());
        assertEquals("Not Found", payload.getError());
        assertEquals("Resource not found", payload.getMessage());
        assertEquals("/api/missing", payload.getPath());
    }
}