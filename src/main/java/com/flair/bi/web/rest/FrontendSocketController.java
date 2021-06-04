package com.flair.bi.web.rest;

import com.flair.bi.service.search.SearchItemSelectedResult;
import com.flair.bi.service.search.SearchResult;
import com.flair.bi.service.search.SearchService;
import com.flair.bi.service.search.deserializers.DeserializedSearchResult;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
@Slf4j
public class FrontendSocketController {

	private final SearchService searchService;

	@PreAuthorize("@accessControlManager.hasAccess(#viewId, 'READ', 'VIEW')")
	@MessageMapping("/view/{viewId}/search")
	@SendToUser("/exchange/search")
	public SearchResponse search(
					   @DestinationVariable Long viewId,
					   @Payload SearchRequest request,
					   SimpMessageHeaderAccessor headerAccessor
	) throws InterruptedException {
		log.info("Search API called for view {}", viewId);

		SearchResult results = searchService.search(viewId, request.getText(), headerAccessor.getUser().getName());

		List<SearchResponse.Item> items = results.getSearchQLResult().getItems()
				.stream()
				.map(item -> new SearchResponse.Item(item.getText()))
				.collect(Collectors.toList());

		return new SearchResponse(items, results.getDeserializedSearchResult());
	}

	@MessageMapping("/view/{viewId}/search-item-selected")
	@SendToUser("/exchange/search-item-selected")
	public SearchItemSelectedResult searchItemSelected(
			@Payload SearchItemSelectedRequest request
	) throws InterruptedException {
		log.info("Search Item Selected API called {}", request);
		return searchService.searchItemSelected(request.getText(), request.getItem());
	}

	@MessageExceptionHandler
	@SendToUser("/exchange/errors")
	public String handleException(Throwable exception) {
		log.info("Search API exception {}", exception.getMessage(), exception);
		return exception.getMessage();
	}

	@Data
	private static class SearchRequest {
		private String text;
	}

	@Data
	private static class SearchItemSelectedRequest {
		private String text;
		private String item;
	}

	@Data
	@RequiredArgsConstructor
	private static class SearchResponse {
		private final List<Item> autoSuggestion;
		private final DeserializedSearchResult searchStruct;

		@Data
		@RequiredArgsConstructor
		private static class Item {
			private final String text;
		}
	}

}
