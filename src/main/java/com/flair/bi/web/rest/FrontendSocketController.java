package com.flair.bi.web.rest;

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
import java.util.stream.Stream;

@RequiredArgsConstructor
@Controller
@Slf4j
public class FrontendSocketController {

	@PreAuthorize("@accessControlManager.hasAccess(#viewId, 'READ', 'VIEW')")
	@MessageMapping("/view/{viewId}/search")
	@SendToUser("/exchange/search")
	public SearchResponse search(
					   @DestinationVariable Long viewId,
					   @Payload SearchRequest request,
					   SimpMessageHeaderAccessor headerAccessor
	) throws InterruptedException {
		log.info("Search API called for view {}", viewId);

		List<SearchResponse.Item> items = Stream.of(request.getText(), "hello")
				.map(item -> new SearchResponse.Item(item))
				.collect(Collectors.toList());

		return new SearchResponse(items);
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
	@RequiredArgsConstructor
	private static class SearchResponse {
		private final List<Item> autoSuggestion;

		@Data
		@RequiredArgsConstructor
		private static class Item {
			private final String text;
		}
	}

}
