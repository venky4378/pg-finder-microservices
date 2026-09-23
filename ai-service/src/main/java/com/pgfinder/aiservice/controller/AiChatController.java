package com.pgfinder.aiservice.controller;

import com.pgfinder.aiservice.dto.ChatRequest;
import com.pgfinder.aiservice.dto.ChatResponse;
import com.pgfinder.aiservice.service.AiRecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/ai")
public class AiChatController {

    private final AiRecommendationService aiRecommendationService;

    public AiChatController(AiRecommendationService aiRecommendationService) {
        this.aiRecommendationService = aiRecommendationService;
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        ChatResponse response = aiRecommendationService.processChat(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> status() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "ai-service",
                "model", "gemini-1.5-flash / RAG local fallback"
        ));
    }
}
