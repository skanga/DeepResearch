package com.skanga.config;

import com.skanga.model.LLMProvider;
import com.skanga.model.SearchAPI;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

// Configuration class with Spring Boot properties support
@Component
@ConfigurationProperties(prefix = "research")
public class Configuration {
    
    /**
     * Number of research iterations to perform
     */
    @Min(value = 1, message = "Research depth must be at least 1")
    @JsonProperty("max_web_research_loops")
    private int maxWebResearchLoops = 3;
    
    /**
     * Name of the LLM model to use
     */
    @NotBlank(message = "LLM model name cannot be blank")
    @JsonProperty("model_name")
    private String modelName = "llama3.2";
    
    /**
     * Provider for the LLM
     */
    @NotNull(message = "LLM provider cannot be null")
    @JsonProperty("llm_provider")
    private LLMProvider llmProvider = LLMProvider.OLLAMA;
    
    /**
     * Web search API to use
     */
    @NotNull(message = "Search API cannot be null")
    @JsonProperty("search_api")
    private SearchAPI searchApi = SearchAPI.DUCKDUCKGO;
    
    /**
     * Include the full page content in the search results
     */
    @JsonProperty("fetch_full_page")
    private boolean fetchFullPage = true;
    
    /**
     * Base URL for the LLM service (defaults to Ollama)
     */
    @NotBlank(message = "Base URL cannot be blank")
    @JsonProperty("base_url")
    private String baseUrl = "http://localhost:11434/";

    /**
     * Whether to strip <think> tokens from model responses
     */
    @JsonProperty("strip_thinking_tokens")
    private boolean stripThinkingTokens = true;
    
    /**
     * Use tool calling instead of JSON mode for structured output
     */
    @JsonProperty("use_tool_calling")
    private boolean useToolCalling = false;
    
    // Default constructor
    public Configuration() {}
    
    // Constructor with all parameters
    public Configuration(int maxWebResearchLoops, String modelName, LLMProvider llmProvider,
                         SearchAPI searchApi, boolean fetchFullPage, String baseUrl,
                         boolean stripThinkingTokens, boolean useToolCalling) {
        this.maxWebResearchLoops = maxWebResearchLoops;
        this.modelName = modelName;
        this.llmProvider = llmProvider;
        this.searchApi = searchApi;
        this.fetchFullPage = fetchFullPage;
        this.baseUrl = baseUrl;
        this.stripThinkingTokens = stripThinkingTokens;
        this.useToolCalling = useToolCalling;
    }
    
    /**
     * Create a Configuration instance from environment variables and optional config map.
     * This is equivalent to the Python from_runnable_config method.
     * 
     * @param configurable Optional configuration map (can be null)
     * @return Configuration instance with values from environment or config map
     */
    public static Configuration fromRunnableConfig(Map<String, Object> configurable) {
        Configuration config = new Configuration();
        
        // Helper method to get value from environment or config map
        Function<String, String> getValue = (fieldName) -> {
            // First check environment variables (uppercase)
            String envValue = System.getenv(fieldName.toUpperCase());
            if (envValue != null && !envValue.isEmpty()) {
                return envValue;
            }
            
            // Then check configurable map
            if (configurable != null) {
                Object value = configurable.get(fieldName);
                return value != null ? value.toString() : null;
            }
            
            return null;
        };
        
        // Set values from environment or config map, keeping defaults if not found
        Optional.ofNullable(getValue.apply("max_web_research_loops"))
                .ifPresent(value -> config.setMaxWebResearchLoops(Integer.parseInt(value)));
                
        Optional.ofNullable(getValue.apply("model_name"))
                .ifPresent(config::setModelName);
                
        Optional.ofNullable(getValue.apply("llm_provider"))
                .ifPresent(value -> config.setLlmProvider(LLMProvider.fromString(value)));
                
        Optional.ofNullable(getValue.apply("search_api"))
                .ifPresent(value -> config.setSearchApi(SearchAPI.fromString(value)));
                
        Optional.ofNullable(getValue.apply("fetch_full_page"))
                .ifPresent(value -> config.setFetchFullPage(Boolean.parseBoolean(value)));
                
        Optional.ofNullable(getValue.apply("base_url"))
                .ifPresent(config::setBaseUrl);

        Optional.ofNullable(getValue.apply("strip_thinking_tokens"))
                .ifPresent(value -> config.setStripThinkingTokens(Boolean.parseBoolean(value)));
                
        Optional.ofNullable(getValue.apply("use_tool_calling"))
                .ifPresent(value -> config.setUseToolCalling(Boolean.parseBoolean(value)));
        
        return config;
    }
    
