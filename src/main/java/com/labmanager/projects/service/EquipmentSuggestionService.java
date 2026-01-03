package com.labmanager.projects.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EquipmentSuggestionService {

    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper om = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(EquipmentSuggestionService.class);
    
    private final String geminiKey = System.getenv("GEMINI_API_KEY");
    // select model
    private final String geminiModel = System.getenv().getOrDefault("GEMINI_MODEL", "gemini-2.5-flash-lite");

    // input: project description, list of available equipment
    // output: dto EquipmentRequest list
    public String suggestEquipment(String projectDescription, List<String> availableEquipment) throws IOException, InterruptedException {
        if (geminiKey == null || geminiKey.isBlank()) {
            throw new IllegalStateException("GEMINI_API_KEY not set");
        }

        // 1. MAKE REQUEST
        // create a list of eq, seperated with ,
        String availableListText = availableEquipment.stream().collect(Collectors.joining(", "));

        // generate prompt
        String userPrompt = "Given the following project description, return a JSON array of equipment items the project will need." +
            " Each item must be an object with fields `name` (string) and `quantity` (integer). Return ONLY a valid JSON array, no extra commentary." +
            " IMPORTANT: You may only select equipment from the available equipment list provided (case-insensitive). If an item is not available, do not include it." +
            
            "\n\nProject description: " + projectDescription + 
            "\n\nAvailable equipment (choose only from this list): " + availableListText +

            "\n\nExample output:\n" + "[{\"name\":\"Microscope\",\"quantity\":1},{\"name\":\"Gloves\",\"quantity\":20}]";

        // compose JSON body for Gemini API
        Map<String, Object> body = Map.of(
            "contents", List.of(
                Map.of("parts", List.of(
                    Map.of("text", userPrompt)
                ))
            ),
            "generationConfig", Map.of(
                "temperature", 0.2,
                "maxOutputTokens", 400
            )
        );

        String reqJson = om.writeValueAsString(body);

        // endpoint URI and request build
        String baseUri = "https://generativelanguage.googleapis.com/v1beta/models/" + geminiModel + ":generateContent";
        String uriWithKey = baseUri + "?key=" + geminiKey;
        log.info("Calling Gemini API: {}", baseUri);

        HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create(uriWithKey))
            .timeout(Duration.ofSeconds(30))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(reqJson))
            .build();

        // 2. SEND REQUEST
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());

        // 3. extract and clean response
        String contentText = extractGeminiResponse(resp.body());
        String jsonArray = cleanJsonMarkdown(contentText);

        return jsonArray;
    }

    // Gemini vrača: candidates[0] -> content -> parts[0] -> text
    private String extractGeminiResponse(String responseBody) throws IOException {
        Map<String, Object> respMap = om.readValue(responseBody, new TypeReference<Map<String, Object>>() {});
        
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) respMap.get("candidates");
        if (candidates == null || candidates.isEmpty()) throw new IOException("No candidates");
        
        Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
        if (content == null) throw new IOException("No content");
        
        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
        if (parts == null || parts.isEmpty()) throw new IOException("No parts");
        
        return (String) parts.get(0).get("text");
    }

    // Pomožna metoda za odstranjevanje ```json in ```
    private String cleanJsonMarkdown(String text) {
        text = text.trim();
        if (text.startsWith("```json")) {
            text = text.substring(7);
        } else if (text.startsWith("```")) {
            text = text.substring(3);
        }
        if (text.endsWith("```")) {
            text = text.substring(0, text.length() - 3);
        }
        return text.trim();
    }
}