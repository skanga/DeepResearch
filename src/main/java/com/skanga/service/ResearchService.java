package com.skanga.service;

import com.skanga.config.ResearchConfiguration;
import com.skanga.model.*;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

// Main research service using LangChain4J
@Service
public class ResearchService {
    private static final Logger logger = LoggerFactory.getLogger(ResearchService.class);
    private final QueryGeneratorService queryGenerator;
    private final SummarizerService summarizer;
    private final ReflectionService reflectionService;
    private final SearchService searchService;
    private final ResearchConfiguration config;

    public ResearchService(SearchService searchService, ResearchConfiguration config) {
        this.searchService = searchService;
        this.config = config;

        logger.info("=== ResearchService: Initializing LangChain4J services ===");
        logger.info("Configuration - Provider: {}, Model: {}, Base URL: {}",
                config.getLlmProvider(), config.getModelName(), config.getBaseUrl());

        // Initialize LangChain4J services
        ChatModel chatModel = createChatModel();

        this.queryGenerator = AiServices.builder(QueryGeneratorService.class)
                .chatModel(chatModel)
                .build();

        this.summarizer = AiServices.builder(SummarizerService.class)
                .chatModel(chatModel)
                .build();

        this.reflectionService = AiServices.builder(ReflectionService.class)
                .chatModel(chatModel)
                .build();

        logger.info("=== ResearchService: LangChain4J services initialized successfully ===");
    }

    private ChatModel createChatModel() {
        logger.debug("=== ResearchService: Creating ChatModel ===");
        LLMProvider provider = LLMProvider.fromString(config.getLlmProvider());

        // Validate configuration
        if (config.getBaseUrl() == null || config.getBaseUrl().trim().isEmpty()) {
            logger.error("Base URL cannot be null or empty");
            throw new IllegalArgumentException("Base URL cannot be null or empty");
        }

        if (config.getModelName() == null || config.getModelName().trim().isEmpty()) {
            logger.error("Model name cannot be null or empty");
            throw new IllegalArgumentException("Model name cannot be null or empty");
        }

        logger.info("Creating ChatModel with provider: {}, model: {}, baseUrl: {}",
                provider, config.getModelName(), config.getBaseUrl());

        return switch (provider) {
            case OPENAI, GROQ, INCEPTION, OLLAMA, OPENROUTER, LMSTUDIO -> OpenAiChatModel.builder()
                    .baseUrl(config.getBaseUrl())
                    .apiKey(config.getApiKey())
                    .modelName(config.getModelName())
                    .temperature(0.0)
                    .build();
            case ANTHROPIC -> AnthropicChatModel.builder()
                    .apiKey(config.getApiKey())
                    .modelName(config.getModelName())
                    .build();
            case GEMINI -> GoogleAiGeminiChatModel.builder()
                    .apiKey(config.getApiKey())
                    .modelName(config.getModelName())
                    .build();
            default -> {
                logger.error("Unsupported LLM provider: {}", config.getLlmProvider());
                throw new IllegalArgumentException("Unsupported LLM provider: " + config.getLlmProvider());
            }
        };
    }

    public CompletableFuture<String> conductResearch(String researchTopic) {
        return conductResearch(researchTopic, null);
    }

    public CompletableFuture<String> conductResearch(String researchTopic, Integer researchStepsOverride) {
        return CompletableFuture.supplyAsync(() -> {
            long startTime = System.currentTimeMillis();
            logger.info("=== ResearchService: Starting research for topic: {} ===", researchTopic);

            SummaryState state = new SummaryState(researchTopic);
            state.getMetadata().setLlmProvider(config.getLlmProvider());
            state.getMetadata().setModelName(config.getModelName());

            try {
                // Determine max research loops based on override or default
                int maxResearchLoops = config.getMaxWebResearchLoops();
                if (researchStepsOverride != null) {
                    maxResearchLoops = researchStepsOverride;
                    logger.info("=== OVERRIDE DETECTED ===");
                    logger.info("Using override research steps: {} (was default: {})", 
                               maxResearchLoops, config.getMaxWebResearchLoops());
                } else {
                    logger.info("Using default research steps: {}", maxResearchLoops);
                }

                logger.info("=== ResearchService: Starting research loop ===");
                logger.info("Max research loops: {}", maxResearchLoops);
                logger.info("Initial research loop count: {}", state.getResearchLoopCount());

                // Generate initial query
                logger.info("=== ResearchService: Generating initial query ===");
                generateQuery(state);
                logger.info("Initial query generated: {}", state.getSearchQuery());

                // Research loop
                int loopNumber = 1;
                while (state.getResearchLoopCount() < maxResearchLoops) {
                    logger.info("=== ResearchService: Research loop {}/{} ===", 
                               loopNumber, maxResearchLoops);
                    logger.info("Current research loop count: {}", state.getResearchLoopCount());

                    // Perform web research
                    logger.info("=== ResearchService: Performing web research ===");
                    webResearch(state);
                    logger.info("After web research, loop count: {}", state.getResearchLoopCount());

                    // Summarize sources
                    logger.info("=== ResearchService: Summarizing sources ===");
                    summarizeSources(state);

                    // Check if we need more research
                    if (state.getResearchLoopCount() < maxResearchLoops) {
                        logger.info("=== ResearchService: Reflecting on summary ===");
                        reflectOnSummary(state);
                        logger.info("Follow-up query generated: {}", state.getSearchQuery());
                    }
                    loopNumber++;
                }

                // Finalize summary
                logger.info("=== ResearchService: Finalizing summary ===");
                logger.info("Final metadata loop count: {}", state.getResearchLoopCount());
                long endTime = System.currentTimeMillis();
                state.getMetadata().setTotalTimeTaken(endTime - startTime);
                state.getMetadata().setLoopCount(state.getResearchLoopCount());
                String finalSummary = finalizeSummary(state);
                logger.info("=== ResearchService: Research completed successfully ===");
                logger.info("Final summary length: {} characters", finalSummary.length());

                return finalSummary;

            } catch (Exception e) {
                logger.error("=== ResearchService: Research failed ===", e);
                throw new RuntimeException("Research failed: " + e.getMessage(), e);
            }
        });
    }

