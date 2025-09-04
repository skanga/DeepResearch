package com.skanga.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SearchQuery {
    @JsonProperty("query")
    @JsonAlias({"query", "searchQuery", "search_query"})
    private String query;

    @JsonProperty("rationale")
    @JsonAlias({"rationale", "reasoning", "explanation"})
    private String rationale;

    public SearchQuery() {
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getRationale() {
        return rationale;
    }

    public void setRationale(String rationale) {
        this.rationale = rationale;
    }
}


