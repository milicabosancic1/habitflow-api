package com.habitflow.api.controller;

import com.habitflow.api.dto.WeeklyInsightRequest;
import com.habitflow.api.dto.WeeklyInsightResponse;
import com.habitflow.api.service.AiInsightService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiInsightService service;

    public AiController(AiInsightService service) {
        this.service = service;
    }

    @PostMapping("/weekly-insight")
    public WeeklyInsightResponse weeklyInsight(@Valid @RequestBody WeeklyInsightRequest request) {
        return new WeeklyInsightResponse(service.generateWeeklyInsight(request));
    }
}
