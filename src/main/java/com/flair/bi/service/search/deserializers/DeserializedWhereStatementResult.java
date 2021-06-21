package com.flair.bi.service.search.deserializers;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Data
public class DeserializedWhereStatementResult implements IDeserializedStatementResult {
    private final List<DeserializedWhereConditionResult> conditions;
}
