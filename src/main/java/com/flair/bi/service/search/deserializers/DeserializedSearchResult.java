package com.flair.bi.service.search.deserializers;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class DeserializedSearchResult {

    private DeserializedAggregationStatementsResult aggregation;
    private DeserializedByStatementResult by;
    private DeserializedWhereStatementResult where;
    private DeserializedOrderByStatementResult orderBy;

}
