package com.flair.bi.service.search.manager;

public interface ISearchQLManagerProcessor {
    SearchQLManagerProcessorResult process(SearchQLManagerInput input);

    int getOrder();
}
