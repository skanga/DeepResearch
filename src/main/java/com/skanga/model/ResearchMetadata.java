package com.skanga.model;

import java.time.LocalDateTime;

public class ResearchMetadata {
    private LocalDateTime date;
    private long totalTimeTaken;
    private int loopCount;
    private int searchCallCount;
    private int llmCallCount;
    private String llmProvider;
    private String modelName;

    public ResearchMetadata() {
        this.date = LocalDateTime.now();
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public long getTotalTimeTaken() {
        return totalTimeTaken;
    }

    public void setTotalTimeTaken(long totalTimeTaken) {
        this.totalTimeTaken = totalTimeTaken;
    }

    public int getLoopCount() {
        return loopCount;
    }

    public void setLoopCount(int loopCount) {
        this.loopCount = loopCount;
    }

    public int getSearchCallCount() {
        return searchCallCount;
    }

    public void setSearchCallCount(int searchCallCount) {
        this.searchCallCount = searchCallCount;
    }

    public int getLlmCallCount() {
        return llmCallCount;
    }

    public void setLlmCallCount(int llmCallCount) {
        this.llmCallCount = llmCallCount;
    }

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

    public void incrementSearchCallCount() {
        this.searchCallCount++;
    }

    public void incrementLlmCallCount() {
        this.llmCallCount++;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("- Date: ").append(date).append("\n");
        sb.append("- Total Time Taken: ").append(totalTimeTaken/1000).append(" sec\n");
        sb.append("- Research Loop Count: ").append(loopCount).append("\n");
        sb.append("- Search Call Count: ").append(searchCallCount).append("\n");
        sb.append("- LLM Call Count: ").append(llmCallCount).append("\n");
        sb.append("- LLM Provider: ").append(llmProvider).append("\n");
        sb.append("- Model Name: ").append(modelName);
        return sb.toString();
    }
}
