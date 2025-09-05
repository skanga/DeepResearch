package com.skanga.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skanga.config.ResearchConfiguration;
import com.skanga.model.SearchResult;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SearchService {
    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Value("${search.tavily.api.key:}")
    private String tavilyApiKey;

    @Value("${search.perplexity.api.key:}")
    private String perplexityApiKey;

    public SearchService() {
        this(HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build());
    }

    // Constructor for testing
    public SearchService(HttpClient httpClient) {
        logger.info("=== SearchService: Initializing JDK HttpClient ===");
        this.httpClient = httpClient;
        this.objectMapper = new ObjectMapper();
        logger.info("=== SearchService: JDK HttpClient initialized ===");
    }

    public List<SearchResult> search(String query, ResearchConfiguration config) {
        logger.info("=== SearchService: Starting search ===");
        logger.info("Query: {}", query);
        logger.info("Search API: {}", config.getSearchApi());

        try {
            switch (config.getSearchApi().toLowerCase()) {
                case "duckduckgo":
                    logger.info("Using DuckDuckGo search");
                    return duckDuckGoSearch(query, 3);
                case "tavily":
                    logger.info("Using Tavily search");
                    return tavilySearch(query, 5);
                case "perplexity":
                    logger.info("Using Perplexity search");
                    return perplexitySearch(query);
                default:
                    logger.warn("Unknown search API: {}, defaulting to DuckDuckGo", config.getSearchApi());
                    return duckDuckGoSearch(query, 3);
            }
        } catch (Exception e) {
            logger.error("Search failed, falling back to DuckDuckGo", e);
            try {
                return duckDuckGoSearch(query, 3);
            } catch (Exception fallbackException) {
                logger.error("Fallback search also failed", fallbackException);
                return createErrorResults(query, "Search service temporarily unavailable");
            }
        }
    }

    private List<SearchResult> duckDuckGoSearch(String query, int maxResults) throws IOException, InterruptedException {
        logger.info("=== SearchService: DuckDuckGo search for: {} ===", query);

        // DuckDuckGo Instant Answer API (limited but free)
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = "https://api.duckduckgo.com/?q=" + encodedQuery + "&format=json&no_redirect=1&no_html=1&skip_disambig=1";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "Mozilla/5.0 (compatible; SearchService/1.0)")
                .timeout(Duration.ofSeconds(15))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            logger.warn("DuckDuckGo API returned status: {}", response.statusCode());
            return fallbackWebSearch(query, maxResults);
        }

        List<SearchResult> results = new ArrayList<>();

        try {
            JsonNode jsonResponse = objectMapper.readTree(response.body());

            // Try to get instant answer
            if (jsonResponse.has("AbstractText") && !jsonResponse.get("AbstractText").asText().isEmpty()) {
                results.add(new SearchResult(
                        "DuckDuckGo Instant Answer: " + query,
                        jsonResponse.get("AbstractText").asText(),
                        jsonResponse.has("AbstractURL") ? jsonResponse.get("AbstractURL").asText() : "https://duckduckgo.com",
                        0.9
                ));
            }

            // Try to get related topics
            if (jsonResponse.has("RelatedTopics") && jsonResponse.get("RelatedTopics").isArray()) {
                for (JsonNode topic : jsonResponse.get("RelatedTopics")) {
                    if (topic.has("Text") && topic.has("FirstURL") && results.size() < maxResults) {
                        results.add(new SearchResult(
                                "Related: " + extractTitle(topic.get("Text").asText()),
                                topic.get("Text").asText(),
                                topic.get("FirstURL").asText(),
                                0.7
                        ));
                    }
                }
            }

            // If no results from API, try web scraping as fallback
            if (results.isEmpty()) {
                logger.info("No results from DuckDuckGo API, trying web search fallback");
                return fallbackWebSearch(query, maxResults);
            }

        } catch (Exception e) {
            logger.error("Error parsing DuckDuckGo response", e);
            return fallbackWebSearch(query, maxResults);
        }

        logger.info("Returning {} DuckDuckGo search results", results.size());
        return results.stream().limit(maxResults).collect(Collectors.toList());
    }

    private List<SearchResult> fallbackWebSearch(String query, int maxResults) {
        logger.info("Performing fallback web search for: {}", query);

        try {
            // Use a search engine that allows scraping (like Startpage or Searx)
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String searchUrl = "https://html.duckduckgo.com/html/?q=" + encodedQuery;

            Document doc = Jsoup.connect(searchUrl)
                    .userAgent("Mozilla/5.0 (compatible; SearchService/1.0)")
                    .timeout(15000)
                    .get();

            List<SearchResult> results = new ArrayList<>();
            Elements searchResults = doc.select(".result");

            for (Element result : searchResults) {
                if (results.size() >= maxResults) break;

                Element titleElement = result.selectFirst(".result__title a");
                Element snippetElement = result.selectFirst(".result__snippet");

                if (titleElement != null && snippetElement != null) {
                    String title = titleElement.text();
                    String url = titleElement.attr("href");
                    String snippet = snippetElement.text();

                    results.add(new SearchResult(title, snippet, url, 0.8));
                }
            }

            if (results.isEmpty()) {
                return createErrorResults(query, "No search results found");
            }

            return results;

        } catch (Exception e) {
            logger.error("Fallback web search failed", e);
            return createErrorResults(query, "Web search temporarily unavailable");
        }
    }

    private List<SearchResult> tavilySearch(String query, int maxResults) throws IOException, InterruptedException {
        logger.info("=== SearchService: Tavily search for: {} ===", query);

        if (tavilyApiKey == null || tavilyApiKey.trim().isEmpty()) {
            logger.warn("Tavily API key not configured, falling back to DuckDuckGo");
            return duckDuckGoSearch(query, maxResults);
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("api_key", tavilyApiKey);
        requestBody.put("query", query);
        requestBody.put("search_depth", "basic");
        requestBody.put("max_results", maxResults);
        requestBody.put("include_answer", true);
        requestBody.put("include_domains", new ArrayList<>());
        requestBody.put("exclude_domains", new ArrayList<>());

        String jsonBody = objectMapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.tavily.com/search"))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            logger.error("Tavily API returned status: {} - {}", response.statusCode(), response.body());
            throw new IOException("Tavily API request failed with status: " + response.statusCode());
        }

        List<SearchResult> results = new ArrayList<>();

        try {
            JsonNode jsonResponse = objectMapper.readTree(response.body());

            // Add answer if available
            if (jsonResponse.has("answer") && !jsonResponse.get("answer").asText().isEmpty()) {
                results.add(new SearchResult(
                        "Tavily AI Answer: " + query,
                        jsonResponse.get("answer").asText(),
                        "https://tavily.com",
                        0.95
                ));
            }

            // Add search results
            if (jsonResponse.has("results") && jsonResponse.get("results").isArray()) {
                for (JsonNode result : jsonResponse.get("results")) {
                    if (results.size() >= maxResults) break;

                    String title = result.has("title") ? result.get("title").asText() : "Untitled";
                    String content = result.has("content") ? result.get("content").asText() : "";
                    String url = result.has("url") ? result.get("url").asText() : "";
                    double score = result.has("score") ? result.get("score").asDouble() : 0.5;

                    results.add(new SearchResult(title, content, url, score));
                }
            }

        } catch (Exception e) {
            logger.error("Error parsing Tavily response", e);
            throw new IOException("Failed to parse Tavily response", e);
        }

        logger.info("Returning {} Tavily search results", results.size());
        return results;
    }

    private List<SearchResult> perplexitySearch(String query) throws IOException, InterruptedException {
        logger.info("=== SearchService: Perplexity search for: {} ===", query);

        if (perplexityApiKey == null || perplexityApiKey.trim().isEmpty()) {
            logger.warn("Perplexity API key not configured, falling back to DuckDuckGo");
            return duckDuckGoSearch(query, 3);
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "llama-3.1-sonar-small-128k-online");

        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", query);
        messages.add(message);

        requestBody.put("messages", messages);
        requestBody.put("max_tokens", 1000);
        requestBody.put("temperature", 0.2);
        requestBody.put("top_p", 0.9);
        requestBody.put("return_citations", true);
        requestBody.put("search_domain_filter", new ArrayList<>());
        requestBody.put("return_images", false);
        requestBody.put("return_related_questions", false);
        requestBody.put("search_recency_filter", "month");

        String jsonBody = objectMapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.perplexity.ai/chat/completions"))
                .header("Authorization", "Bearer " + perplexityApiKey)
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            logger.error("Perplexity API returned status: {} - {}", response.statusCode(), response.body());
            throw new IOException("Perplexity API request failed with status: " + response.statusCode());
        }

        List<SearchResult> results = new ArrayList<>();

        try {
            JsonNode jsonResponse = objectMapper.readTree(response.body());

            if (jsonResponse.has("choices") && jsonResponse.get("choices").isArray()) {
                JsonNode choice = jsonResponse.get("choices").get(0);

                if (choice.has("message") && choice.get("message").has("content")) {
                    String content = choice.get("message").get("content").asText();

                    results.add(new SearchResult(
                            "Perplexity AI Response: " + query,
                            content,
                            "https://perplexity.ai",
                            0.9
                    ));
                }
            }

            // Extract citations if available
            if (jsonResponse.has("citations") && jsonResponse.get("citations").isArray()) {
                for (JsonNode citation : jsonResponse.get("citations")) {
                    if (results.size() >= 3) break; // Limit total results

                    String url = citation.asText();
                    // You could fetch the actual content from these URLs if needed
                    results.add(new SearchResult(
                            "Source: " + extractDomainFromUrl(url),
                            "Referenced source from Perplexity search",
                            url,
                            0.7
                    ));
                }
            }

        } catch (Exception e) {
            logger.error("Error parsing Perplexity response", e);
            throw new IOException("Failed to parse Perplexity response", e);
        }

        logger.info("Returning {} Perplexity search results", results.size());
        return results;
    }

    private List<SearchResult> createErrorResults(String query, String errorMessage) {
        List<SearchResult> errorResults = new ArrayList<>();
        errorResults.add(new SearchResult(
                "Search Error",
                errorMessage + " for query: " + query,
                "",
                0.1
        ));
        return errorResults;
    }

    private String extractTitle(String text) {
        return text.length() > 60 ? text.substring(0, 60) + "..." : text;
    }

    private String extractDomainFromUrl(String url) {
        try {
            return URI.create(url).getHost();
        } catch (Exception e) {
            return url;
        }
    }

    public String formatResults(List<SearchResult> results, int maxTokensPerSource) {
        logger.debug("=== SearchService: Formatting {} results ===", results.size());
        logger.debug("Max tokens per source: {}", maxTokensPerSource);

        StringBuilder formatted = new StringBuilder();

        for (int i = 0; i < results.size(); i++) {
            SearchResult result = results.get(i);
            logger.debug("Formatting result {}: {}", i + 1, result.getTitle());

            formatted.append("Title: ").append(result.getTitle()).append("\n");
            formatted.append("URL: ").append(result.getUrl()).append("\n");

            String content = result.getContent();
            // Truncate content to max tokens (approximating 4 chars per token)
            if (content.length() > maxTokensPerSource * 4) {
                content = content.substring(0, maxTokensPerSource * 4) + "...";
                logger.debug("Truncated content to {} chars", content.length());
            }

            formatted.append("Content: ").append(content).append("\n");
            formatted.append("Score: ").append(String.format("%.2f", result.getScore())).append("\n");
            formatted.append("---\n");
        }

        String result = formatted.toString();
        logger.debug("Formatted results length: {} chars", result.length());
        return result;
    }

    public List<String> formatSources(List<SearchResult> results) {
        logger.debug("=== SearchService: Formatting {} sources ===", results.size());

        List<String> sources = results.stream()
                .map(result -> {
                    String source = String.format("[%s](%s)", result.getTitle(), result.getUrl());
                    logger.debug("Formatted source: {}", source);
                    return source;
                })
                .collect(Collectors.toList());

        logger.debug("Formatted {} sources", sources.size());
        return sources;
    }
}
