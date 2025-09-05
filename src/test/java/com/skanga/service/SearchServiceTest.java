package com.skanga.service;

import com.skanga.config.ResearchConfiguration;
import com.skanga.model.SearchResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<String> httpResponse;

    private SearchService searchService;

    @BeforeEach
    void setUp() {
        searchService = new SearchService(httpClient);
    }

    @Test
    void search_duckDuckGo_success() throws IOException, InterruptedException {
        // Given
        ResearchConfiguration config = new ResearchConfiguration();
        config.setSearchApi("duckduckgo");

        String jsonResponse = "{\"AbstractText\":\"Test abstract\",\"AbstractURL\":\"http://example.com\"}";
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(jsonResponse);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);

        // When
        List<SearchResult> results = searchService.search("test query", config);

        // Then
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals("DuckDuckGo Instant Answer: test query", results.get(0).getTitle());
        assertEquals("Test abstract", results.get(0).getContent());
    }

    @Test
    void search_tavily_success() throws IOException, InterruptedException {
        // Given
        ResearchConfiguration config = new ResearchConfiguration();
        config.setSearchApi("tavily");
        // Use reflection to set the private API key field
        try {
            java.lang.reflect.Field apiKeyField = SearchService.class.getDeclaredField("tavilyApiKey");
            apiKeyField.setAccessible(true);
            apiKeyField.set(searchService, "test_api_key");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        String jsonResponse = "{\"answer\":\"Tavily answer\", \"results\":[{\"title\":\"Tavily result\",\"url\":\"http://example.com\",\"content\":\"Result content\",\"score\":0.9}]}";
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(jsonResponse);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);

        // When
        List<SearchResult> results = searchService.search("test query", config);

        // Then
        assertFalse(results.isEmpty());
        assertEquals(2, results.size());
        assertEquals("Tavily AI Answer: test query", results.get(0).getTitle());
    }

    @Test
    void search_perplexity_success() throws IOException, InterruptedException {
        // Given
        ResearchConfiguration config = new ResearchConfiguration();
        config.setSearchApi("perplexity");
        // Use reflection to set the private API key field
        try {
            java.lang.reflect.Field apiKeyField = SearchService.class.getDeclaredField("perplexityApiKey");
            apiKeyField.setAccessible(true);
            apiKeyField.set(searchService, "test_api_key");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        String jsonResponse = "{\"choices\":[{\"message\":{\"content\":\"Perplexity answer\"}}]}";
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(jsonResponse);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);

        // When
        List<SearchResult> results = searchService.search("test query", config);

        // Then
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals("Perplexity AI Response: test query", results.get(0).getTitle());
    }

    @Test
    void search_fallback_to_duckduckgo() throws IOException, InterruptedException {
        // Given
        ResearchConfiguration config = new ResearchConfiguration();
        config.setSearchApi("tavily"); // Tavily will fail

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new IOException("Tavily failed")) // First call fails
                .thenReturn(httpResponse); // Second call (fallback) succeeds

        String ddgJsonResponse = "{\"AbstractText\":\"DDG fallback abstract\",\"AbstractURL\":\"http://fallback.com\"}";
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(ddgJsonResponse);

        // When
        List<SearchResult> results = searchService.search("test query", config);

        // Then
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals("DuckDuckGo Instant Answer: test query", results.get(0).getTitle());
    }
}
