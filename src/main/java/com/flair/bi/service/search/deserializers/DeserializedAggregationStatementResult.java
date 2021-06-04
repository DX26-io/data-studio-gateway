package com.flair.bi.service.search.deserializers;


import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class DeserializedAggregationStatementResult {
    private final String func;
    private final String feature;
}
