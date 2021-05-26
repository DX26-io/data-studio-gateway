package com.flair.bi.service.search.manager;

import com.flair.bi.service.search.SearchResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class SearchQLManager {
    private final List<ISearchQLManagerProcessor> processors;

    public SearchResult process(SearchQLManagerInput input) {
        return processors.stream()
                .sorted(Comparator.comparingInt(ISearchQLManagerProcessor::getOrder))
                .map(p -> p.process(input))
                .filter(p -> p.stopProcessing())
                .findFirst()
                .map(p -> p.getSearchResult())
                .orElseGet(() -> new SearchResult(Collections.emptyList()));
    }

}
