package com.flair.bi.service.search.manager;

import com.flair.bi.compiler.search.AggregationStatementResult;
import com.flair.bi.domain.Feature;
import com.flair.bi.domain.QFeature;
import com.flair.bi.domain.View;
import com.flair.bi.domain.enumeration.FeatureType;
import com.flair.bi.domain.value.QValue;
import com.flair.bi.domain.value.Value;
import com.flair.bi.repository.ValueRepository;
import com.flair.bi.service.FeatureService;
import com.flair.bi.service.properttype.PropertyTypeService;
import com.flair.bi.service.search.SearchQLResult;
import com.flair.bi.view.ViewService;
import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SearchQLFinder {

    private final PropertyTypeService propertyTypeService;
    private final ViewService viewService;
    private final ValueRepository valueRepository;
    private final FeatureService featureService;

    public SearchQLResult getAggregationFeatures(Long viewId, String featureName) {
        return getAggregationFeatures(viewId, featureName, null);
    }

    public SearchQLResult getAggregationFeatures(Long viewId, String featureName, FeatureType featureType) {
        View view = viewService.findOne(viewId);
        Long datasourceId = view.getViewDashboard().getDashboardDatasource().getId();
        List<Feature> features = featureService.getFeatures(QFeature.feature.datasource.id.eq(datasourceId));

        List<SearchQLResult.Item> searchItems = features.stream()
                .filter(f -> featureType == null || f.getFeatureType() == featureType)
                .map(v -> new SearchQLResult.Item(v.getName()))
                .filter(v -> listOnFilter(v, featureName))
                .collect(Collectors.toList());

        return new SearchQLResult(searchItems);
    }

    public SearchQLResult getAggregationFunctions(AggregationStatementResult aggregationStatementResult) {
        Iterable<Value> values = propertyTypeService.findByName("Aggregation type")
                .map(pt -> valueRepository.findAll(QValue.value.selectPropertyType.id.eq(pt.getId())))
                .orElseThrow();
        List<Value> list = ImmutableList.copyOf(values);

        List<SearchQLResult.Item> searchItems = list.stream()
                .map(l -> l.getValue().toString())
                .map(v -> new SearchQLResult.Item(v))
                .filter(v -> listOnFilter(v, Optional.ofNullable(aggregationStatementResult).map(asr -> asr.getFunction()).orElse(null)))
                .collect(Collectors.toList());

        return new SearchQLResult(searchItems);
    }

    public boolean listOnFilter(SearchQLResult.Item v, String searchText) {
        if (StringUtils.isNotEmpty(searchText)) {
            return v.getText().toUpperCase().contains(searchText.toUpperCase());
        }
        return true;
    }

    public boolean listOnFilter(String item, String searchText) {
        if (StringUtils.isNotEmpty(searchText)) {
            return item.toUpperCase().contains(searchText.toUpperCase());
        }
        return true;
    }
}
