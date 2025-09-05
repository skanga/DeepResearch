package com.skanga.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

// HTTP utilities for web content fetching with JDK HttpClient
@Component
public class WebContentFetcher {
    
    private final HttpClient httpClient;
    
    public WebContentFetcher() {
        this(HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build());
    }

    // Constructor for testing
    public WebContentFetcher(HttpClient httpClient) {
        this.httpClient = httpClient;
    }
    
    /**
     * Fetch HTML content from a URL and convert it to Markdown format.
     * Uses a 10-second timeout to avoid hanging on slow sites or large pages.
     */
    public Optional<String> fetchRawContent(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .header("User-Agent", "Mozilla/5.0 (compatible; ResearchBot/1.0)")
                    .GET()
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                String html = response.body();
                return Optional.of(htmlToMarkdown(html));
            } else {
                System.err.println("HTTP error " + response.statusCode() + " for URL: " + url);
                return Optional.empty();
            }
        } catch (Exception e) {
            System.err.println("Warning: Failed to fetch full page content for " + url + ": " + e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Convert HTML to markdown using JSoup for parsing
     */
    private String htmlToMarkdown(String html) {
        try {
            Document doc = Jsoup.parse(html);
            
            // Remove script and style elements
            doc.select("script, style").remove();
            
            // Extract text content
            String text = doc.body().text();
            
            // Basic markdown conversion (you might want to use a proper HTML to markdown library)
            return text.replaceAll("\\s+", " ").trim();
        } catch (Exception e) {
            System.err.println("Error converting HTML to markdown: " + e.getMessage());
            return html; // Return original HTML if conversion fails
        }
    }
}
