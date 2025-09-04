package com.skanga.service;

import com.skanga.model.SearchQuery;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

// AI Services interfaces for structured output
interface QueryGeneratorService {
    @SystemMessage("""
        You are a research query generator. Generate an optimized search query for web research.
        Current date: {{currentDate}}
        Research topic: {{researchTopic}}
        
        Generate a focused, specific search query that will yield the most relevant information.
        Consider using specific terms, avoiding overly broad queries, and including relevant keywords.
        
        Return your response as a JSON object with exactly these fields:
        {
          "query": "your generated search query",
          "rationale": "brief explanation of why this query is effective"
        }
        """)
    @UserMessage("""
        Generate a search query for web research on this topic: {{researchTopic}}
        
        Return your response as JSON with the exact format specified.
        """)
    SearchQuery generateQuery(@V("researchTopic") String researchTopic, @V("currentDate") String currentDate);
}

