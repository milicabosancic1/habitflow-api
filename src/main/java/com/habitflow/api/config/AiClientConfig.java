package com.habitflow.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Postavlja connect/read timeout za sve RestClient.Builder instance koje Spring
 * ubrizgava (trenutno koristi samo AiInsightService, za poziv ka LLM provajderu).
 * Odvojeno od servisa da bi RestClient.Builder ostao lako testljiv sa
 * MockRestServiceServer (koji sam postavlja request factory na builder).
 */
@Configuration
public class AiClientConfig {

    @Bean
    public RestClientCustomizer anthropicTimeoutCustomizer(
            @Value("${app.ai.anthropic.timeout-ms}") long timeoutMs) {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofMillis(timeoutMs))
                .withReadTimeout(Duration.ofMillis(timeoutMs));
        return builder -> builder.requestFactory(ClientHttpRequestFactories.get(settings));
    }
}
