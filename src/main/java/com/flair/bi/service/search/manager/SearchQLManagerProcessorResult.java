package com.flair.bi.service.search.manager;

import com.flair.bi.service.search.SearchResult;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SearchQLManagerProcessorResult {
    private final SearchResult searchResult;

    public static SearchQLManagerProcessorResult of(SearchResult searchResult) {
        return new SearchQLManagerProcessorResult(searchResult);
    }

    public static SearchQLManagerProcessorResult empty() {
        return new SearchQLManagerProcessorResult(null);
    }

    public boolean stopProcessing() {
        return searchResult != null;
    }
}
