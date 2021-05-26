package com.flair.bi.service.search.manager;

import com.flair.bi.compiler.search.SearchResult;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SearchQLManagerInput {
    private final Long viewId;
    private final SearchResult compiledQuery;
}
