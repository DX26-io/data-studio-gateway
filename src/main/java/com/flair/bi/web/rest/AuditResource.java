package com.flair.bi.web.rest;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.flair.bi.service.AuditEventService;
import com.flair.bi.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;

/**
 * REST controller for getting the audit events.
 */
@RestController
@RequestMapping("/management/audits")
@RequiredArgsConstructor
public class AuditResource {

	private final AuditEventService auditEventService;

	/**
	 * GET /audits : get a page of AuditEvents.
	 *
	 * @param pageable the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of AuditEvents
	 *         in body
	 * @throws URISyntaxException if there is an error to generate the pagination
	 *                            HTTP headers
	 */
	@GetMapping
	public ResponseEntity<List<AuditEvent>> getAll(@ApiParam Pageable pageable) throws URISyntaxException {
		Page<AuditEvent> page = auditEventService.findAll(pageable);
		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/management/audits");
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}

	/**
	 * GET /audits : get a page of AuditEvents between the fromDate and toDate.
	 *
	 * @param fromDate  the start of the time period of AuditEvents to get
	 * @param toDate    the end of the time period of AuditEvents to get
	 * @param pageable  the pagination information
	 * @param principal principal
	 * @return the ResponseEntity with status 200 (OK) and the list of AuditEvents
	 *         in body
	 * @throws URISyntaxException if there is an error to generate the pagination
	 *                            HTTP headers
	 */

	@GetMapping(params = { "fromDate", "toDate" })
	public ResponseEntity<List<AuditEvent>> getByDates(@RequestParam(value = "fromDate") LocalDate fromDate,
			@RequestParam(value = "toDate") LocalDate toDate,
			@RequestParam(value = "principal", required = false) String principal, @ApiParam Pageable pageable)
			throws URISyntaxException {
		Page<AuditEvent> page = null;
		if (principal != null) {
			page = auditEventService.findByDatesAndPrincipal(fromDate.atTime(0, 0), toDate.atTime(23, 59), principal,
					pageable);
		} else {
			page = auditEventService.findByDates(fromDate.atTime(0, 0), toDate.atTime(23, 59), pageable);
		}
		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/management/audits");
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}

	/**
	 * GET /audits/:id : get an AuditEvent by id.
	 *
	 * @param id the id of the entity to get
	 * @return the ResponseEntity with status 200 (OK) and the AuditEvent in body,
	 *         or status 404 (Not Found)
	 */
	@GetMapping("/{id:.+}")
	public ResponseEntity<AuditEvent> get(@PathVariable Long id) {
		return auditEventService.find(id).map((entity) -> new ResponseEntity<>(entity, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

}
