package com.kh.controller;

import com.kh.service.AiService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping("/api/ai")
@RestController
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/generate")
    public String askAi(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        String response = aiService.generateText(prompt);
        response = response.replace("```json", "").replace("```", "").trim();
        return response;
    }
}
