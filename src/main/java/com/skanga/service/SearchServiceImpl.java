package com.skanga.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skanga.exception.SearchException;
import com.skanga.model.SearchResponse;
import com.skanga.model.SearchResult;
import com.skanga.util.WebContentFetcher;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

// Search service implementations
@Service
public class SearchServiceImpl {

    private final WebContentFetcher webContentFetcher;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public SearchServiceImpl(WebContentFetcher webContentFetcher) {
        this.webContentFetcher = webContentFetcher;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Search the web using DuckDuckGo and return formatted results.
     * Uses the public DuckDuckGo Instant Answer API (JSON).
     */
    public SearchResponse duckduckgoSearch(String query, int maxResults, boolean fetchFullPage) {
        SearchResponse response = new SearchResponse();
        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String searchUrl = "https://api.duckduckgo.com/?q=" + encodedQuery + "&format=json&no_html=1";
            String json = restTemplate.getForObject(searchUrl, String.class);
            if (json == null) return response;
            var root = objectMapper.readTree(json);
            var topics = root.path("RelatedTopics");
            int added = 0;
            for (var node : topics) {
                if (added >= maxResults) break;
                // Some entries are nested under "Topics"
                if (node.has("Topics")) {
                    for (var sub : node.get("Topics")) {
                        if (added >= maxResults) break;
                        addDuckResult(sub, query, fetchFullPage, response);
                        added++;
                    }
                } else {
                    addDuckResult(node, query, fetchFullPage, response);
                    added++;
                }
            }
        } catch (IOException e) {
            throw new SearchException("Error in DuckDuckGo search", e);
        }
        return response;
    }

    private void addDuckResult(JsonNode node, String query, boolean fetchFullPage, SearchResponse response) {
        String title = node.path("Text").asText();
        String url = node.path("FirstURL").asText();
        if (title.isEmpty() || url.isEmpty()) return;
        SearchResult result = new SearchResult();
        result.setTitle(title);
        result.setUrl(url);
        result.setContent(title); // DuckDuckGo does not provide a snippet; use title as content placeholder
        String raw = resolveContent(url, fetchFullPage, result.getContent());
        result.setRawContent(raw);
        response.addResult(result);
    }

    /**
     * Search the web using SearXNG and return formatted results.
     * Expects a SearXNG instance exposing the JSON API (default http://localhost:8888).
     */
    public SearchResponse searxngSearch(String query, int maxResults, boolean fetchFullPage) {
        SearchResponse response = new SearchResponse();
        try {
            String searxngUrl = System.getenv().getOrDefault("SEARXNG_URL", "http://localhost:8888");
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String searchUrl = searxngUrl + "/search?q=" + encodedQuery + "&format=json";
            String json = restTemplate.getForObject(searchUrl, String.class);
            if (json == null) return response;
            var root = objectMapper.readTree(json);
            var results = root.path("results");
            int added = 0;
            for (var node : results) {
                if (added >= maxResults) break;
                addSearxResult(node, fetchFullPage, response);
                added++;
            }
        } catch (IOException e) {
            throw new SearchException("Error in SearXNG search", e);
        }
        return response;
    }

    private void addSearxResult(com.fasterxml.jackson.databind.JsonNode node, boolean fetchFullPage, SearchResponse response) {
        String title = node.path("title").asText();
        String url = node.path("url").asText();
        String content = node.path("content").asText();
        if (title.isEmpty() || url.isEmpty()) return;
        SearchResult result = new SearchResult();
        result.setTitle(title);
        result.setUrl(url);
        result.setContent(content.isEmpty() ? title : content);
        String raw = resolveContent(url, fetchFullPage, result.getContent());
        result.setRawContent(raw);
        response.addResult(result);
    }

    /**
     * Centralised helper to fetch raw page content when requested.
     */
    private String resolveContent(String url, boolean fetchFullPage, String fallback) {
        if (!fetchFullPage) {
            return fallback;
        }
        return webContentFetcher.fetchRawContent(url).orElse(fallback);
    }

