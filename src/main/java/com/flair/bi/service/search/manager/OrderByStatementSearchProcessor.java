package com.flair.bi.service.search.manager;

import com.flair.bi.compiler.search.OrderByStatementResult;
import com.flair.bi.service.search.SearchQLResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class OrderByStatementSearchProcessor implements ISearchQLManagerProcessor {

    private static final List<String> DIRECTIONS = Arrays.asList("ASC", "DESC");
    private final SearchQLFinder searchQLFinder;

    @Override
    public SearchQLManagerProcessorResult process(SearchQLManagerInput input) {
        Optional<OrderByStatementResult> byStatementResult = input.getCompiledQuery().asOrderByStatementResult();
        if (byStatementResult.isPresent()) {
            if (byStatementResult.get().getState() == OrderByStatementResult.State.FEATURE) {
                return SearchQLManagerProcessorResult.of(searchQLFinder.getAggregationFeatures(input.getViewId(), byStatementResult.get().getFeature()));
            } else if (byStatementResult.get().getState() == OrderByStatementResult.State.DIRECTION) {
                return searchDirectionValues();
            }
        }

        return SearchQLManagerProcessorResult.skip();
    }

    private SearchQLManagerProcessorResult searchDirectionValues() {
        return SearchQLManagerProcessorResult.of(new SearchQLResult(
                DIRECTIONS.stream().map(a -> new SearchQLResult.Item(a)).collect(Collectors.toList())
        ));
    }

    @Override
    public int getOrder() {
        return 7;
    }
}
