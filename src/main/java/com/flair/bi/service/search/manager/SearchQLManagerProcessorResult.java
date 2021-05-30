package com.flair.bi.service.search.manager;

import com.flair.bi.service.search.SearchResult;
import lombok.Builder;
import lombok.Data;

import java.util.Collections;

@Builder
@Data
public class SearchQLManagerProcessorResult {
    private final SearchResult searchResult;

    public static SearchQLManagerProcessorResult of(SearchResult searchResult) {
        return new SearchQLManagerProcessorResult(searchResult);
    }

    public static SearchQLManagerProcessorResult skip() {
        return new SearchQLManagerProcessorResult(null);
    }

    public static SearchQLManagerProcessorResult ofEmpty() {
        return of(new SearchResult(Collections.emptyList()));
    }

    public boolean stopProcessing() {
        return searchResult != null;
    }
}
