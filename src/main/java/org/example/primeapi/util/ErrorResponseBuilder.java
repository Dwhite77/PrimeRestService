package org.example.primeapi.util;

import jakarta.servlet.http.HttpServletRequest;
import org.example.primeapi.model.ErrorPayload;

import java.time.Instant;

public class ErrorResponseBuilder {

    public static ErrorPayload badRequest(String message, HttpServletRequest request) {
        return build(message, request.getRequestURI(), 400, "Bad Request");
    }

    public static ErrorPayload build(String message, String path, int status, String errorLabel) {
        return ErrorPayload.builder()
                .status(status)
                .error(errorLabel)
                .message(message)
                .path(path)
                .timestamp(Instant.now())
                .build();
    }
}