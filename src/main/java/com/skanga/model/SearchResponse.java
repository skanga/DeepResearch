package com.skanga.model;

import java.util.ArrayList;
import java.util.List;

// Search response wrapper
public class SearchResponse {
    private List<SearchResult> results;
    
    public SearchResponse() {
        this.results = new ArrayList<>();
    }
    
    public SearchResponse(List<SearchResult> results) {
        this.results = results;
    }
    
    public List<SearchResult> getResults() { return results; }
    public void setResults(List<SearchResult> results) { this.results = results; }
    
    public void addResult(SearchResult result) {
        this.results.add(result);
    }
}
