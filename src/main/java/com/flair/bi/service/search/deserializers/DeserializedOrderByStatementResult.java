package com.flair.bi.service.search.deserializers;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class DeserializedOrderByStatementResult implements IDeserializedStatementResult {

    private final String feature;
    private final String direction;

}
