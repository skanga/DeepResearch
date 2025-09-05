package com.skanga.controller;

import com.skanga.service.ResearchService;
import com.skanga.config.ResearchConfiguration;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

// REST Controller with debug logging
@RestController
@RequestMapping("/api/research")
public class ResearchController {
    private static final Logger logger = LoggerFactory.getLogger(ResearchController.class);
    private final ResearchService researchService;
    private final ResearchConfiguration config;

    public ResearchController(ResearchService researchService, ResearchConfiguration config) {
        this.researchService = researchService;
        this.config = config;
    }

    @PostMapping("/conduct")
    public CompletableFuture<Map<String, String>> conductResearch(@RequestBody Map<String, Object> request) {
        logger.info("=== ResearchController: Starting research request ===");
        logger.info("Request body: {}", request);
        logger.info("Request keys: {}", request.keySet());

        String topic = (String) request.get("topic");
        Object researchStepsObj = request.get("researchSteps");
        Integer researchSteps = null;
        
        logger.info("Raw researchStepsObj: {} (type: {})", researchStepsObj, 
                   researchStepsObj != null ? researchStepsObj.getClass() : "null");
        
        if (researchStepsObj != null) {
            if (researchStepsObj instanceof Integer) {
                researchSteps = (Integer) researchStepsObj;
                logger.info("Parsed Integer researchSteps: {}", researchSteps);
            } else if (researchStepsObj instanceof String) {
                try {
                    researchSteps = Integer.parseInt((String) researchStepsObj);
                    logger.info("Parsed String researchSteps: {}", researchSteps);
                } catch (NumberFormatException e) {
                    logger.warn("Invalid research steps format: {}", researchStepsObj);
                }
            } else if (researchStepsObj instanceof Double) {
                researchSteps = ((Double) researchStepsObj).intValue();
                logger.info("Parsed Double researchSteps: {}", researchSteps);
            }
        }
        
        logger.info("Research topic: {}", topic);
        logger.info("Final research steps override: {}", researchSteps);

        if (topic == null || topic.trim().isEmpty()) {
            logger.error("Invalid topic provided: {}", topic);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Missing required field: topic");
            errorResponse.put("status", "failed");
            return CompletableFuture.completedFuture(errorResponse);
        }

        logger.info("Calling ResearchService.conductResearch with topic: {} and steps: {}", 
                  topic, researchSteps);

        return researchService.conductResearch(topic.trim(), researchSteps)
                .thenApply(result -> {
                    logger.info("=== ResearchController: Research completed successfully ===");
                    logger.debug("Result length: {} characters", result.length());

                    Map<String, String> response = new HashMap<>();
                    response.put("summary", result);
                    response.put("status", "completed");
                    return response;
                })
                .exceptionally(throwable -> {
                    logger.error("=== ResearchController: Research failed ===", throwable);
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", throwable.getMessage());
                    errorResponse.put("status", "failed");
                    return errorResponse;
                });
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        logger.debug("Health check requested");
        Map<String, String> response = new HashMap<>();
        response.put("status", "healthy");
        response.put("timestamp", LocalDate.now().toString());
        response.put("provider", config.getLlmProvider());
        response.put("model", config.getModelName());
        response.put("base_url", config.getBaseUrl());
        response.put("api_key_status", config.getApiKey() != null && !config.getApiKey().isEmpty() ? "configured" : "missing");
        return response;
    }

    @GetMapping("/debug")
    public Map<String, Object> debug() {
        logger.info("Debug endpoint accessed");
        Map<String, Object> debug = new HashMap<>();
        debug.put("timestamp", LocalDate.now().toString());
        
        // Show effective configuration values using a mutable map to allow nulls
        Map<String, Object> configMap = new HashMap<>();
        configMap.put("llmProvider", config.getLlmProvider());
        configMap.put("modelName", config.getModelName());
        configMap.put("baseUrl", config.getBaseUrl());
        configMap.put("apiKeyStatus", (config.getApiKey() != null && !config.getApiKey().isEmpty()) ? "[SET]" : "[NOT SET]");
        configMap.put("maxWebResearchLoops", config.getMaxWebResearchLoops());
        configMap.put("searchApi", config.getSearchApi());
        configMap.put("maxTokensPerSource", config.getMaxTokensPerSource());
        debug.put("config", configMap);
        
        // Show raw environment variables for debugging
        // Show raw environment variables for debugging using a mutable map to avoid nulls
        Map<String, Object> envMap = new HashMap<>();
        envMap.put("RESEARCH_LLM_PROVIDER", System.getenv("RESEARCH_LLM_PROVIDER"));
        envMap.put("RESEARCH_MODEL_NAME", System.getenv("RESEARCH_MODEL_NAME"));
        envMap.put("RESEARCH_BASE_URL", System.getenv("RESEARCH_BASE_URL"));
        envMap.put("RESEARCH_API_KEY", System.getenv("RESEARCH_API_KEY") != null ? "[SET]" : "[NOT SET]");
        debug.put("env_vars", envMap);
        return debug;
    }
}
