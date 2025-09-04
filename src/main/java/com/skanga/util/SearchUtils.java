package com.skanga.util;

import com.skanga.model.SearchResponse;
import com.skanga.model.SearchResult;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Utility class for search‑related helper methods.
 * All methods are static; the class cannot be instantiated.
 */
public final class SearchUtils {
    private SearchUtils() { /* prevent instantiation */ }
    
    private static final int CHARS_PER_TOKEN = 4;
    private static final Pattern THINKING_TOKEN_PATTERN = Pattern.compile("<think>.*?</think>", Pattern.DOTALL);
    
    /**
     * Convert configuration values to string format, handling both string and enum types.
     */
    public static String getConfigValue(Object value) {
        if (value instanceof String) {
            return (String) value;
        } else if (value instanceof Enum<?>) {
            return ((Enum<?>) value).name().toLowerCase();
        } else if (value != null) {
            return value.toString();
        }
        return "";
    }
    
    /**
     * Remove <think> and </think> tags and their content from the text.
     * Iteratively removes all occurrences of content enclosed in thinking tokens.
     */
    public static String stripThinkingTokens(String text) {
        if (text == null) return null;
        
        Matcher matcher = THINKING_TOKEN_PATTERN.matcher(text);
        return matcher.replaceAll("").trim();
    }
    
    /**
     * Format and deduplicate search responses from various search APIs.
     */
    public static String deduplicateAndFormatSources(
            Object searchResponse, 
            int maxTokensPerSource, 
            boolean fetchFullPage) {
        
        List<SearchResult> sourcesList = extractSearchResults(searchResponse);
        
        // Deduplicate by URL
        Map<String, SearchResult> uniqueSources = new LinkedHashMap<>();
        for (SearchResult source : sourcesList) {
            if (source.getUrl() != null && !uniqueSources.containsKey(source.getUrl())) {
                uniqueSources.put(source.getUrl(), source);
            }
        }
        
        // Format output
        StringBuilder formattedText = new StringBuilder("Sources:\n\n");
        int counter = 1;
        
        for (SearchResult source : uniqueSources.values()) {
            formattedText.append("Source: ").append(source.getTitle()).append("\n===\n");
            formattedText.append("URL: ").append(source.getUrl()).append("\n===\n");
            formattedText.append("Most relevant content from source: ")
                         .append(source.getContent()).append("\n===\n");
            
            if (fetchFullPage && source.getRawContent() != null) {
                int charLimit = maxTokensPerSource * CHARS_PER_TOKEN;
                String rawContent = source.getRawContent();
                
                if (rawContent.length() > charLimit) {
                    rawContent = rawContent.substring(0, charLimit) + "... [truncated]";
                }
                
                formattedText.append("Full source content limited to ")
                           .append(maxTokensPerSource)
                           .append(" tokens: ")
                           .append(rawContent)
                           .append("\n\n");
            }
            counter++;
        }
        
        return formattedText.toString().trim();
    }
    
    /**
     * Extract search results from various response formats
     */
    @SuppressWarnings("unchecked")
    private static List<SearchResult> extractSearchResults(Object searchResponse) {
        List<SearchResult> sourcesList = new ArrayList<>();
        
        if (searchResponse instanceof SearchResponse) {
            return ((SearchResponse) searchResponse).getResults();
        } else if (searchResponse instanceof Map) {
            Map<String, Object> responseMap = (Map<String, Object>) searchResponse;
            Object results = responseMap.get("results");
            if (results instanceof List) {
                List<Object> resultsList = (List<Object>) results;
                for (Object result : resultsList) {
                    if (result instanceof SearchResult) {
                        sourcesList.add((SearchResult) result);
                    } else if (result instanceof Map) {
                        sourcesList.add(mapToSearchResult((Map<String, Object>) result));
                    }
                }
            }
        } else if (searchResponse instanceof List) {
            List<Object> responseList = (List<Object>) searchResponse;
            for (Object response : responseList) {
                if (response instanceof SearchResponse) {
                    sourcesList.addAll(((SearchResponse) response).getResults());
                } else if (response instanceof Map) {
                    Map<String, Object> responseMap = (Map<String, Object>) response;
                    Object results = responseMap.get("results");
                    if (results instanceof List) {
                        List<Object> resultsList = (List<Object>) results;
                        for (Object result : resultsList) {
                            if (result instanceof SearchResult) {
                                sourcesList.add((SearchResult) result);
                            } else if (result instanceof Map) {
                                sourcesList.add(mapToSearchResult((Map<String, Object>) result));
                            }
                        }
                    }
                }
            }
        }
        
        return sourcesList;
    }
    
    /**
     * Convert a Map to SearchResult
     */
    private static SearchResult mapToSearchResult(Map<String, Object> map) {
        SearchResult result = new SearchResult();
        result.setTitle((String) map.get("title"));
        result.setUrl((String) map.get("url"));
        result.setContent((String) map.get("content"));
        result.setRawContent((String) map.get("raw_content"));
        return result;
    }
    
    /**
     * Format search results into a bullet-point list of sources with URLs.
     */
    public static String formatSources(SearchResponse searchResults) {
        if (searchResults == null || searchResults.getResults() == null) {
            return "";
        }
        
        return searchResults.getResults().stream()
                .filter(result -> result.getTitle() != null && result.getUrl() != null)
                .map(result -> "* " + result.getTitle() + " : " + result.getUrl())
                .collect(Collectors.joining("\n"));
    }
}

