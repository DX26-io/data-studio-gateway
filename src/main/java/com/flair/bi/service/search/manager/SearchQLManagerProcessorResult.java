package com.flair.bi.service.search.manager;

import com.flair.bi.service.search.SearchQLResult;
import lombok.Builder;
import lombok.Data;

import java.util.Collections;

@Builder
@Data
public class SearchQLManagerProcessorResult {
    private final SearchQLResult searchQLResult;

    public static SearchQLManagerProcessorResult of(SearchQLResult searchQLResult) {
        return new SearchQLManagerProcessorResult(searchQLResult);
    }

    public static SearchQLManagerProcessorResult skip() {
        return new SearchQLManagerProcessorResult(null);
    }

    public static SearchQLManagerProcessorResult ofEmpty() {
        return of(new SearchQLResult(Collections.emptyList()));
    }

    public boolean stopProcessing() {
        return searchQLResult != null;
    }
}