    /**
     * Overloaded method for when no configurable map is provided
     */
    public static Configuration fromRunnableConfig() {
        return fromRunnableConfig(null);
    }
    
    // Getters and Setters
    public int getMaxWebResearchLoops() {
        return maxWebResearchLoops;
    }
    
    public void setMaxWebResearchLoops(int maxWebResearchLoops) {
        this.maxWebResearchLoops = maxWebResearchLoops;
    }
    
    public String getModelName() {
        return modelName;
    }
    
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
    
    public LLMProvider getLlmProvider() {
        return llmProvider;
    }
    
    public void setLlmProvider(LLMProvider llmProvider) {
        this.llmProvider = llmProvider;
    }
    
    public SearchAPI getSearchApi() {
        return searchApi;
    }
    
    public void setSearchApi(SearchAPI searchApi) {
        this.searchApi = searchApi;
    }
    
    public boolean isFetchFullPage() {
        return fetchFullPage;
    }
    
    public void setFetchFullPage(boolean fetchFullPage) {
        this.fetchFullPage = fetchFullPage;
    }
    
    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public boolean isStripThinkingTokens() {
        return stripThinkingTokens;
    }
    
    public void setStripThinkingTokens(boolean stripThinkingTokens) {
        this.stripThinkingTokens = stripThinkingTokens;
    }
    
    public boolean isUseToolCalling() {
        return useToolCalling;
    }
    
    public void setUseToolCalling(boolean useToolCalling) {
        this.useToolCalling = useToolCalling;
    }
    
    @Override
    public String toString() {
        return "Configuration{" +
                "maxWebResearchLoops=" + maxWebResearchLoops +
                ", modelName='" + modelName + '\'' +
                ", llmProvider=" + llmProvider +
                ", searchApi=" + searchApi +
                ", fetchFullPage=" + fetchFullPage +
                ", baseUrl='" + baseUrl + '\'' +
                ", stripThinkingTokens=" + stripThinkingTokens +
                ", useToolCalling=" + useToolCalling +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Configuration)) return false;
        
        Configuration otherConf = (Configuration) o;
        
        if (maxWebResearchLoops != otherConf.maxWebResearchLoops) return false;
        if (fetchFullPage != otherConf.fetchFullPage) return false;
        if (stripThinkingTokens != otherConf.stripThinkingTokens) return false;
        if (useToolCalling != otherConf.useToolCalling) return false;
        if (!modelName.equals(otherConf.modelName)) return false;
        if (llmProvider != otherConf.llmProvider) return false;
        if (searchApi != otherConf.searchApi) return false;
        if (!baseUrl.equals(otherConf.baseUrl)) return false;
        return true;
    }
    
    @Override
    public int hashCode() {
        int result = maxWebResearchLoops;
        result = 31 * result + modelName.hashCode();
        result = 31 * result + llmProvider.hashCode();
        result = 31 * result + searchApi.hashCode();
        result = 31 * result + (fetchFullPage ? 1 : 0);
        result = 31 * result + baseUrl.hashCode();
        result = 31 * result + (stripThinkingTokens ? 1 : 0);
        result = 31 * result + (useToolCalling ? 1 : 0);
        return result;
    }
}
