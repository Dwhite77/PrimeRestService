package org.example.primeapi.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.MalformedChunkCodingException;
import org.example.primeapi.model.APIResponse;
import org.example.primeapi.util.ErrorResponseBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<APIResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String message = String.format("Invalid value for parameter '%s': expected '%s', but received '%s'",
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown",
                ex.getValue() != null ? ex.getValue().toString() : "null");
        log.warn("⚠️ Type mismatch on '{}': {}", ex.getName(), message);
        return ResponseEntity.status(400).body(APIResponse.error(ErrorResponseBuilder.badRequest(message, request), 400));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<APIResponse> handleMissingParam(MissingServletRequestParameterException ex, HttpServletRequest request) {
        String message = String.format("Missing required parameter '%s' of type '%s'", ex.getParameterName(), ex.getParameterType());
        log.warn("⚠️ Missing parameter '{}': expected type '{}'", ex.getParameterName(), ex.getParameterType());
        return ResponseEntity.status(400).body(APIResponse.error(ErrorResponseBuilder.badRequest(message, request), 400));
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<APIResponse> handleMissingPathVariable(MissingPathVariableException ex, HttpServletRequest request) {
        String message = String.format("Missing path variable '%s'", ex.getVariableName());
        return ResponseEntity.status(400).body(APIResponse.error(ErrorResponseBuilder.badRequest(message, request), 400));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<APIResponse> handleUnreadableMessage(HttpMessageNotReadableException ex, HttpServletRequest request) {
        String message = "Malformed request body: " + ex.getMostSpecificCause().getMessage();
        return ResponseEntity.status(400).body(APIResponse.error(ErrorResponseBuilder.badRequest(message, request), 400));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<APIResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        String message = String.format("Method '%s' not supported. Supported methods: %s",
                ex.getMethod(), String.join(", ", ex.getSupportedMethods()));
        return ResponseEntity.status(405).body(APIResponse.error(ErrorResponseBuilder.methodNotAllowed(message, request), 405));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<APIResponse> handleResponseStatus(ResponseStatusException ex, HttpServletRequest request) {
        String message = ex.getReason() != null ? ex.getReason() : "Unexpected error";
        return ResponseEntity.status(ex.getStatusCode()).body(APIResponse.error(ErrorResponseBuilder.badRequest(message, request), ex.getStatusCode().value()));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<APIResponse> handleNotFound(HttpServletRequest request) {
        String message = "Unknown path: " + request.getRequestURI();
        log.warn("Unhandled path: {}", request.getRequestURI());
        return ResponseEntity.status(404).body(APIResponse.error(ErrorResponseBuilder.notFound(message, request), 404));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception at '{}': {}", request.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(500).body(APIResponse.error(ErrorResponseBuilder.internalServerError("Unexpected error occurred", request), 500));
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<APIResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        String message = "Illegal argument: " + ex.getMessage();
        log.warn("⚠️ IllegalArgumentException at '{}': {}", request.getRequestURI(), message);
        return ResponseEntity.status(400).body(APIResponse.error(ErrorResponseBuilder.badRequest(message, request), 400));
    }

    @ExceptionHandler(MalformedChunkCodingException.class)
    public void handleChunkError(MalformedChunkCodingException ex, HttpServletRequest request) {
        log.warn("Chunked response failed at '{}': {}", request.getRequestURI(), ex.getMessage());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<APIResponse> handleNoResourceFound(NoResourceFoundException ex, HttpServletRequest request) {
        String message = "Resource not found: " + ex.getMessage();
        log.warn("🔍 NoResourceFoundException at '{}': {}", request.getRequestURI(), message);
        return ResponseEntity.status(404).body(APIResponse.error(ErrorResponseBuilder.notFound(message, request), 404));
    }


}