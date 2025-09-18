package org.example.primeapi.util;

import lombok.extern.slf4j.Slf4j;
import org.example.primeapi.model.ErrorPayload;
import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
class ErrorResponseBuilderTest {

    private HttpServletRequest mockRequest(String uri) {
        logTestStep("Mocking HttpServletRequest with URI: " + uri);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn(uri);
        return request;
    }

    private void assertPayload(ErrorPayload payload, int status, String label, String message, String path) {
        logTestStep("Asserting payload: " + payload);
        assertEquals(status, payload.getStatus());
        assertEquals(label, payload.getError());
        assertEquals(message, payload.getMessage());
        assertEquals(path, payload.getPath());
    }

    private void logTestStep(String description) {
        log.info("🔍 {}", description);
    }

    @Test
    void testBuildCreatesCorrectPayload() {
        ErrorPayload payload = ErrorResponseBuilder.build("Invalid input", "/api/primes", 422, "Unprocessable Entity");
        assertPayload(payload, 422, "Unprocessable Entity", "Invalid input", "/api/primes");
    }

    @Test
    void testBadRequestUsesRequestUri() {
        ErrorPayload payload = ErrorResponseBuilder.badRequest("Missing parameters", mockRequest("/api/test"));
        assertPayload(payload, 400, "Bad Request", "Missing parameters", "/api/test");
    }

    @Test
    void testNotFoundPayload() {
        ErrorPayload payload = ErrorResponseBuilder.notFound("Resource not found", mockRequest("/api/missing"));
        assertPayload(payload, 404, "Not Found", "Resource not found", "/api/missing");
    }

    @Test
    void testUnauthorizedPayload() {
        ErrorPayload payload = ErrorResponseBuilder.unauthorized("Authentication required", mockRequest("/api/secure"));
        assertPayload(payload, 401, "Unauthorized", "Authentication required", "/api/secure");
    }

    @Test
    void testForbiddenPayload() {
        ErrorPayload payload = ErrorResponseBuilder.forbidden("Access denied", mockRequest("/api/admin"));
        assertPayload(payload, 403, "Forbidden", "Access denied", "/api/admin");
    }

    @Test
    void testMethodNotAllowedPayload() {
        ErrorPayload payload = ErrorResponseBuilder.methodNotAllowed("POST not supported", mockRequest("/api/primes"));
        assertPayload(payload, 405, "Method Not Allowed", "POST not supported", "/api/primes");
    }

    @Test
    void testConflictPayload() {
        ErrorPayload payload = ErrorResponseBuilder.conflict("Resource already exists", mockRequest("/api/resource"));
        assertPayload(payload, 409, "Conflict", "Resource already exists", "/api/resource");
    }

    @Test
    void testUnprocessableEntityPayload() {
        ErrorPayload payload = ErrorResponseBuilder.unprocessableEntity("Invalid format", mockRequest("/api/data"));
        assertPayload(payload, 422, "Unprocessable Entity", "Invalid format", "/api/data");
    }

    @Test
    void testInternalServerErrorPayload() {
        ErrorPayload payload = ErrorResponseBuilder.internalServerError("Unexpected error", mockRequest("/api/failure"));
        assertPayload(payload, 500, "Internal Server Error", "Unexpected error", "/api/failure");
    }

    @Test
    void testServiceUnavailablePayload() {
        ErrorPayload payload = ErrorResponseBuilder.serviceUnavailable("Service temporarily unavailable", mockRequest("/api/maintenance"));
        assertPayload(payload, 503, "Service Unavailable", "Service temporarily unavailable", "/api/maintenance");
    }

    @Test
    void testBuildWithNullValues() {
        logTestStep("Testing build() with null values");
        ErrorPayload payload = ErrorResponseBuilder.build(null, null, 500, null);
        assertEquals(500, payload.getStatus());
        assertNull(payload.getError());
        assertNull(payload.getMessage());
        assertNull(payload.getPath());
    }
}