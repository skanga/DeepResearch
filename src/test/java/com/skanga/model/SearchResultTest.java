package com.skanga.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SearchResultTest {

    @Test
    void testDefaultConstructor() {
        SearchResult result = new SearchResult();
        assertEquals(null, result.getTitle());
        assertEquals(null, result.getContent());
        assertEquals(null, result.getUrl());
        assertEquals(0.0, result.getScore());
    }

    @Test
    void testConstructorWithScore() {
        SearchResult result = new SearchResult("Title", "Content", "http://example.com", 0.9);
        assertEquals("Title", result.getTitle());
        assertEquals("Content", result.getContent());
        assertEquals("http://example.com", result.getUrl());
        assertEquals(0.9, result.getScore());
    }

    @Test
    void testConstructorWithRawContent() {
        SearchResult result = new SearchResult("Title", "Content", "http://example.com", "Raw Content", 0.9);
        assertEquals("Title", result.getTitle());
        assertEquals("Content", result.getContent());
        assertEquals("http://example.com", result.getUrl());
        assertEquals("Raw Content", result.getRawContent());
        assertEquals(0.9, result.getScore());
    }

    @Test
    void testGettersAndSetters() {
        SearchResult result = new SearchResult();
        result.setTitle("New Title");
        result.setContent("New Content");
        result.setUrl("http://new.com");
        result.setRawContent("New Raw Content");
        result.setScore(0.5);

        assertEquals("New Title", result.getTitle());
        assertEquals("New Content", result.getContent());
        assertEquals("http://new.com", result.getUrl());
        assertEquals("New Raw Content", result.getRawContent());
        assertEquals(0.5, result.getScore());
    }

    @Test
    void testToString() {
        SearchResult result = new SearchResult("Title", "Content", "http://example.com", 0.9);
        String expected = "SearchResult{title='Title', url='http://example.com', content='Content...', score=0.9}";
        assertEquals(expected, result.toString());
    }
}
