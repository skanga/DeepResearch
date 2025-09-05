package com.skanga.service;

import com.skanga.config.ResearchConfiguration;
import com.skanga.model.FollowUpQuery;
import com.skanga.model.SearchQuery;
import com.skanga.model.SearchResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResearchServiceTest {

    @Mock
    private SearchService searchService;

    @Mock
    private ResearchConfiguration researchConfiguration;

    @Mock
    private QueryGeneratorService queryGeneratorService;

    @Mock
    private SummarizerService summarizerService;

    @Mock
    private ReflectionService reflectionService;

    private ResearchService researchService;

    @BeforeEach
    void setUp() {
        researchService = new ResearchService(
                searchService,
                researchConfiguration,
                queryGeneratorService,
                summarizerService,
                reflectionService
        );
    }

    @Test
    void conductResearch_singleLoop() throws ExecutionException, InterruptedException {
        // Given
        String researchTopic = "test topic";
        SearchQuery initialQuery = new SearchQuery();
        initialQuery.setQuery("initial query");
        initialQuery.setRationale("initial rationale");

        when(researchConfiguration.getMaxWebResearchLoops()).thenReturn(1);
        when(researchConfiguration.getLlmProvider()).thenReturn("test-provider");
        when(researchConfiguration.getModelName()).thenReturn("test-model");
        when(queryGeneratorService.generateQuery(anyString(), anyString())).thenReturn(initialQuery);
        when(searchService.search(anyString(), any())).thenReturn(Collections.singletonList(new SearchResult("title", "content", "url", 0.8)));
        when(searchService.formatResults(any(), anyInt())).thenReturn("formatted results");
        when(searchService.formatSources(any())).thenReturn(Collections.singletonList("formatted sources"));
        when(summarizerService.summarize(anyString(), anyString(), anyString())).thenReturn("final summary");

        // When
        CompletableFuture<String> future = researchService.conductResearch(researchTopic);
        String result = future.get();

        // Then
        assertNotNull(result);
        assertTrue(result.contains("final summary"));
        assertTrue(result.contains("formatted sources"));
    }

    @Test
    void conductResearch_multipleLoops() throws ExecutionException, InterruptedException {
        // Given
        String researchTopic = "multi-loop topic";
        SearchQuery initialQuery = new SearchQuery();
        initialQuery.setQuery("initial query");
        initialQuery.setRationale("initial rationale");

        FollowUpQuery followUpQuery = new FollowUpQuery();
        followUpQuery.setFollowUpQuery("follow-up query");
        followUpQuery.setKnowledgeGap("knowledge gap");

        when(researchConfiguration.getMaxWebResearchLoops()).thenReturn(2);
        when(researchConfiguration.getLlmProvider()).thenReturn("test-provider");
        when(researchConfiguration.getModelName()).thenReturn("test-model");
        when(queryGeneratorService.generateQuery(anyString(), anyString())).thenReturn(initialQuery);
        when(searchService.search(anyString(), any())).thenReturn(Collections.singletonList(new SearchResult("title", "content", "url", 0.8)));
        when(searchService.formatResults(any(), anyInt())).thenReturn("formatted results");
        when(searchService.formatSources(any())).thenReturn(Collections.singletonList("formatted sources"));
        when(summarizerService.summarize(anyString(), anyString(), anyString()))
                .thenReturn("intermediate summary")
                .thenReturn("final summary");
        when(reflectionService.generateFollowUpQuery(anyString(), anyString())).thenReturn(followUpQuery);

        // When
        CompletableFuture<String> future = researchService.conductResearch(researchTopic);
        String result = future.get();

        // Then
        assertNotNull(result);
        assertTrue(result.contains("final summary"));
    }
}
