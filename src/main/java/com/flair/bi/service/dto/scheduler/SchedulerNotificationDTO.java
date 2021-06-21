package com.flair.bi.service.dto.scheduler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SchedulerNotificationDTO {

	private ReportDTO report;
	private ReportLineItem reportLineItem;
	private AssignReport assignReport;
	private Schedule schedule;
	private String query;
	private String constraints;
}
