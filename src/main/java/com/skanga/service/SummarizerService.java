package com.skanga.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

interface SummarizerService {
    @SystemMessage("""
            You are an expert research summarizer. Your task is to create comprehensive, well-structured summaries.
            
            Guidelines:
            - Synthesize information from multiple sources
            - Maintain factual accuracy
            - Organize information logically
            - Highlight key findings and insights
            - Keep the summary concise but comprehensive
            """)
    @UserMessage("""
            <Existing Summary>
            {{existingSummary}}
            </Existing Summary>
            
            <New Context>
            {{newContext}}
            </New Context>
            
            Update the Existing Summary with the New Context on this topic:
            <User Input>
            {{researchTopic}}
            </User Input>
            
            If the Existing Summary is empty, just create a new summary from the New Context.
            """)
    String summarize(@V("existingSummary") String existingSummary,
                     @V("newContext") String newContext,
                     @V("researchTopic") String researchTopic);
}
