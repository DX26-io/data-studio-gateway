package com.flair.bi.service.search;

import com.flair.bi.compiler.search.SearchQLCompiler;
import com.flair.bi.compiler.search.SearchQuery;
import com.flair.bi.service.search.deserializers.DeserializedSearchResult;
import com.flair.bi.service.search.deserializers.SearchQLDeserializer;
import com.flair.bi.service.search.manager.SearchQLManager;
import com.flair.bi.service.search.manager.SearchQLManagerInput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

    private final SearchQLManager searchQLManager;
    private final SearchQLDeserializer searchQLDeserializer;
    private final SearchQLCompiler searchQLCompiler;
    private static final Pattern PATTERN = Pattern.compile("\\w+");

    public SearchResult search(Long viewId, String text, String actorId) {
        com.flair.bi.compiler.search.SearchResult compile = searchQLCompiler.compile(new SearchQuery(text));

        SearchQLManagerInput build = SearchQLManagerInput.builder()
                .viewId(viewId)
                .compiledQuery(compile)
                .actorId(actorId)
                .build();

        DeserializedSearchResult deserializedSearchResult = searchQLDeserializer.deserialize(compile);

        SearchQLResult searchQLResult = searchQLManager.process(build);

        return new SearchResult(text, searchQLResult, deserializedSearchResult);
    }

    public SearchItemSelectedResult searchItemSelected(String text, String item, Integer cursor) {
        Matcher matcher = PATTERN.matcher(text);

        MatchResult selectedResult = matcher.results()
                .filter(result -> cursor >= result.start() && cursor <= result.end())
                .findFirst()
                .orElse(null);

        String result;
        if (selectedResult == null) {
            result = text + item;
        } else {
            result = text.substring(0, selectedResult.start()) + item + text.substring(selectedResult.end());
        }

        log.info("Last expr {}", result);
        return new SearchItemSelectedResult(result);
    }

}
