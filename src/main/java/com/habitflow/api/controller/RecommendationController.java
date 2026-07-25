package com.habitflow.api.controller;

import com.habitflow.api.dto.RecommendationDto;
import com.habitflow.api.security.CurrentUser;
import com.habitflow.api.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService service;

    public RecommendationController(RecommendationService service) {
        this.service = service;
    }

    @GetMapping
    public List<RecommendationDto> getActive() {
        return service.getActive(CurrentUser.id());
    }

    @PostMapping("/{id}/dismiss")
    public ResponseEntity<Void> dismiss(@PathVariable String id) {
        service.dismiss(CurrentUser.id(), id);
        return ResponseEntity.noContent().build();
    }
}
