package com.skanga.util;

import com.skanga.model.SearchResponse;
import com.skanga.model.SearchResult;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SearchUtilsTest {

    private enum TestEnum { VALUE1, VALUE2 }

    @Test
    void getConfigValue_withString() {
        assertEquals("test", SearchUtils.getConfigValue("test"));
    }

    @Test
    void getConfigValue_withEnum() {
        assertEquals("value1", SearchUtils.getConfigValue(TestEnum.VALUE1));
    }

    @Test
    void getConfigValue_withObject() {
        assertEquals("123", SearchUtils.getConfigValue(123));
    }

    @Test
    void stripThinkingTokens_removesTokens() {
        String text = "<think>some thoughts</think>Some text<think>more thoughts</think>";
        assertEquals("Some text", SearchUtils.stripThinkingTokens(text));
    }

    @Test
    void stripThinkingTokens_noTokens() {
        String text = "Some text without tokens";
        assertEquals(text, SearchUtils.stripThinkingTokens(text));
    }

    @Test
    void deduplicateAndFormatSources_withSearchResponse() {
        SearchResponse response = new SearchResponse();
        response.addResult(new SearchResult("Title 1", "Content 1", "http://example.com/1", 0.9));
        response.addResult(new SearchResult("Title 2", "Content 2", "http://example.com/1", 0.8)); // Duplicate URL

        String formatted = SearchUtils.deduplicateAndFormatSources(response, 100, false);

        assertEquals("Sources:\n\nSource: Title 1\n===\nURL: http://example.com/1\n===\nMost relevant content from source: Content 1\n===", formatted.trim());
    }

    @Test
    void formatSources_withSearchResponse() {
        SearchResponse response = new SearchResponse();
        response.addResult(new SearchResult("Title 1", "Content 1", "http://example.com/1", 0.9));
        response.addResult(new SearchResult("Title 2", "Content 2", "http://example.com/2", 0.8));

        String formatted = SearchUtils.formatSources(response);

        assertEquals("* Title 1 : http://example.com/1\n* Title 2 : http://example.com/2", formatted);
    }
}
