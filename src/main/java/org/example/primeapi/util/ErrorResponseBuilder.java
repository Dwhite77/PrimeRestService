package org.example.primeapi.util;

import jakarta.servlet.http.HttpServletRequest;
import org.example.primeapi.model.ErrorPayload;


public class ErrorResponseBuilder {

    public static ErrorPayload badRequest(String message, HttpServletRequest request) {
        return build(message, request.getRequestURI(), 400, "Bad Request");
    }

    public static ErrorPayload unauthorized(String message, HttpServletRequest request) {
        return build(message, request.getRequestURI(), 401, "Unauthorized");
    }

    public static ErrorPayload forbidden(String message, HttpServletRequest request) {
        return build(message, request.getRequestURI(), 403, "Forbidden");
    }

    public static ErrorPayload notFound(String message, HttpServletRequest request) {
        return build(message, request.getRequestURI(), 404, "Not Found");
    }

    public static ErrorPayload conflict(String message, HttpServletRequest request) {
        return build(message, request.getRequestURI(), 409, "Conflict");
    }

    public static ErrorPayload unprocessableEntity(String message, HttpServletRequest request) {
        return build(message, request.getRequestURI(), 422, "Unprocessable Entity");
    }

    public static ErrorPayload internalServerError(String message, HttpServletRequest request) {
        return build(message, request.getRequestURI(), 500, "Internal Server Error");
    }

    public static ErrorPayload serviceUnavailable(String message, HttpServletRequest request) {
        return build(message, request.getRequestURI(), 503, "Service Unavailable");
    }

    public static ErrorPayload build(String message, String path, int status, String errorLabel) {
        return ErrorPayload.builder()
                .status(status)
                .error(errorLabel)
                .message(message)
                .path(path)
                .build();
    }
}