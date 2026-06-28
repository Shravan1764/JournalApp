package com.example.boot2.service;

import com.example.boot2.enums.Sentiment;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Service that performs ML-based sentiment analysis by calling the
 * HuggingFace Inference API. This offloads the ML model to HuggingFace's
 * servers, keeping the application lightweight for Heroku deployment.
 *
 * Uses the "nlptown/bert-base-multilingual-uncased-sentiment" model which
 * is a BERT transformer fine-tuned for sentiment analysis. It classifies
 * text into 5 star ratings (1-5 stars) which we map to our Sentiment enum:
 *   1 star  -> VERY_NEGATIVE
 *   2 stars -> NEGATIVE
 *   3 stars -> NEUTRAL
 *   4 stars -> POSITIVE
 *   5 stars -> VERY_POSITIVE
 */
@Service
public class SentimentAnalyzerService {

    private static final Logger logger = LoggerFactory.getLogger(SentimentAnalyzerService.class);

    private static final String HUGGINGFACE_API_URL =
            "https://api-inference.huggingface.co/models/nlptown/bert-base-multilingual-uncased-sentiment";

    @Value("${huggingface.api.token}")
    private String apiToken;

    @Autowired
    private RestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Analyzes the sentiment of the given text by calling the HuggingFace
     * Inference API with a fine-tuned BERT model.
     *
     * @param text the journal text to analyze (title + description)
     * @return the overall Sentiment classification, or NEUTRAL if text is empty/null or API fails
     */
    public Sentiment analyzeSentiment(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Sentiment.NEUTRAL;
        }

        try {
            // Truncate text to 512 tokens (BERT's max input length)
            // Approximation: ~4 chars per token
            String truncatedText = text.length() > 2000 ? text.substring(0, 2000) : text;

            // Build the request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiToken);

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("inputs", truncatedText);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

            // Call HuggingFace Inference API
            String response = restTemplate.postForObject(HUGGINGFACE_API_URL, request, String.class);

            return parseResponse(response);

        } catch (Exception e) {
            logger.error("Error calling HuggingFace sentiment API: {}", e.getMessage());
            // Fallback to NEUTRAL if API is unavailable (e.g., rate limited, cold start)
            return Sentiment.NEUTRAL;
        }
    }

    /**
     * Parses the HuggingFace API response and extracts the top sentiment.
     *
     * Response format: [[{"label":"5 stars","score":0.85},{"label":"4 stars","score":0.10},...]]
     * We pick the label with the highest score.
     */
    private Sentiment parseResponse(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);

            // Response is a nested array: [[{label, score}, ...]]
            JsonNode predictions = root.get(0);
            if (predictions == null || predictions.isEmpty()) {
                return Sentiment.NEUTRAL;
            }

            String bestLabel = null;
            double bestScore = -1;

            for (JsonNode prediction : predictions) {
                double score = prediction.get("score").asDouble();
                if (score > bestScore) {
                    bestScore = score;
                    bestLabel = prediction.get("label").asText();
                }
            }

            logger.debug("HuggingFace sentiment result: label={}, score={}", bestLabel, bestScore);
            return mapLabelToSentiment(bestLabel);

        } catch (Exception e) {
            logger.error("Error parsing HuggingFace response: {}", e.getMessage());
            return Sentiment.NEUTRAL;
        }
    }

    /**
     * Maps the HuggingFace model's star rating labels to our Sentiment enum.
     * Model: nlptown/bert-base-multilingual-uncased-sentiment
     * Labels: "1 star", "2 stars", "3 stars", "4 stars", "5 stars"
     */
    private Sentiment mapLabelToSentiment(String label) {
        if (label == null) return Sentiment.NEUTRAL;

        return switch (label) {
            case "1 star" -> Sentiment.VERY_NEGATIVE;
            case "2 stars" -> Sentiment.NEGATIVE;
            case "3 stars" -> Sentiment.NEUTRAL;
            case "4 stars" -> Sentiment.POSITIVE;
            case "5 stars" -> Sentiment.VERY_POSITIVE;
            default -> Sentiment.NEUTRAL;
        };
    }
}
