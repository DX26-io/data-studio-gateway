package com.flair.bi.service.search;

import com.flair.bi.compiler.search.AggregationStatementResult;
import com.flair.bi.compiler.search.AggregationStatementsResult;
import com.flair.bi.compiler.search.ByStatementResult;
import com.flair.bi.compiler.search.SearchQLCompiler;
import com.flair.bi.compiler.search.SearchQuery;
import com.flair.bi.domain.Feature;
import com.flair.bi.domain.QFeature;
import com.flair.bi.domain.View;
import com.flair.bi.domain.enumeration.FeatureType;
import com.flair.bi.domain.value.QValue;
import com.flair.bi.domain.value.Value;
import com.flair.bi.repository.ValueRepository;
import com.flair.bi.service.FeatureService;
import com.flair.bi.service.properttype.PropertyTypeService;
import com.flair.bi.view.ViewService;
import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

    private final PropertyTypeService propertyTypeService;
    private final ViewService viewService;
    private final ValueRepository valueRepository;
    private final FeatureService featureService;
    private final SearchQLCompiler searchQLCompiler = new SearchQLCompiler();

    public SearchResult search(Long viewId, String text) {
        com.flair.bi.compiler.search.SearchResult compile = searchQLCompiler.compile(new SearchQuery(text));

        Optional<ByStatementResult> byStatementResult = compile.asByStatementResult();
        if (byStatementResult.isPresent()) {
            String featureName;
            if (byStatementResult.get().getState() == ByStatementResult.State.EXPRESSION) {
                featureName = null;
            } else {
                featureName = byStatementResult.get().lastFeature().orElse(null);
            }
            return getAggregationFeatures(viewId, featureName, FeatureType.DIMENSION);
        }

        Optional<AggregationStatementsResult> aggregationStatementsResult = compile.asAggregationStatementsResult();
        if (aggregationStatementsResult.isPresent()) {
            if (aggregationStatementsResult.get().getState() == AggregationStatementsResult.State.EXPRESSION) {
                Optional<AggregationStatementResult> lastStatement = aggregationStatementsResult.get().lastStatement();
                if (lastStatement.isPresent()) {
                    if (lastStatement.get().getState() == AggregationStatementResult.State.FUNCTION) {
                        return getAggregationFunctions(lastStatement.get());
                    } else if (lastStatement.get().getState() == AggregationStatementResult.State.FEATURE) {
                        return getAggregationFeatures(viewId, lastStatement.get().getFeature(), FeatureType.MEASURE);
                    }
                }
                return getAggregationFunctions(null);
            }
        } else {
            return getAggregationFunctions(null);
        }

        return new SearchResult(Collections.emptyList());
    }

    private SearchResult getAggregationFeatures(Long viewId, String featureName, FeatureType featureType) {
        View view = viewService.findOne(viewId);
        Long datasourceId = view.getViewDashboard().getDashboardDatasource().getId();
        List<Feature> features = featureService.getFeatures(QFeature.feature.datasource.id.eq(datasourceId));

        List<SearchResult.Item> searchItems = features.stream()
                .filter(f -> f.getFeatureType() == featureType)
                .map(v -> new SearchResult.Item(v.getName()))
                .filter(v -> listOnFilter(v, featureName))
                .collect(Collectors.toList());

        return new SearchResult(searchItems);
    }

    private SearchResult getAggregationFunctions(AggregationStatementResult aggregationStatementResult) {
        Iterable<Value> values = propertyTypeService.findByName("Aggregation type")
                .map(pt -> valueRepository.findAll(QValue.value.selectPropertyType.id.eq(pt.getId())))
                .orElseThrow();
        List<Value> list = ImmutableList.copyOf(values);

        List<String> aggregationNames = list.stream()
                .map(l -> l.getValue().toString())
                .collect(Collectors.toList());

        List<SearchResult.Item> searchItems = aggregationNames.stream()
                .map(v -> new SearchResult.Item(v))
                .filter(v -> listOnFilter(v, Optional.ofNullable(aggregationStatementResult).map(asr -> asr.getFunction()).orElse(null)))
                .collect(Collectors.toList());

        return new SearchResult(searchItems);
    }

    private boolean listOnFilter(SearchResult.Item v, String searchText) {
        if (StringUtils.isNotEmpty(searchText)) {
            return v.getText().toUpperCase().contains(searchText.toUpperCase());
        }
        return true;
    }

}
