package com.habitflow.api.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Svaka grana mora vratiti isti JSON format ({timestamp, status, error, message}),
 * uključujući i fallback za neočekivane greške - inače klijent (Android) ne može
 * pouzdano da parsira odgovor kad nešto pukne neplanirano.
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleUnexpected_returns500_withGenericMessage_notStackTrace() {
        ResponseEntity<Map<String, Object>> response = handler.handleUnexpected(new NullPointerException("interni detalj koji ne sme da procuri"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("status")).isEqualTo(500);
        assertThat(body.get("error")).isEqualTo("Internal Server Error");
        assertThat(body.get("message")).isEqualTo("Došlo je do neočekivane greške");
        assertThat(body).containsKey("timestamp");
    }

    @Test
    void handleNotFound_returns404_withExceptionMessage() {
        ResponseEntity<Map<String, Object>> response = handler.handleNotFound(new NotFoundException("Navika nije pronađena"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().get("message")).isEqualTo("Navika nije pronađena");
    }

    @Test
    void handleBadGateway_returns502() {
        ResponseEntity<Map<String, Object>> response = handler.handleBadGateway(new BadGatewayException("LLM provajder nije dostupan"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
    }

    @Test
    void handleGatewayTimeout_returns504() {
        ResponseEntity<Map<String, Object>> response = handler.handleGatewayTimeout(new GatewayTimeoutException("LLM provajder nije odgovorio na vreme"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.GATEWAY_TIMEOUT);
    }
}
