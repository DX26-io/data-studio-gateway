package com.flair.bi.service.search.deserializers;

import com.flair.bi.compiler.search.AggregationStatementsResult;
import com.flair.bi.compiler.search.ByStatementResult;
import com.flair.bi.compiler.search.IStatementResult;
import com.flair.bi.compiler.search.OrderByStatementResult;
import com.flair.bi.compiler.search.SearchResult;
import com.flair.bi.compiler.search.WhereStatementResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchQLDeserializer {
    public DeserializedSearchResult deserialize(SearchResult compile) {
        DeserializedSearchResult deserializedSearchResult = new DeserializedSearchResult();

        compile.asAggregationStatementsResult().ifPresent(ag -> parseResult(ag, deserializedSearchResult));
        compile.asByStatementResult().ifPresent(ag -> parseResult(ag, deserializedSearchResult));
        compile.asWhereStatementResult().ifPresent(ag -> parseResult(ag, deserializedSearchResult));
        compile.asOrderByStatementResult().ifPresent(ag -> parseResult(ag, deserializedSearchResult));

        return deserializedSearchResult;
    }

    private void parseResult(IStatementResult result, DeserializedSearchResult deserializedSearchResult) {
        if (result instanceof AggregationStatementsResult) {
            deserializedSearchResult.setAggregation(deserializeAggregationStatementsResult((AggregationStatementsResult) result));
        } else if (result instanceof ByStatementResult) {
            deserializedSearchResult.setBy(deserializeByStatementResult((ByStatementResult) result));
        } else if (result instanceof WhereStatementResult) {
            deserializedSearchResult.setWhere(deserializeWhereStatementResult((WhereStatementResult) result));
        } else if (result instanceof OrderByStatementResult) {
            deserializedSearchResult.setOrderBy(deserializeOrderByStatementResult((OrderByStatementResult) result));
        } else {
            throw new RuntimeException("unknown result " + result);
        }
    }

    private DeserializedOrderByStatementResult deserializeOrderByStatementResult(OrderByStatementResult result) {
        return new DeserializedOrderByStatementResult(result.getFeature(), result.getDirection());
    }

    private DeserializedWhereStatementResult deserializeWhereStatementResult(WhereStatementResult result) {
        return new DeserializedWhereStatementResult(
                result.getConditions()
                        .stream()
                        .map(c -> new DeserializedWhereConditionResult(c.getFeature(), c.getCondition(), c.getStatement(), c.getStatements()))
                        .collect(Collectors.toList())
        );
    }

    private DeserializedByStatementResult deserializeByStatementResult(ByStatementResult result) {
        return new DeserializedByStatementResult(result.getFeature());
    }

    private DeserializedAggregationStatementsResult deserializeAggregationStatementsResult(AggregationStatementsResult result) {
        return new DeserializedAggregationStatementsResult(
                result.getStatements()
                        .stream()
                        .map(asr -> new DeserializedAggregationStatementResult(asr.getFunction(), asr.getFeature()))
                        .collect(Collectors.toList())
        );
    }
}
