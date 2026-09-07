package com.habitflow.api.service;

import com.habitflow.api.dto.WeeklyInsightHabitDto;
import com.habitflow.api.dto.WeeklyInsightRequest;
import com.habitflow.api.exception.BadGatewayException;
import com.habitflow.api.exception.GatewayTimeoutException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;

/**
 * Poziva Anthropic Claude Messages API da generiše kratku, personalizovanu nedeljnu
 * poruku (vidi docs/backend-ai-weekly-insight.md na Android strani). Endpoint nije
 * kritičan za rad sistema - Android strana tiho pada na sopstveni fallback tekst
 * ako ovaj poziv ne uspe, pa je namerno bez retry/keširanja (van obima zadatka).
 */
@Service
public class AiInsightService {

    private static final String ANTHROPIC_URL = "https://api.anthropic.com/v1/messages";
    private static final String ANTHROPIC_VERSION = "2023-06-01";

    private final RestClient restClient;
    private final String apiKey;
    private final String model;

    public AiInsightService(RestClient.Builder restClientBuilder,
                             @Value("${app.ai.anthropic.api-key:}") String apiKey,
                             @Value("${app.ai.anthropic.model}") String model) {
        this.apiKey = apiKey;
        this.model = model;
        this.restClient = restClientBuilder
                .baseUrl(ANTHROPIC_URL)
                .build();
    }

    public String generateWeeklyInsight(WeeklyInsightRequest request) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new BadGatewayException("LLM provajder nije dostupan");
        }

        Map<String, Object> body = Map.of(
                "model", model,
                "max_tokens", 200,
                "temperature", 0.7,
                "messages", List.of(Map.of("role", "user", "content", buildPrompt(request)))
        );

        AnthropicMessageResponse response;
        try {
            response = restClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", ANTHROPIC_VERSION)
                    .body(body)
                    .retrieve()
                    .body(AnthropicMessageResponse.class);
        } catch (ResourceAccessException e) {
            if (e.getCause() instanceof SocketTimeoutException) {
                throw new GatewayTimeoutException("LLM provajder nije odgovorio na vreme");
            }
            throw new BadGatewayException("LLM provajder nije dostupan");
        } catch (RestClientException e) {
            throw new BadGatewayException("LLM provajder nije dostupan");
        }

        String text = extractText(response);
        if (text == null || text.isBlank()) {
            throw new BadGatewayException("LLM provajder nije vratio validan odgovor");
        }
        return text.trim();
    }

    private String extractText(AnthropicMessageResponse response) {
        if (response == null || response.getContent() == null) {
            return null;
        }
        return response.getContent().stream()
                .filter(block -> "text".equals(block.getType()))
                .map(AnthropicMessageResponse.ContentBlock::getText)
                .findFirst()
                .orElse(null);
    }

    private String buildPrompt(WeeklyInsightRequest request) {
        StringBuilder habitsBlock = new StringBuilder();
        for (WeeklyInsightHabitDto habit : request.getHabits()) {
            habitsBlock.append("- ")
                    .append(habit.getHabitName())
                    .append(" (").append(habit.getCategory()).append("): ")
                    .append(habit.getThisWeekPct()).append("%, trenutni niz ")
                    .append(habit.getCurrentStreak()).append(" dana\n");
        }

        return """
                Ti si koučing asistent za izgradnju navika po principima knjige Atomic Habits.
                Korisnik je ove nedelje ostvario %d%% uspešnosti (prošle nedelje %d%%).
                Navike i njihova nedeljna uspešnost:
                %s
                Napiši kratku (2-3 rečenice), toplu, konkretnu poruku na srpskom koja:
                - pohvali ono što ide dobro (po imenu, ako postoji jasan izuzetak),
                - blago i podržavajuće ukaže na naviku koja najviše zaostaje (ako postoji),
                - ne bude generička ili prazna fraza.
                Vrati SAMO tekst poruke, bez uvoda i bez navodnika.
                """.formatted(request.getThisWeekPct(), request.getLastWeekPct(), habitsBlock.toString().stripTrailing());
    }
}