    private void generateQuery(SummaryState state) {
        logger.debug("=== ResearchService: Generating query for topic: {} ===", state.getResearchTopic());
        String currentDate = LocalDate.now().toString();
        SearchQuery searchQuery = queryGenerator.generateQuery(state.getResearchTopic(), currentDate);
        state.getMetadata().incrementLlmCallCount();
        state.setSearchQuery(searchQuery.getQuery());
        logger.debug("Generated query: {}", searchQuery.getQuery());
        logger.debug("Query rationale: {}", searchQuery.getRationale());
    }

    private void webResearch(SummaryState state) {
        logger.debug("=== ResearchService: Starting web research for query: {} ===", state.getSearchQuery());

        List<SearchResult> searchResults = searchService.search(state.getSearchQuery(), config);
        state.getMetadata().incrementSearchCallCount();
        logger.debug("Found {} search results", searchResults.size());

        String formattedResults = searchService.formatResults(searchResults, config.getMaxTokensPerSource());
        logger.debug("Formatted results length: {} characters", formattedResults.length());

        state.addWebResearchResult(formattedResults);
        state.addSourcesGathered(searchService.formatSources(searchResults));
        state.incrementResearchLoopCount();

        logger.debug("Web research completed for loop: {}", state.getResearchLoopCount());
    }

    private void summarizeSources(SummaryState state) {
        logger.debug("=== ResearchService: Summarizing sources ===");

        if (state.getWebResearchResults().isEmpty()) {
            logger.warn("No web research results to summarize");
            return;
        }

        String latestResearch = state.getWebResearchResults().getLast();

        // Ensure existing summary is non-null
        String existingSummary = state.getRunningSummary();
        if (existingSummary == null || existingSummary.trim().isEmpty()) {
            logger.debug("No existing summary, starting fresh");
            existingSummary = "";
        }

        logger.debug("Summarizing with existing summary length: {} chars", existingSummary.length());
        logger.debug("New research length: {} chars", latestResearch.length());

        String updatedSummary = summarizer.summarize(
                existingSummary,
                latestResearch,
                state.getResearchTopic()
        );
        state.getMetadata().incrementLlmCallCount();

        logger.debug("Generated summary length: {} chars", updatedSummary.length());

        if (config.isStripThinkingTokens()) {
            updatedSummary = stripThinkingTokens(updatedSummary);
            logger.debug("Stripped thinking tokens, final length: {} chars", updatedSummary.length());
        }

        state.setRunningSummary(updatedSummary);
    }

    private void reflectOnSummary(SummaryState state) {
        logger.debug("=== ResearchService: Reflecting on summary ===");

        String currentSummary = state.getRunningSummary();
        if (currentSummary == null || currentSummary.trim().isEmpty()) {
            logger.debug("No current summary to reflect on");
            return;
        }

        logger.debug("Current summary length: {} chars", currentSummary.length());

        FollowUpQuery followUp = reflectionService.generateFollowUpQuery(
                currentSummary,
                state.getResearchTopic()
        );
        state.getMetadata().incrementLlmCallCount();

        logger.debug("Generated follow-up query: {}", followUp.getFollowUpQuery());
        logger.debug("Knowledge gap identified: {}", followUp.getKnowledgeGap());

        state.setSearchQuery(followUp.getFollowUpQuery());
    }

    private String finalizeSummary(SummaryState state) {
        logger.debug("=== ResearchService: Finalizing summary ===");

        // Deduplicate sources
        Set<String> uniqueSources = new LinkedHashSet<>(state.getSourcesGathered());
        String allSources = String.join("\n", uniqueSources);

        String finalSummary = String.format("## Summary\n%s\n\n### Sources:\n%s\n\n### Metadata:\n%s\n",
                state.getRunningSummary(), allSources, state.getMetadata().toString());

        logger.debug("Final summary prepared with {} unique sources", uniqueSources.size());
        return finalSummary;
    }

    private String stripThinkingTokens(String content) {
        logger.debug("Stripping thinking tokens from content length: {} chars", content.length());
        String result = content.replaceAll("(?s)<thinking>.*?</thinking>", "").trim();
        logger.debug("After stripping thinking tokens: {} chars", result.length());
        return result;
    }
}
