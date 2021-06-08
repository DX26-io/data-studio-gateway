package com.flair.bi.service.search.deserializers;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Data
public class DeserializedWhereConditionResult {
    private final String feature;
    private final String condition;
    private final String statement;
    private final List<String> statements;
}
