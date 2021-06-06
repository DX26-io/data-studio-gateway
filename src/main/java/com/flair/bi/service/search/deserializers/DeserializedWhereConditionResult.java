package com.flair.bi.service.search.deserializers;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class DeserializedWhereConditionResult {
    private final String feature;
    private final String condition;
    private final String statement;
}
