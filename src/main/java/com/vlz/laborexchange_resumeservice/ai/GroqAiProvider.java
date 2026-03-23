package com.vlz.laborexchange_resumeservice.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vlz.laborexchange_resumeservice.dto.AiResumeRequestDto;
import com.vlz.laborexchange_resumeservice.dto.AiResumeResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@ConditionalOnProperty(name = "ai.provider", havingValue = "groq")
@RequiredArgsConstructor
public class GroqAiProvider implements AiProvider {

    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${ai.groq.api-key}")
    private String apiKey;

    @Value("${ai.groq.model:llama-3.3-70b-versatile}")
    private String model;

    @Value("${ai.groq.max-tokens:2048}")
    private int maxTokens;

    @Override
    public AiResumeResponseDto generate(AiResumeRequestDto request) {
        String rawJson = callGroq(request);
        return parseResponse(rawJson);
    }

    private String callGroq(AiResumeRequestDto req) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = Map.of(
                "model", model,
                "max_tokens", maxTokens,
                "messages", List.of(
                        Map.of("role", "system", "content", AiPromptBuilder.SYSTEM_PROMPT),
                        Map.of("role", "user", "content", AiPromptBuilder.buildUserPrompt(req))
                )
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        String response = restTemplate.postForObject(API_URL, entity, String.class);

        try {
            JsonNode root = objectMapper.readTree(response);
            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (JsonProcessingException e) {
            log.error("Failed to parse Groq API response", e);
            throw new IllegalStateException("Invalid response from Groq API", e);
        }
    }

    private AiResumeResponseDto parseResponse(String json) {
        String cleaned = json.strip();
        if (cleaned.startsWith("```")) {
            cleaned = cleaned.replaceAll("^```[a-z]*\\n?", "").replaceAll("```$", "").strip();
        }
        try {
            return objectMapper.readValue(cleaned, AiResumeResponseDto.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize Groq response JSON: {}", cleaned, e);
            throw new IllegalStateException("AI returned unparseable JSON", e);
        }
    }
}
