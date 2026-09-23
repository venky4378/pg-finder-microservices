package com.pgfinder.aiservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgfinder.aiservice.client.HostelServiceClient;
import com.pgfinder.aiservice.dto.ChatRequest;
import com.pgfinder.aiservice.dto.ChatResponse;
import com.pgfinder.aiservice.dto.HostelSummaryDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AiRecommendationService {

    private static final Logger log = LoggerFactory.getLogger(AiRecommendationService.class);

    private final HostelServiceClient hostelServiceClient;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    @Value("${gemini.model:gemini-1.5-flash}")
    private String geminiModel;

    public AiRecommendationService(HostelServiceClient hostelServiceClient, WebClient.Builder webClientBuilder) {
        this.hostelServiceClient = hostelServiceClient;
        this.webClient = webClientBuilder.build();
        this.objectMapper = new ObjectMapper();
    }

    public ChatResponse processChat(ChatRequest request) {
        String userQuery = request.getMessage();
        if (userQuery == null || userQuery.trim().isEmpty()) {
            return ChatResponse.builder()
                    .reply("Hello! How can I help you find a PG or hostel today? You can ask about areas (like Gachibowli or Madhapur), gender (Men/Women), or amenities like AC and Food.")
                    .recommendedHostels(Collections.emptyList())
                    .build();
        }

        // ==========================================
        // 1. RETRIEVE: Fetch live hostels from Hostel Service (RAG Step 1)
        // ==========================================
        List<HostelSummaryDto> liveHostels = Collections.emptyList();
        try {
            liveHostels = hostelServiceClient.getAllHostels();
            log.info("RAG Engine successfully retrieved {} hostels from hostel-service", liveHostels.size());
        } catch (Exception e) {
            log.warn("Could not reach hostel-service via Feign (it may be offline). Proceeding with fallback catalog.", e);
        }

        // ==========================================
        // 2. AUGMENT: Format factual catalog context (RAG Step 2)
        // ==========================================
        String catalogContext = buildCatalogContext(liveHostels);

        // ==========================================
        // 3. GENERATE: Call Gemini LLM or Local RAG Engine (RAG Step 3)
        // ==========================================
        if (geminiApiKey != null && !geminiApiKey.trim().isEmpty() && !geminiApiKey.equals("YOUR_GEMINI_API_KEY")) {
            try {
                return callGeminiLlm(userQuery, catalogContext, liveHostels);
            } catch (Exception e) {
                log.error("Gemini API call failed, switching to Intelligent Local RAG Matcher", e);
            }
        }

        // Default / Offline Fallback RAG Matcher (Always works without API key)
        return localRagMatcher(userQuery, liveHostels);
    }

    private String buildCatalogContext(List<HostelSummaryDto> hostels) {
        if (hostels == null || hostels.isEmpty()) {
            return "No live hostels currently listed in the database.";
        }

        StringBuilder sb = new StringBuilder();
        for (HostelSummaryDto h : hostels) {
            sb.append(String.format("ID: %d | Name: %s | Gender: %s | Area: %s, %s | Description: %s\n",
                    h.getId(),
                    h.getName(),
                    h.getGenderType() != null ? h.getGenderType() : "ALL",
                    h.getAddress() != null ? h.getAddress().getArea() : "Hyderabad",
                    h.getAddress() != null ? h.getAddress().getCity() : "Hyderabad",
                    h.getDescription() != null ? h.getDescription() : "Comfortable stay"));
        }
        return sb.toString();
    }

    private ChatResponse callGeminiLlm(String userQuery, String catalogContext, List<HostelSummaryDto> hostels) {
        String systemInstruction = "You are PG Finder Assistant, a friendly and accurate AI advisor for finding hostels and PGs in Hyderabad. " +
                "You must base your recommendations strictly on the following verified catalog data. " +
                "Do NOT invent or hallucinate hostels not in this list. Always mention hostel name, area, gender policy, and why it fits.\n\n" +
                "=== VERIFIED HOSTEL CATALOG ===\n" + catalogContext;

        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of("role", "user", "parts", List.of(
                                Map.of("text", systemInstruction + "\n\nUser Question: " + userQuery)
                        ))
                )
        );

        String url = String.format("https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s",
                geminiModel, geminiApiKey);

        String responseJson = webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        String answer = extractGeminiText(responseJson);

        List<ChatResponse.RecommendedHostel> recommended = matchHostelsFromText(answer, hostels);

        return ChatResponse.builder()
                .reply(answer)
                .recommendedHostels(recommended)
                .build();
    }

    private String extractGeminiText(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode textNode = root.path("candidates").get(0)
                    .path("content").path("parts").get(0).path("text");
            if (!textNode.isMissingNode()) {
                return textNode.asText();
            }
        } catch (Exception e) {
            log.error("Failed to parse Gemini JSON response", e);
        }
        return "I found some great options for you based on our live catalog!";
    }

    private ChatResponse localRagMatcher(String query, List<HostelSummaryDto> hostels) {
        String lowerQuery = query.toLowerCase();

        // Filter hostels matching query criteria
        List<HostelSummaryDto> matched = hostels.stream()
                .filter(h -> {
                    String name = h.getName() != null ? h.getName().toLowerCase() : "";
                    String area = (h.getAddress() != null && h.getAddress().getArea() != null) ? h.getAddress().getArea().toLowerCase() : "";
                    String gender = h.getGenderType() != null ? h.getGenderType().toLowerCase() : "";
                    String desc = h.getDescription() != null ? h.getDescription().toLowerCase() : "";

                    boolean areaMatch = lowerQuery.contains("gachibowli") && area.contains("gachibowli")
                            || lowerQuery.contains("madhapur") && area.contains("madhapur")
                            || lowerQuery.contains("hitec") && area.contains("hitec")
                            || lowerQuery.contains("kondapur") && area.contains("kondapur")
                            || lowerQuery.contains("kukatpally") && area.contains("kukatpally");

                    boolean genderMatch = (lowerQuery.contains("women") || lowerQuery.contains("female") || lowerQuery.contains("girl")) && (gender.contains("female") || gender.contains("women"))
                            || (lowerQuery.contains("men") || lowerQuery.contains("male") || lowerQuery.contains("boy")) && (gender.contains("male") || gender.contains("men"));

                    return areaMatch || genderMatch || lowerQuery.contains(name) || (areaMatch && genderMatch);
                })
                .collect(Collectors.toList());

        // If no specific match, default to top 3 available
        if (matched.isEmpty() && !hostels.isEmpty()) {
            matched = hostels.stream().limit(3).collect(Collectors.toList());
        }

        if (matched.isEmpty()) {
            return ChatResponse.builder()
                    .reply("I searched our database for **\"" + query + "\"**, but couldn't find matching hostels right now. Try searching for areas like **Gachibowli**, **Madhapur**, **Hitec City**, or specify **Men's** or **Women's** PGs!")
                    .recommendedHostels(Collections.emptyList())
                    .build();
        }

        StringBuilder reply = new StringBuilder();
        reply.append("Based on our live Hyderabad hostel database, here are the best recommendations for you:\n\n");

        List<ChatResponse.RecommendedHostel> recList = new ArrayList<>();
        int count = 1;
        for (HostelSummaryDto h : matched) {
            String area = h.getAddress() != null ? h.getAddress().getArea() : "Hyderabad";
            reply.append(String.format("%d. **%s** (%s)\n", count++, h.getName(), area));
            reply.append(String.format("   • **Gender Policy:** %s\n", h.getGenderType() != null ? h.getGenderType() : "Co-ed"));
            if (h.getDescription() != null && !h.getDescription().isEmpty()) {
                reply.append(String.format("   • **Features:** %s\n", h.getDescription()));
            }
            reply.append("\n");

            recList.add(ChatResponse.RecommendedHostel.builder()
                    .id(h.getId())
                    .name(h.getName())
                    .area(area)
                    .gender(h.getGenderType() != null ? h.getGenderType() : "ALL")
                    .build());
        }

        reply.append("Click on any recommended property below to view room availability and book your bed directly!");

        return ChatResponse.builder()
                .reply(reply.toString())
                .recommendedHostels(recList)
                .build();
    }

    private List<ChatResponse.RecommendedHostel> matchHostelsFromText(String text, List<HostelSummaryDto> hostels) {
        String lower = text.toLowerCase();
        List<ChatResponse.RecommendedHostel> results = new ArrayList<>();
        for (HostelSummaryDto h : hostels) {
            if (h.getName() != null && lower.contains(h.getName().toLowerCase())) {
                results.add(ChatResponse.RecommendedHostel.builder()
                        .id(h.getId())
                        .name(h.getName())
                        .area(h.getAddress() != null ? h.getAddress().getArea() : "Hyderabad")
                        .gender(h.getGenderType() != null ? h.getGenderType() : "ALL")
                        .build());
            }
        }
        return results;
    }
}
