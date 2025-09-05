package com.skanga.config;

import org.springframework.stereotype.Component;
import org.springframework.boot.context.properties.ConfigurationProperties;

// Configuration class
@Component
@ConfigurationProperties(prefix = "research")
public class ResearchConfiguration {
    private String llmProvider = "inception";
    private String modelName = "mercury-coder";
    private String baseUrl = "https://api.inceptionlabs.ai/v1";
    private boolean useToolCalling = false;
    private int maxWebResearchLoops = 3;
    private String searchApi = "duckduckgo";
    private boolean fetchFullPage = false;
    private boolean stripThinkingTokens = true;
    private int maxTokensPerSource = 1000;

    private String apiKey = "";
    private String project = "";
    private String location = "";

    // Search API keys
    private String tavilyApiKey = "";
    private String perplexityApiKey = "";
    private String searxngUrl = "http://localhost:8888";

    // Getters and setters
    public String getLlmProvider() {
        return llmProvider;
    }

    public void setLlmProvider(String llmProvider) {
        this.llmProvider = llmProvider;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isUseToolCalling() {
        return useToolCalling;
    }

    public void setUseToolCalling(boolean useToolCalling) {
        this.useToolCalling = useToolCalling;
    }

    public int getMaxWebResearchLoops() {
        return maxWebResearchLoops;
    }

    public void setMaxWebResearchLoops(int maxWebResearchLoops) {
        this.maxWebResearchLoops = maxWebResearchLoops;
    }

    public String getSearchApi() {
        return searchApi;
    }

    public void setSearchApi(String searchApi) {
        this.searchApi = searchApi;
    }

    public boolean isFetchFullPage() {
        return fetchFullPage;
    }

    public void setFetchFullPage(boolean fetchFullPage) {
        this.fetchFullPage = fetchFullPage;
    }

    public boolean isStripThinkingTokens() {
        return stripThinkingTokens;
    }

    public void setStripThinkingTokens(boolean stripThinkingTokens) {
        this.stripThinkingTokens = stripThinkingTokens;
    }

    public int getMaxTokensPerSource() {
        return maxTokensPerSource;
    }

    public void setMaxTokensPerSource(int maxTokensPerSource) {
        this.maxTokensPerSource = maxTokensPerSource;
    }

    public String getTavilyApiKey() {
        return tavilyApiKey;
    }

    public void setTavilyApiKey(String tavilyApiKey) {
        this.tavilyApiKey = tavilyApiKey;
    }

    public String getPerplexityApiKey() {
        return perplexityApiKey;
    }

    public void setPerplexityApiKey(String perplexityApiKey) {
        this.perplexityApiKey = perplexityApiKey;
    }

    public String getSearxngUrl() {
        return searxngUrl;
    }

    public void setSearxngUrl(String searxngUrl) {
        this.searxngUrl = searxngUrl;
    }
}


