package com.habitflow.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Nizak limit (3) samo za ovaj test, izolovano od ostalih testova preko
 * @TestPropertySource (dobija sopstveni Spring kontekst, pa i sopstvenu
 * in-memory bucket mapu — ne meša se sa AuthIntegrationTest pozivima).
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev-h2")
@TestPropertySource(properties = {
        "app.security.rate-limit.capacity=3",
        "app.security.rate-limit.refill-seconds=60"
})
class RateLimitFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void exceedingLoginAttempts_returnsTooManyRequests() throws Exception {
        String body = objectMapper.writeValueAsString(
                Map.of("email", "nepostoji@habitflow.dev", "password", "pogresna"));

        for (int i = 0; i < 3; i++) {
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isUnauthorized());
        }

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().is(429));
    }
}
