package com.flair.bi.service.search.manager;

import com.flair.bi.compiler.search.AggregationStatementResult;
import com.flair.bi.compiler.search.AggregationStatementsResult;
import com.flair.bi.domain.enumeration.FeatureType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class AggregationStatementSearchProcessor implements ISearchQLManagerProcessor {

    private final SearchQLFinder searchQLFinder;

    @Override
    public SearchQLManagerProcessorResult process(SearchQLManagerInput input) {
        Optional<AggregationStatementsResult> aggregationStatementsResult = input.getCompiledQuery().asAggregationStatementsResult();
        if (aggregationStatementsResult.isPresent()) {
            if (aggregationStatementsResult.get().getState() == AggregationStatementsResult.State.EXPRESSION) {
                Optional<AggregationStatementResult> lastStatement = aggregationStatementsResult.get().lastStatement();
                if (lastStatement.isPresent()) {
                    if (lastStatement.get().getState() == AggregationStatementResult.State.FUNCTION) {
                        return SearchQLManagerProcessorResult.of(searchQLFinder.getAggregationFunctions(lastStatement.get()));
                    } else if (lastStatement.get().getState() == AggregationStatementResult.State.FEATURE) {
                        return SearchQLManagerProcessorResult.of(searchQLFinder.getAggregationFeatures(input.getViewId(), lastStatement.get().getFeature(), FeatureType.MEASURE));
                    }
                }
                return SearchQLManagerProcessorResult.of(searchQLFinder.getAggregationFunctions(null));
            }
            return SearchQLManagerProcessorResult.ofEmpty();
        } else {
            return SearchQLManagerProcessorResult.of(searchQLFinder.getAggregationFunctions(null));
        }
    }

    @Override
    public int getOrder() {
        return 10;
    }
}
