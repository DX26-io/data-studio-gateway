package com.flair.bi.service.search.manager;

import com.flair.bi.compiler.search.WhereConditionResult;
import com.flair.bi.compiler.search.WhereStatementResult;
import com.flair.bi.domain.enumeration.FeatureType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class WhereStatementSearchProcessor implements ISearchQLManagerProcessor {

    private final SearchQLFinder searchQLFinder;

    @Override
    public SearchQLManagerProcessorResult process(SearchQLManagerInput input) {
        Optional<WhereStatementResult> whereStatementResult = input.getCompiledQuery().asWhereStatementResult();
        if (whereStatementResult.isPresent()) {
            if (whereStatementResult.get().getState() == WhereStatementResult.State.EXPRESSION) {
                Optional<WhereConditionResult> lastStatement = whereStatementResult.get().lastStatement();
                if (lastStatement.isPresent()) {
                    if (lastStatement.get().getState() == WhereConditionResult.State.FEATURE) {
                        return SearchQLManagerProcessorResult.of(searchQLFinder.getAggregationFeatures(input.getViewId(), lastStatement.get().getFeature()));
                    } else if (lastStatement.get().getState() == WhereConditionResult.State.STATEMENT) {
                        return SearchQLManagerProcessorResult.of(searchQLFinder.getAggregationFeatures(input.getViewId(), lastStatement.get().getFeature(), FeatureType.MEASURE));
                    }
                }
                return SearchQLManagerProcessorResult.of(searchQLFinder.getAggregationFunctions(null));
            }
        } else {
            return SearchQLManagerProcessorResult.of(searchQLFinder.getAggregationFunctions(null));
        }

        return SearchQLManagerProcessorResult.empty();
    }

    @Override
    public int getOrder() {
        return 9;
    }
}
