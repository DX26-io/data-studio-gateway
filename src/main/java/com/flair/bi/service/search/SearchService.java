package com.flair.bi.service.search;

import com.flair.bi.compiler.search.SearchQLCompiler;
import com.flair.bi.compiler.search.SearchQuery;
import com.flair.bi.service.search.manager.SearchQLManager;
import com.flair.bi.service.search.manager.SearchQLManagerInput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

    private final SearchQLManager searchQLManager;
    private final SearchQLCompiler searchQLCompiler;

    public SearchResult search(Long viewId, String text, String actorId) {
        com.flair.bi.compiler.search.SearchResult compile = searchQLCompiler.compile(new SearchQuery(text));

        SearchQLManagerInput build = SearchQLManagerInput.builder()
                .viewId(viewId)
                .compiledQuery(compile)
                .actorId(actorId)
                .build();

        return searchQLManager.process(build);
    }



}
