package com.flair.bi.service.search.manager;

import com.flair.bi.compiler.search.ByStatementResult;
import com.flair.bi.domain.enumeration.FeatureType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class ByStatementSearchProcessor implements ISearchQLManagerProcessor {

    private final SearchQLFinder searchQLFinder;

    @Override
    public SearchQLManagerProcessorResult process(SearchQLManagerInput input) {
        Optional<ByStatementResult> byStatementResult = input.getCompiledQuery().asByStatementResult();
        if (byStatementResult.isPresent()) {
            String featureName;
            if (byStatementResult.get().getState() == ByStatementResult.State.EXPRESSION) {
                featureName = null;
            } else {
                featureName = byStatementResult.get().lastFeature().orElse(null);
            }
            return SearchQLManagerProcessorResult.of(searchQLFinder.getAggregationFeatures(input.getViewId(), featureName, FeatureType.DIMENSION));
        }

        return SearchQLManagerProcessorResult.empty();
    }

    @Override
    public int getOrder() {
        return 9;
    }
}
