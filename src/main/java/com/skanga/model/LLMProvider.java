package com.skanga.model;

// LLM Provider enum
public enum LLMProvider {
    OLLAMA("ollama"),
    OPENAI("openai"),
    ANTHROPIC("anthropic"),
    INCEPTION("inception"),
    LMSTUDIO("lmstudio"),
    GEMINI("gemini"),
    GROQ("groq"),
    HUGGING_FACE("huggingface"),
    VERTEX_AI("vertex-ai"),
    OPENROUTER("openrouter");

    private final String value;
    
    LLMProvider(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static LLMProvider fromString(String value) {
        for (LLMProvider provider : LLMProvider.values()) {
            if (provider.value.equalsIgnoreCase(value)) {
                return provider;
            }
        }
        throw new IllegalArgumentException("Unknown LLMProvider: " + value);
    }
    
    @Override
    public String toString() {
        return value;
    }
}
