package com.flair.bi.service.search;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class SearchResult {

    private final List<Item> items;

    @Data
    @RequiredArgsConstructor
    public static class Item {
        private final String text;
    }
}
