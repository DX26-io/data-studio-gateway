package com.flair.bi.service.search.manager;

import com.flair.bi.compiler.search.WhereConditionResult;
import com.flair.bi.compiler.search.WhereStatementResult;
import com.flair.bi.domain.View;
import com.flair.bi.domain.enumeration.FeatureType;
import com.flair.bi.messages.QueryResponse;
import com.flair.bi.service.GrpcQueryService;
import com.flair.bi.service.SendGetDataDTO;
import com.flair.bi.view.ViewService;
import com.project.bi.query.dto.FieldDTO;
import com.project.bi.query.dto.QueryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class WhereStatementSearchProcessor implements ISearchQLManagerProcessor {

    private final SearchQLFinder searchQLFinder;
    private final GrpcQueryService grpcQueryService;
    private final ViewService viewService;

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
                        View view = viewService.findOne(input.getViewId());
                        Long datasourceId = view.getViewDashboard().getDashboardDatasource().getId();
                        QueryDTO queryDTO = new QueryDTO();
                        FieldDTO fieldDTO = new FieldDTO();
                        fieldDTO.setName(lastStatement.get().getFeature());
                        queryDTO.setFields(Arrays.asList(fieldDTO));
                        queryDTO.setLimit(100L);
                        queryDTO.setDistinct(true);
                        QueryResponse queryResponse = grpcQueryService.getDataStream(SendGetDataDTO.builder()
                                .datasourcesId(datasourceId)
                                .viewId(input.getViewId())
                                .userId(input.getActorId())
                                .type("filters")
                                .queryDTO(queryDTO)
                                .build());
                        return SearchQLManagerProcessorResult.of(searchQLFinder.getAggregationFeatures(input.getViewId(), lastStatement.get().getFeature(), FeatureType.MEASURE));
                    }
                }
                return SearchQLManagerProcessorResult.of(searchQLFinder.getAggregationFeatures(input.getViewId(), null));
            }
        }

        return SearchQLManagerProcessorResult.empty();
    }

    @Override
    public int getOrder() {
        return 8;
    }
}
