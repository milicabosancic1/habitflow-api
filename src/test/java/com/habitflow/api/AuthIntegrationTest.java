package com.habitflow.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Proverava register/login preko stvarnog HTTP sloja, uklj. da pogrešna
 * lozinka vraća 401 (a ne 400) — regresija za api-spec.md.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev-h2")
@Transactional
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String json(Object body) throws Exception {
        return objectMapper.writeValueAsString(body);
    }

    private String field(MvcResult result, String field) throws Exception {
        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        return node.get(field).asText();
    }

    private String registerAndGetRefreshToken(String email) throws Exception {
        var registerBody = Map.of(
                "email", email,
                "password", "lozinka123",
                "displayName", "Test Korisnik"
        );
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(registerBody)))
                .andExpect(status().isCreated())
                .andReturn();
        return field(result, "refreshToken");
    }

    @Test
    void register_thenLogin_succeeds() throws Exception {
        var registerBody = Map.of(
                "email", "test-user@habitflow.dev",
                "password", "lozinka123",
                "displayName", "Test Korisnik"
        );
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(registerBody)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.userId").exists());

        var loginBody = Map.of("email", "test-user@habitflow.dev", "password", "lozinka123");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(loginBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    void register_withDuplicateEmail_returns400() throws Exception {
        var registerBody = Map.of(
                "email", "dup@habitflow.dev",
                "password", "lozinka123",
                "displayName", "Prvi"
        );
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(registerBody)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(registerBody)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_withWrongPassword_returns401() throws Exception {
        var registerBody = Map.of(
                "email", "wrongpass@habitflow.dev",
                "password", "ispravna-lozinka",
                "displayName", "Neko"
        );
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(registerBody)))
                .andExpect(status().isCreated());

        var loginBody = Map.of("email", "wrongpass@habitflow.dev", "password", "pogresna-lozinka");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(loginBody)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_withUnknownEmail_returns401() throws Exception {
        var loginBody = Map.of("email", "nepostojeci@habitflow.dev", "password", "bilokoja");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(loginBody)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refresh_withValidToken_rotatesAndReturnsNewPair() throws Exception {
        String refreshToken = registerAndGetRefreshToken("refresh-ok@habitflow.dev");

        MvcResult result = mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("refreshToken", refreshToken))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andReturn();

        assertThat(field(result, "refreshToken")).isNotEqualTo(refreshToken);
    }

    @Test
    void refresh_withAlreadyRotatedToken_returns401() throws Exception {
        String refreshToken = registerAndGetRefreshToken("refresh-reuse@habitflow.dev");

        // prva upotreba - rotira token (stari se opoziva)
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("refreshToken", refreshToken))))
                .andExpect(status().isOk());

        // ponovna upotreba istog (vec opozvanog) tokena mora biti odbijena
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("refreshToken", refreshToken))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refresh_withUnknownToken_returns401() throws Exception {
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("refreshToken", "nepostojeci-token"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logout_thenRefresh_returns401() throws Exception {
        String refreshToken = registerAndGetRefreshToken("logout@habitflow.dev");

        mockMvc.perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("refreshToken", refreshToken))))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("refreshToken", refreshToken))))
                .andExpect(status().isUnauthorized());
    }
}
