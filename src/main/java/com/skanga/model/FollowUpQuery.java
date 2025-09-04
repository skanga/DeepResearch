package com.skanga.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FollowUpQuery {
    @JsonProperty("follow_up_query")
    @JsonAlias({"followUpQuery", "follow-up-query", "followup_query"})
    private String followUpQuery;

    @JsonProperty("knowledge_gap")
    @JsonAlias({"knowledgeGap", "knowledge-gap", "knowledge_gap"})
    private String knowledgeGap;

    public FollowUpQuery() {
    }

    public String getFollowUpQuery() {
        return followUpQuery;
    }

    public void setFollowUpQuery(String followUpQuery) {
        this.followUpQuery = followUpQuery;
    }

    public String getKnowledgeGap() {
        return knowledgeGap;
    }

    public void setKnowledgeGap(String knowledgeGap) {
        this.knowledgeGap = knowledgeGap;
    }
}

