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
@ConditionalOnProperty(name = "ai.provider", havingValue = "anthropic", matchIfMissing = true)
@RequiredArgsConstructor
public class AnthropicAiProvider implements AiProvider {

    private static final String API_URL = "https://api.anthropic.com/v1/messages";
    private static final String ANTHROPIC_VERSION = "2023-06-01";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${ai.anthropic.api-key}")
    private String apiKey;

    @Value("${ai.anthropic.model:claude-opus-4-6}")
    private String model;

    @Value("${ai.anthropic.max-tokens:2048}")
    private int maxTokens;

    @Override
    public AiResumeResponseDto generate(AiResumeRequestDto request) {
        String rawJson = callAnthropic(AiPromptBuilder.buildUserPrompt(request));
        return parseResponse(rawJson);
    }

    private String callAnthropic(String userPrompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", apiKey);
        headers.set("anthropic-version", ANTHROPIC_VERSION);

        Map<String, Object> body = Map.of(
                "model", model,
                "max_tokens", maxTokens,
                "system", AiPromptBuilder.SYSTEM_PROMPT,
                "messages", List.of(Map.of("role", "user", "content", userPrompt))
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        String response = restTemplate.postForObject(API_URL, entity, String.class);

        try {
            JsonNode root = objectMapper.readTree(response);
            return root.path("content").get(0).path("text").asText();
        } catch (JsonProcessingException e) {
            log.error("Failed to parse Anthropic API response", e);
            throw new IllegalStateException("Invalid response from Anthropic API", e);
        }
    }

    private AiResumeResponseDto parseResponse(String json) {
        // Strip markdown code fences if model wraps in ```json ... ```
        String cleaned = json.strip();
        if (cleaned.startsWith("```")) {
            cleaned = cleaned.replaceAll("^```[a-z]*\\n?", "").replaceAll("```$", "").strip();
        }
        try {
            return objectMapper.readValue(cleaned, AiResumeResponseDto.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize AI response JSON: {}", cleaned, e);
            throw new IllegalStateException("AI returned unparseable JSON", e);
        }
    }
}
