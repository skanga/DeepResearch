package com.skanga.model;

import java.util.ArrayList;
import java.util.List;

// State management
public class SummaryState {
    private String researchTopic;
    private String searchQuery;
    private String runningSummary;
    private List<String> sourcesGathered = new ArrayList<>();
    private List<String> webResearchResults = new ArrayList<>();
    private int researchLoopCount = 0;
    private ResearchMetadata metadata;

    public SummaryState(String researchTopic) {
        this.researchTopic = researchTopic;
        this.metadata = new ResearchMetadata();
    }

    // Getters and setters
    public String getResearchTopic() {
        return researchTopic;
    }

    public void setResearchTopic(String researchTopic) {
        this.researchTopic = researchTopic;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public String getRunningSummary() {
        return runningSummary;
    }

    public void setRunningSummary(String runningSummary) {
        this.runningSummary = runningSummary;
    }

    public List<String> getSourcesGathered() {
        return sourcesGathered;
    }

    public void setSourcesGathered(List<String> sourcesGathered) {
        this.sourcesGathered = sourcesGathered;
    }

    public List<String> getWebResearchResults() {
        return webResearchResults;
    }

    public void setWebResearchResults(List<String> webResearchResults) {
        this.webResearchResults = webResearchResults;
    }

    public int getResearchLoopCount() {
        return researchLoopCount;
    }

    public void setResearchLoopCount(int researchLoopCount) {
        this.researchLoopCount = researchLoopCount;
    }

    public ResearchMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(ResearchMetadata metadata) {
        this.metadata = metadata;
    }

    public void addWebResearchResult(String result) {
        this.webResearchResults.add(result);
    }

    public void addSourcesGathered(List<String> sources) {
        this.sourcesGathered.addAll(sources);
    }

    public void incrementResearchLoopCount() {
        this.researchLoopCount++;
    }
}

