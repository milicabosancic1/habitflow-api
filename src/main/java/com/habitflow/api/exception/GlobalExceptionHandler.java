package com.habitflow.api.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private Map<String, Object> body(HttpStatus status, String message) {
        Map<String, Object> m = new HashMap<>();
        m.put("timestamp", System.currentTimeMillis());
        m.put("status", status.value());
        m.put("error", status.getReasonPhrase());
        m.put("message", message);
        return m;
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(body(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(body(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorized(UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(body(HttpStatus.UNAUTHORIZED, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .orElse("Neispravan zahtev");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(body(HttpStatus.BAD_REQUEST, msg));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(body(HttpStatus.UNAUTHORIZED, ex.getMessage()));
    }

    @ExceptionHandler(BadGatewayException.class)
    public ResponseEntity<Map<String, Object>> handleBadGateway(BadGatewayException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(body(HttpStatus.BAD_GATEWAY, ex.getMessage()));
    }

    @ExceptionHandler(GatewayTimeoutException.class)
    public ResponseEntity<Map<String, Object>> handleGatewayTimeout(GatewayTimeoutException ex) {
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                .body(body(HttpStatus.GATEWAY_TIMEOUT, ex.getMessage()));
    }

    /** Fallback za sve neočekivane greške - i one moraju vratiti isti JSON format, ne goli stack trace. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpected(Exception ex) {
        log.error("Neočekivana greška", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body(HttpStatus.INTERNAL_SERVER_ERROR, "Došlo je do neočekivane greške"));
    }
}
