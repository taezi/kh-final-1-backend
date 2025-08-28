package com.kh.controller;

import com.kh.service.AiService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/ai")
@RestController
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @GetMapping("/generate")
    public String askAi(@RequestParam String prompt) {
        String response = aiService.generateText(prompt);
        response = response.replace("```json", "").replace("```", "").trim();
        return response;
    }
}
