package com.skanga.model;

// Search API enum
public enum SearchAPI {
    PERPLEXITY("perplexity"),
    TAVILY("tavily"),
    DUCKDUCKGO("duckduckgo"),
    SEARXNG("searxng");
    
    private final String value;
    
    SearchAPI(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static SearchAPI fromString(String value) {
        for (SearchAPI api : SearchAPI.values()) {
            if (api.value.equalsIgnoreCase(value)) {
                return api;
            }
        }
        throw new IllegalArgumentException("Unknown SearchAPI: " + value);
    }
    
    @Override
    public String toString() {
        return value;
    }
}

