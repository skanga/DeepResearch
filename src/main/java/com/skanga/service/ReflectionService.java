package com.skanga.service;

import com.skanga.model.FollowUpQuery;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

interface ReflectionService {
    @SystemMessage("""
        You are a research analyst. Analyze the current summary and identify knowledge gaps 
        that need further investigation.
        
        Research topic: {{researchTopic}}
        
        Your task is to:
        1. Identify what information is missing or needs clarification
        2. Generate a specific follow-up query to address the most important gap
        3. Focus on areas that would significantly enhance understanding of the topic
        
        Return your response as a JSON object with exactly these fields:
        {
          "follow_up_query": "your generated query",
          "knowledge_gap": "description of the knowledge gap"
        }
        
        IMPORTANT: Use exactly these field names in your JSON response.
        """)
    @UserMessage("""
        <Current Summary>
        {{runningSummary}}
        </Current Summary>
        
        <Research Topic>
        {{researchTopic}}
        </Research Topic>
        
        Analyze the current summary and identify a knowledge gap. Generate a follow-up web search query.
        Return your response as JSON with the exact format specified above.
        """)
    FollowUpQuery generateFollowUpQuery(@V("runningSummary") String runningSummary,
                                        @V("researchTopic") String researchTopic);
}

