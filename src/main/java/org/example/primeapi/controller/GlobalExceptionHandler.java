package org.example.primeapi.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.primeapi.model.APIResponse;
import org.example.primeapi.model.ErrorPayload;
import org.example.primeapi.util.ErrorResponseBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<APIResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String param = ex.getName();
        String expected = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        String received = ex.getValue() != null ? ex.getValue().toString() : "null";

        String message = String.format("Invalid value for parameter '%s': expected '%s', but received '%s'", param, expected, received);
        log.warn("⚠️ Type mismatch on '{}': expected {}, got '{}'", param, expected, received);

        return ResponseEntity.status(400).body(APIResponse.error(ErrorResponseBuilder.badRequest(message, request), 400));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<APIResponse> handleMissingParam(MissingServletRequestParameterException ex, HttpServletRequest request) {
        String param = ex.getParameterName();
        String expectedType = ex.getParameterType();

        String message = String.format("Missing required parameter '%s' of type '%s'", param, expectedType);
        log.warn("⚠️ Missing parameter '{}': expected type '{}'", param, expectedType);

        return ResponseEntity.status(400).body(APIResponse.error(ErrorResponseBuilder.badRequest(message, request), 400));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<APIResponse> handleNotFound(HttpServletRequest request) {
        log.warn("Unhandled path: {}", request.getRequestURI());
        ErrorPayload error = ErrorResponseBuilder.notFound("Unknown path: " + request.getRequestURI(), request);
        return ResponseEntity.status(404).body(APIResponse.error(error, 404));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception at '{}': {}", request.getRequestURI(), ex.getMessage(), ex);
        ErrorPayload error = ErrorResponseBuilder.internalServerError("Unexpected error occurred", request);
        return ResponseEntity.status(500).body(APIResponse.error(error, 500));
    }

}