    /**
     * Search the web using the Tavily API and return formatted results.
     */
    public SearchResponse tavilySearch(String query, boolean fetchFullPage, int maxResults) {
        SearchResponse response = new SearchResponse();
        try {
            String apiKey = System.getenv("TAVILY_API_KEY");
            if (apiKey == null || apiKey.isEmpty()) {
                System.err.println("TAVILY_API_KEY environment variable not set");
                return response;
            }
            String url = "https://api.tavily.com/search";
            // Build request payload
            var payload = new java.util.HashMap<String, Object>();
            payload.put("api_key", apiKey);
            payload.put("query", query);
            payload.put("max_results", maxResults);
            payload.put("search_depth", "basic");
            // Use RestTemplate to POST JSON
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            org.springframework.http.HttpEntity<java.util.Map<String, Object>> request = new org.springframework.http.HttpEntity<>(payload, headers);
            String json = restTemplate.postForObject(url, request, String.class);
            if (json == null) return response;
            var root = objectMapper.readTree(json);
            var results = root.path("results");
            for (var node : results) {
                String title = node.path("title").asText();
                String resultUrl = node.path("url").asText();
                String content = node.path("content").asText();
                SearchResult result = new SearchResult();
                result.setTitle(title.isEmpty() ? query : title);
                result.setUrl(resultUrl);
                result.setContent(content.isEmpty() ? title : content);
                String raw = resolveContent(resultUrl, fetchFullPage, result.getContent());
                result.setRawContent(raw);
                response.addResult(result);
            }
        } catch (Exception e) {
            System.err.println("Error in Tavily search: " + e.getMessage());
        }
        return response;
    }

    /**
     * Search the web using the Perplexity API and return formatted results.
     */
    public SearchResponse perplexitySearch(String query, int perplexitySearchLoopCount) {
        SearchResponse response = new SearchResponse();
        try {
            String apiKey = System.getenv("PERPLEXITY_API_KEY");
            if (apiKey == null || apiKey.isEmpty()) {
                System.err.println("PERPLEXITY_API_KEY environment variable not set");
                return response;
            }
            String url = "https://api.perplexity.ai/chat/completions";
            // Build request payload
            var payload = new java.util.HashMap<String, Object>();
            payload.put("model", "sonar-pro");
            List<Map<String, String>> messages = List.of(
                    Map.of("role", "system", "content", "Search the web and provide factual information with sources."),
                    Map.of("role", "user", "content", query)
            );
            payload.put("messages", messages);
            // Headers
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);
            org.springframework.http.HttpEntity<Map<String, Object>> request = new org.springframework.http.HttpEntity<>(payload, headers);
            String json = restTemplate.postForObject(url, request, String.class);
            if (json == null) return response;
            var root = objectMapper.readTree(json);
            var choices = root.path("choices");
            if (choices.isArray() && !choices.isEmpty()) {
                var message = choices.get(0).path("message");
                String content = message.path("content").asText();
                SearchResult result = new SearchResult();
                result.setTitle("Perplexity Search " + (perplexitySearchLoopCount + 1));
                result.setUrl("https://perplexity.ai");
                result.setContent(content);
                result.setRawContent(content);
                response.addResult(result);
            }
            // Optional: handle citations if present (simplified)
            var citations = root.path("citations");
            if (citations.isArray()) {
                int idx = 2;
                for (var cit : citations) {
                    String citUrl = cit.path("url").asText();
                    if (citUrl.isEmpty()) continue;
                    SearchResult citResult = new SearchResult();
                    citResult.setTitle("Perplexity Citation " + idx);
                    citResult.setUrl(citUrl);
                    citResult.setContent("Citation source");
                    citResult.setRawContent(null);
                    response.addResult(citResult);
                    idx++;
                }
            }
        } catch (Exception e) {
            System.err.println("Error in Perplexity search: " + e.getMessage());
        }
        return response;
    }
}
