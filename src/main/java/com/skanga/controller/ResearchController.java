package com.skanga.controller;

import com.skanga.service.ResearchService;
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

    public ResearchController(ResearchService researchService) {
        this.researchService = researchService;
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
        response.put("provider", System.getenv("RESEARCH_LLM_PROVIDER") != null ? System.getenv("RESEARCH_LLM_PROVIDER") : "ollama");
        response.put("model", System.getenv("RESEARCH_MODEL_NAME") != null ? System.getenv("RESEARCH_MODEL_NAME") : "llama2");
        response.put("base_url", System.getenv("RESEARCH_BASE_URL") != null ? System.getenv("RESEARCH_BASE_URL") : "http://localhost:11434");
        response.put("api_key_status", System.getenv("RESEARCH_API_KEY") != null && !System.getenv("RESEARCH_API_KEY").isEmpty() ? "configured" : "missing");
        return response;
    }

    @GetMapping("/debug")
    public Map<String, Object> debug() {
        logger.info("Debug endpoint accessed");
        Map<String, Object> debug = new HashMap<>();
        debug.put("timestamp", LocalDate.now().toString());
        debug.put("env_vars", Map.of(
                "RESEARCH_LLM_PROVIDER", System.getenv("RESEARCH_LLM_PROVIDER"),
                "RESEARCH_MODEL_NAME", System.getenv("RESEARCH_MODEL_NAME"),
                "RESEARCH_BASE_URL", System.getenv("RESEARCH_BASE_URL"),
                "RESEARCH_API_KEY", System.getenv("RESEARCH_API_KEY") != null ? "[SET]" : "[NOT SET]"
        ));
        return debug;
    }
}

