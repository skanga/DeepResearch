package com.skanga.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResearchConfigurationTest {

    private ResearchConfiguration config;

    @BeforeEach
    void setUp() {
        config = new ResearchConfiguration();
    }

    @Test
    void testDefaultValues() {
        assertEquals("inception", config.getLlmProvider());
        assertEquals("mercury-coder", config.getModelName());
        assertEquals("https://api.inceptionlabs.ai/v1", config.getBaseUrl());
        assertEquals(3, config.getMaxWebResearchLoops());
        assertEquals("duckduckgo", config.getSearchApi());
        assertTrue(config.isStripThinkingTokens());
        assertEquals(1000, config.getMaxTokensPerSource());
    }

    @Test
    void testSetters() {
        config.setLlmProvider("test-provider");
        config.setModelName("test-model");
        config.setBaseUrl("http://test-url");
        config.setMaxWebResearchLoops(5);
        config.setSearchApi("test-api");
        config.setStripThinkingTokens(false);
        config.setMaxTokensPerSource(500);

        assertEquals("test-provider", config.getLlmProvider());
        assertEquals("test-model", config.getModelName());
        assertEquals("http://test-url", config.getBaseUrl());
        assertEquals(5, config.getMaxWebResearchLoops());
        assertEquals("test-api", config.getSearchApi());
        assertEquals(false, config.isStripThinkingTokens());
        assertEquals(500, config.getMaxTokensPerSource());
    }
}
