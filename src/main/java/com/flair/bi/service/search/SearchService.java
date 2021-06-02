package com.flair.bi.service.search;

import com.flair.bi.compiler.search.SearchQLCompiler;
import com.flair.bi.compiler.search.SearchQuery;
import com.flair.bi.service.search.manager.SearchQLManager;
import com.flair.bi.service.search.manager.SearchQLManagerInput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public SearchItemSelectedResult searchItemSelected(String text, String item) {
        Pattern pattern = Pattern.compile("\\w+");
        Matcher matcher = pattern.matcher(text);
        Optional<MatchResult> match = matcher.results().reduce((first, second) -> second);
        if (match.isPresent() && text.endsWith(match.get().group())) {
            text = text.substring(0, text.length() - match.get().group().length()) + item;
        } else {
            text = text + item;
        }
        log.info("Last expr {}", text);
        return new SearchItemSelectedResult(text);
    }

}
