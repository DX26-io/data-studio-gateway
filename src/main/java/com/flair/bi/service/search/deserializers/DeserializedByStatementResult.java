package com.flair.bi.service.search.deserializers;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Data
public class DeserializedByStatementResult implements IDeserializedStatementResult {

    private final List<String> features;

}
