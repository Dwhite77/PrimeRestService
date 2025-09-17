package org.example.primeapi.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.primeapi.model.APIResponse;
import org.example.primeapi.util.ErrorResponseBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;



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

}