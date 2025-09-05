package com.skanga.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ModelClassesTest {

    @Test
    void testFollowUpQuery() {
        FollowUpQuery query = new FollowUpQuery();
        query.setFollowUpQuery("test query");
        query.setKnowledgeGap("test gap");
        assertEquals("test query", query.getFollowUpQuery());
        assertEquals("test gap", query.getKnowledgeGap());
    }

    @Test
    void testLLMProvider() {
        assertEquals(LLMProvider.OPENAI, LLMProvider.fromString("openai"));
        assertEquals("openai", LLMProvider.OPENAI.getValue());
        assertThrows(IllegalArgumentException.class, () -> LLMProvider.fromString("unknown"));
    }

    @Test
    void testResearchMetadata() {
        ResearchMetadata metadata = new ResearchMetadata();
        metadata.setLlmProvider("provider");
        metadata.setModelName("model");
        metadata.setLoopCount(2);
        metadata.incrementLlmCallCount();
        metadata.incrementSearchCallCount();

        assertEquals("provider", metadata.getLlmProvider());
        assertEquals("model", metadata.getModelName());
        assertEquals(2, metadata.getLoopCount());
        assertEquals(1, metadata.getLlmCallCount());
        assertEquals(1, metadata.getSearchCallCount());
    }

    @Test
    void testSearchAPI() {
        assertEquals(SearchAPI.TAVILY, SearchAPI.fromString("tavily"));
        assertEquals("tavily", SearchAPI.TAVILY.getValue());
        assertThrows(IllegalArgumentException.class, () -> SearchAPI.fromString("unknown"));
    }

    @Test
    void testSearchQuery() {
        SearchQuery query = new SearchQuery();
        query.setQuery("test query");
        query.setRationale("test rationale");
        assertEquals("test query", query.getQuery());
        assertEquals("test rationale", query.getRationale());
    }

    @Test
    void testSearchResponse() {
        SearchResponse response = new SearchResponse();
        SearchResult result = new SearchResult();
        response.addResult(result);
        assertEquals(1, response.getResults().size());
        response.setResults(List.of(new SearchResult(), new SearchResult()));
        assertEquals(2, response.getResults().size());
    }

    @Test
    void testSummaryState() {
        SummaryState state = new SummaryState("test topic");
        state.setSearchQuery("query");
        state.setRunningSummary("summary");
        state.addWebResearchResult("result");
        state.addSourcesGathered(List.of("source"));
        state.incrementResearchLoopCount();

        assertEquals("test topic", state.getResearchTopic());
        assertEquals("query", state.getSearchQuery());
        assertEquals("summary", state.getRunningSummary());
        assertEquals(1, state.getWebResearchResults().size());
        assertEquals(1, state.getSourcesGathered().size());
        assertEquals(1, state.getResearchLoopCount());
        assertNotNull(state.getMetadata());
    }
}
