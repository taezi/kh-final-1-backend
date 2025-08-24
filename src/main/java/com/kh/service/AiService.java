package com.kh.service;


import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class AiService {
    private final Client client;

    public AiService(@Value("${google.genai.api-key}") String apiKey) {
        this.client = Client.builder()
                .apiKey(apiKey)  // application.properties 값 사용
                .build();
    }

    /**
     * Gemini AI에게 텍스트를 입력하면 응답 텍스트를 반환
     * @param prompt AI에게 요청할 질문/명령
     * @return AI 응답 텍스트
     */
    public String generateText(String prompt) {
        try {
            GenerateContentResponse response = client.models.generateContent(
                    "gemini-2.5-flash",
                    prompt,
                    null
            );
            return response.text();
        } catch (Exception e) {
            e.printStackTrace();
            return "AI 요청 중 오류 발생";
        }
    }






}
