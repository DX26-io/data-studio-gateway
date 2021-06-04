package com.flair.bi.service.search;

import com.flair.bi.service.search.deserializers.DeserializedSearchResult;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class SearchResult {
    private final SearchQLResult searchQLResult;
    private final DeserializedSearchResult deserializedSearchResult;
}
