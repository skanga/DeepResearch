package com.skanga.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skanga.service.ResearchService;
import com.skanga.config.ResearchConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

@WebMvcTest(ResearchController.class)
class ResearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ResearchService researchService;

    @MockitoBean
    private ResearchConfiguration researchConfiguration;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void conductResearch_success() throws Exception {
        // Given
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("topic", "test topic");

        when(researchService.conductResearch(anyString(), any()))
                .thenReturn(CompletableFuture.completedFuture("research result"));

        // When & Then
        var mvcResult = mockMvc.perform(post("/api/research/conduct")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("completed"))
                .andExpect(jsonPath("$.summary").value("research result"));
    }

    @Test
    void conductResearch_missingTopic() throws Exception {
        // Given
        Map<String, Object> requestBody = new HashMap<>();

        // When & Then
        var mvcResult = mockMvc.perform(post("/api/research/conduct")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("failed"))
                .andExpect(jsonPath("$.error").value("Missing required field: topic"));
    }

    @Test
    void conductResearch_serviceThrowsException() throws Exception {
        // Given
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("topic", "test topic");

        when(researchService.conductResearch(anyString(), any()))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Service failure")));

        // When & Then
        var mvcResult = mockMvc.perform(post("/api/research/conduct")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("failed"))
                .andExpect(jsonPath("$.error").value("java.lang.RuntimeException: Service failure"));
    }

    @Test
    void health_returnsHealthy() throws Exception {
        // Mock the configuration methods used in health endpoint
        when(researchConfiguration.getLlmProvider()).thenReturn("test-provider");
        when(researchConfiguration.getModelName()).thenReturn("test-model");
        when(researchConfiguration.getBaseUrl()).thenReturn("http://test-url");
        when(researchConfiguration.getApiKey()).thenReturn("test-key");

        mockMvc.perform(get("/api/research/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("healthy"));
    }

    @Test
    void debug_returnsDebugInfo() throws Exception {
        // Mock the configuration methods used in debug endpoint
        when(researchConfiguration.getLlmProvider()).thenReturn("test-provider");
        when(researchConfiguration.getModelName()).thenReturn("test-model");
        when(researchConfiguration.getBaseUrl()).thenReturn("http://test-url");
        when(researchConfiguration.getApiKey()).thenReturn("test-key");
        when(researchConfiguration.getMaxWebResearchLoops()).thenReturn(3);
        when(researchConfiguration.getSearchApi()).thenReturn("test-api");
        when(researchConfiguration.getMaxTokensPerSource()).thenReturn(1000);

        mockMvc.perform(get("/api/research/debug"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.config").exists())
                .andExpect(jsonPath("$.env_vars").exists());
    }
}
