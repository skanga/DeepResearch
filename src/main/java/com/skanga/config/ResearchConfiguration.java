package com.skanga.config;

import org.springframework.stereotype.Component;
import org.springframework.boot.context.properties.ConfigurationProperties;

// Configuration class
@Component
@ConfigurationProperties(prefix = "research")
public class ResearchConfiguration {
    private String llmProvider = "ollama";
    private String modelName = "llama3.1";
    private String baseUrl = "http://localhost:11434"; // renamed from ollamaBaseUrl, defaults to Ollama
    private boolean useToolCalling = false;
    private int maxWebResearchLoops = 3;
    private String searchApi = "duckduckgo";
    private boolean fetchFullPage = false;
    private boolean stripThinkingTokens = true;
    private int maxTokensPerSource = 1000;

    private String apiKey = "";
    private String project = "";
    private String location = "";

    // Getters and setters
    public String getLlmProvider() {
        String envProvider = System.getenv("RESEARCH_LLM_PROVIDER");
        return envProvider != null ? envProvider : llmProvider;
    }

    public void setLlmProvider(String llmProvider) {
        this.llmProvider = llmProvider;
    }

    public String getModelName() {
        String envModel = System.getenv("RESEARCH_MODEL_NAME");
        return envModel != null ? envModel : modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getBaseUrl() {
        String envUrl = System.getenv("RESEARCH_BASE_URL");
        return envUrl != null ? envUrl : baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getApiKey() {
        String envKey = System.getenv("RESEARCH_API_KEY");
        if (envKey != null && !envKey.isEmpty()) {
            return envKey;
        }
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getProject() {
        String envProject = System.getenv("RESEARCH_PROJECT");
        return envProject != null ? envProject : project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getLocation() {
        String envLocation = System.getenv("RESEARCH_LOCATION");
        return envLocation != null ? envLocation : location;
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
}


