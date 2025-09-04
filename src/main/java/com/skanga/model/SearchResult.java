package com.skanga.model;

// Unified SearchResult class that combines features from both versions
public class SearchResult {
    private String title;
    private String content;
    private String url;
    private String rawContent;
    private double score;
    
    public SearchResult() {}
    
    public SearchResult(String title, String content, String url, double score) {
        this.title = title;
        this.content = content;
        this.url = url;
        this.score = score;
    }
    
    public SearchResult(String title, String content, String url, String rawContent, double score) {
        this.title = title;
        this.content = content;
        this.url = url;
        this.rawContent = rawContent;
        this.score = score;
    }
    
    // Getters and setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    
    public String getRawContent() { return rawContent; }
    public void setRawContent(String rawContent) { this.rawContent = rawContent; }
    
    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }
    
    @Override
    public String toString() {
        return "SearchResult{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", content='" + (content != null ? content.substring(0, Math.min(content.length(), 100)) : "null") + "...'" +
                ", score=" + score +
                '}';
    }
}