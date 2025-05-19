// src/main/java/com/example/greenhouseapplication/backend/service/AIAnalysisService.java
package com.example.greenhouseapplication.backend.service;

import com.example.greenhouseapplication.backend.model.SensorAnalysis;
import com.example.greenhouseapplication.backend.model.SensorData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AIAnalysisService {
    private static final Logger log = LoggerFactory.getLogger(AIAnalysisService.class);

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.url}")
    private String apiUrl;

    @Value("${groq.model}")
    private String modelName;

    /**
     * Analyze one batch for a single sensorType.
     *
     * @param ghId       the greenhouse ID (for storage only)
     * @param batch      the SensorData batch (chronologically ordered)
     * @param sensorType one of "temperature","humidity","lightIntensity","co2Levels","soilMoisture"
     * @return a SensorAnalysis ready to save
     */
    public SensorAnalysis analyzeWindow(String ghId, List<SensorData> batch, String sensorType) {
        double avg = batch.stream()
                .mapToDouble(d -> extractByType(d, sensorType))
                .average().orElse(0.0);
        double min = batch.stream()
                .mapToDouble(d -> extractByType(d, sensorType))
                .min().orElse(0.0);
        double max = batch.stream()
                .mapToDouble(d -> extractByType(d, sensorType))
                .max().orElse(0.0);

        // prompt no longer mentions raw ID, and asks for actions as strings
        String prompt = """
            You are an AI assistant producing JSON only.
            Sensor: %s
            Last %d readings → avg=%4.1f, min=%4.1f, max=%4.1f
            Respond with a JSON object containing exactly:
              - "summary": a brief string (no greenhouse IDs)
              - "recommendations": an array of strings
              - "actions": an array of strings (e.g. "Adjust temperature settings")
            No extra keys, no booleans, no explanations.
            """.formatted(
                sensorType,
                batch.size(),
                avg, min, max
        );

        List<Map<String,String>> messages = List.of(
                Map.of("role","system","content","You output strictly JSON, no commentary."),
                Map.of("role","user","content",prompt)
        );

        Map<String,Object> payload = Map.of(
                "model", modelName,
                "messages", messages,
                "response_format", Map.of("type","json_object")
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String,Object>> request = new HttpEntity<>(payload, headers);

        ResponseEntity<String> response;
        try {
            response = restTemplate.postForEntity(apiUrl, request, String.class);
        } catch (HttpClientErrorException.BadRequest bad) {
            log.warn("AI BadRequest, retrying once: {}", bad.getResponseBodyAsString());
            response = restTemplate.postForEntity(apiUrl, request, String.class);
        }

        return parseResponse(ghId, batch, sensorType, response.getBody());
    }

    private double extractByType(SensorData d, String type) {
        return switch (type) {
            case "temperature"    -> d.getReadings().getTemperature();
            case "humidity"       -> d.getReadings().getHumidity();
            case "lightIntensity" -> d.getReadings().getLightIntensity();
            case "co2Levels"      -> d.getReadings().getCo2Levels();
            case "soilMoisture"   -> d.getReadings().getSoilMoisture();
            default               -> 0.0;
        };
    }

    private SensorAnalysis parseResponse(
            String ghId,
            List<SensorData> batch,
            String sensorType,
            String body
    ) {
        try {
            JsonNode root    = mapper.readTree(body);
            JsonNode content = root.path("choices").get(0).path("message").path("content");
            String text      = content.isTextual() ? content.asText() : content.toString();
            String json      = text.replaceAll("(?s)^```json\\s*|\\s*```$", "");
            JsonNode o       = mapper.readTree(json);

            SensorAnalysis sa = new SensorAnalysis();
            sa.setGreenhouseId(ghId);
            sa.setSensorType(sensorType);
            sa.setIntervalStart(batch.get(0).getRecordedAt());
            sa.setIntervalEnd(batch.get(batch.size()-1).getRecordedAt());
            sa.setAnalysisTimestamp(LocalDateTime.now(ZoneOffset.UTC));

            sa.setSummary(o.path("summary").asText());

            // recommendations
            List<String> recs = new ArrayList<>();
            JsonNode recNode = o.path("recommendations");
            if (recNode.isArray()) recNode.forEach(n -> recs.add(n.asText()));
            else recs.add(recNode.asText());
            sa.setRecommendations(recs);

            // actions as array of strings
            List<String> acts = new ArrayList<>();
            JsonNode actNode = o.path("actions");
            if (actNode.isArray()) actNode.forEach(n -> acts.add(n.asText()));
            else acts.add(actNode.asText());
            sa.setActions(acts);

            return sa;
        } catch (Exception ex) {
            log.error("Failed to parse AI response: {}\n{}", ex.getMessage(), body);
            throw new RuntimeException("AI parse error", ex);
        }
    }
}
