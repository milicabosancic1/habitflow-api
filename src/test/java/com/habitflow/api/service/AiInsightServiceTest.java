package com.habitflow.api.service;

import com.habitflow.api.dto.WeeklyInsightHabitDto;
import com.habitflow.api.dto.WeeklyInsightRequest;
import com.habitflow.api.exception.BadGatewayException;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Poziv ka Anthropic Messages API-ju je opciono/aditivno (Android tiho pada na
 * fallback), zato je fokus testa na tri ishoda koje Android strana razlikuje:
 * uspeh, 502 (provajder nedostupan/vratio grešku) i nedostajući ključ -> 502.
 */
class AiInsightServiceTest {

    private WeeklyInsightRequest sampleRequest() {
        WeeklyInsightRequest req = new WeeklyInsightRequest();
        req.setThisWeekPct(82);
        req.setLastWeekPct(70);
        WeeklyInsightHabitDto habit = new WeeklyInsightHabitDto();
        habit.setHabitName("Trčanje");
        habit.setCategory("Zdravlje");
        habit.setThisWeekPct(90);
        habit.setCurrentStreak(5);
        req.setHabits(List.of(habit));
        return req;
    }

    @Test
    void generateWeeklyInsight_returnsMessage_whenAnthropicRespondsWithText() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        AiInsightService service = new AiInsightService(builder, "test-key", "claude-haiku-4-5-20251001");

        server.expect(method(org.springframework.http.HttpMethod.POST))
                .andExpect(header("x-api-key", "test-key"))
                .andExpect(header("anthropic-version", "2023-06-01"))
                .andRespond(withSuccess("""
                        {"content":[{"type":"text","text":"Sjajna nedelja!"}]}
                        """, MediaType.APPLICATION_JSON));

        String message = service.generateWeeklyInsight(sampleRequest());

        assertThat(message).isEqualTo("Sjajna nedelja!");
        server.verify();
    }

    @Test
    void generateWeeklyInsight_throwsBadGateway_whenApiKeyMissing() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer.bindTo(builder).build();
        AiInsightService service = new AiInsightService(builder, "", "claude-haiku-4-5-20251001");

        assertThatThrownBy(() -> service.generateWeeklyInsight(sampleRequest()))
                .isInstanceOf(BadGatewayException.class);
    }

    @Test
    void generateWeeklyInsight_throwsBadGateway_whenProviderReturnsServerError() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        AiInsightService service = new AiInsightService(builder, "test-key", "claude-haiku-4-5-20251001");

        server.expect(method(org.springframework.http.HttpMethod.POST))
                .andRespond(withServerError());

        assertThatThrownBy(() -> service.generateWeeklyInsight(sampleRequest()))
                .isInstanceOf(BadGatewayException.class);
        server.verify();
    }

    @Test
    void generateWeeklyInsight_throwsBadGateway_whenResponseHasNoText() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        AiInsightService service = new AiInsightService(builder, "test-key", "claude-haiku-4-5-20251001");

        server.expect(method(org.springframework.http.HttpMethod.POST))
                .andRespond(withSuccess("""
                        {"content":[]}
                        """, MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> service.generateWeeklyInsight(sampleRequest()))
                .isInstanceOf(BadGatewayException.class);
        server.verify();
    }
}
