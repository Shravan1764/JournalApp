package com.example.boot2.service;

import com.example.boot2.enums.Sentiment;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Properties;

/**
 * Service that performs real ML-based sentiment analysis using Stanford CoreNLP.
 *
 * The underlying model is a Recursive Neural Tensor Network (RNTN) trained on
 * the Stanford Sentiment Treebank. It classifies text into 5 sentiment classes:
 * Very Negative, Negative, Neutral, Positive, Very Positive.
 *
 * The NLP pipeline is loaded eagerly at application startup to avoid latency
 * on the first journal save request.
 */
@Service
public class SentimentAnalyzerService {

    private static final Logger logger = LoggerFactory.getLogger(SentimentAnalyzerService.class);

    private StanfordCoreNLP pipeline;

    @PostConstruct
    public void init() {
        logger.info("Initializing Stanford CoreNLP sentiment analysis pipeline...");
        Properties props = new Properties();
        // tokenize: split text into tokens
        // ssplit: split tokens into sentences
        // parse: build parse tree (required by sentiment)
        // sentiment: run the RNTN sentiment model on each sentence
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        pipeline = new StanfordCoreNLP(props);
        logger.info("Stanford CoreNLP sentiment analysis pipeline initialized successfully.");
    }

    /**
     * Analyzes the sentiment of the given text using Stanford CoreNLP.
     *
     * The method processes each sentence individually, maps the CoreNLP sentiment
     * label to a numeric score (0-4), computes a length-weighted average across
     * all sentences, and maps the result back to our Sentiment enum.
     *
     * @param text the journal text to analyze (title + description)
     * @return the overall Sentiment classification, or NEUTRAL if text is empty/null
     */
    public Sentiment analyzeSentiment(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Sentiment.NEUTRAL;
        }

        Annotation annotation = new Annotation(text);
        pipeline.annotate(annotation);

        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);

        if (sentences == null || sentences.isEmpty()) {
            return Sentiment.NEUTRAL;
        }

        double totalScore = 0;
        int totalLength = 0;

        for (CoreMap sentence : sentences) {
            String sentimentLabel = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
            int score = mapLabelToScore(sentimentLabel);
            // Weight each sentence by its token count so longer sentences
            // contribute more to the overall sentiment
            int sentenceLength = sentence.toString().length();
            totalScore += score * sentenceLength;
            totalLength += sentenceLength;
        }

        if (totalLength == 0) {
            return Sentiment.NEUTRAL;
        }

        double averageScore = totalScore / totalLength;
        Sentiment result = mapScoreToSentiment(averageScore);

        logger.debug("Sentiment analysis result: score={}, sentiment={}", averageScore, result);
        return result;
    }

    /**
     * Maps CoreNLP's string sentiment label to a numeric score (0-4).
     */
    private int mapLabelToScore(String sentimentLabel) {
        if (sentimentLabel == null) return 2; // NEUTRAL default

        return switch (sentimentLabel) {
            case "Very negative" -> 0;
            case "Negative" -> 1;
            case "Neutral" -> 2;
            case "Positive" -> 3;
            case "Very positive" -> 4;
            default -> 2;
        };
    }

    /**
     * Maps a numeric average score back to our Sentiment enum.
     * Score ranges:
     *   [0.0, 0.8)  -> VERY_NEGATIVE
     *   [0.8, 1.6)  -> NEGATIVE
     *   [1.6, 2.4)  -> NEUTRAL
     *   [2.4, 3.2)  -> POSITIVE
     *   [3.2, 4.0]  -> VERY_POSITIVE
     */
    private Sentiment mapScoreToSentiment(double score) {
        if (score < 0.8) return Sentiment.VERY_NEGATIVE;
        if (score < 1.6) return Sentiment.NEGATIVE;
        if (score < 2.4) return Sentiment.NEUTRAL;
        if (score < 3.2) return Sentiment.POSITIVE;
        return Sentiment.VERY_POSITIVE;
    }
}
