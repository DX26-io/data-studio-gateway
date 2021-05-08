package com.flair.bi.service.search;

import com.flair.bi.domain.value.QValue;
import com.flair.bi.domain.value.Value;
import com.flair.bi.repository.ValueRepository;
import com.flair.bi.service.FeatureService;
import com.flair.bi.service.properttype.PropertyTypeService;
import com.flair.bi.view.ViewService;
import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

    private final PropertyTypeService propertyTypeService;
    private final ViewService viewService;
    private final ValueRepository valueRepository;
    private final FeatureService featureService;

    public SearchResult search(Long viewId, String text) {
        Iterable<Value> values = propertyTypeService.findByName("Aggregation type")
                .map(pt -> valueRepository.findAll(QValue.value.selectPropertyType.id.eq(pt.getId())))
                .orElseThrow();
        List<Value> list = ImmutableList.copyOf(values);

        List<String> aggregationNames = list.stream()
                .map(l -> l.getValue().toString())
                .collect(Collectors.toList());

        List<SearchResult.Item> searchItems = aggregationNames.stream()
                .map(v -> new SearchResult.Item(v))
                .collect(Collectors.toList());

        return new SearchResult(searchItems);
    }

}
