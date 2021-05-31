package com.flair.bi.service.dto.scheduler;

import com.project.bi.query.dto.QueryDTO;
import lombok.Getter;
import lombok.Setter;

public class SchedulerDTO {

	private long datasourceId;
	private ReportDTO report;
	private ReportLineItem reportLineItem;
	private AssignReport assignReport;
	private Schedule schedule;
	@Getter
	@Setter
	private String constraints;
	private QueryDTO queryDTO;
	private boolean putCall;
	private boolean emailReporter;
	private Long dashboardId;

	public SchedulerDTO() {
	}

	public SchedulerDTO(long datasourceId, ReportDTO report, ReportLineItem reportLineItem,
			AssignReport assignReport, Schedule schedule, QueryDTO queryDTO, boolean putCall, boolean emailReporter,
			String constraints) {
		super();
		this.datasourceId = datasourceId;
		this.report = report;
		this.reportLineItem = reportLineItem;
		this.assignReport = assignReport;
		this.constraints = constraints;
		this.schedule = schedule;
		this.queryDTO = queryDTO;
		this.putCall = putCall;
		this.emailReporter = emailReporter;
	}

	public ReportDTO getReport() {
		return report;
	}

	public void setReport(ReportDTO report) {
		this.report = report;
	}

	public ReportLineItem getReportLineItem() {
		return reportLineItem;
	}

	public void setReportLineItem(ReportLineItem reportLineItem) {
		this.reportLineItem = reportLineItem;
	}

	public AssignReport getAssignReport() {
		return assignReport;
	}

	public void setAssignReport(AssignReport assignReport) {
		this.assignReport = assignReport;
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	public long getDatasourceId() {
		return datasourceId;
	}

	public void setDatasourceId(long datasourceId) {
		this.datasourceId = datasourceId;
	}

	public QueryDTO getQueryDTO() {
		return queryDTO;
	}

	public void setQueryDTO(QueryDTO queryDTO) {
		this.queryDTO = queryDTO;
	}

	public boolean getPutCall() {
		return putCall;
	}

	public void setPutCall(boolean putCall) {
		this.putCall = putCall;
	}

	public boolean getEmailReporter() {
		return emailReporter;
	}

	@Override
	public String toString() {
		return "SchedulerDTO [datasourceId=" + datasourceId + ", report=" + report + ", reportLineItem="
				+ reportLineItem + ", assignReport=" + assignReport + ", schedule=" + schedule + ", queryDTO="
				+ queryDTO + ", putCall=" + putCall + ", emailReporter=" + emailReporter + ", dashboardId=" + dashboardId + "]";
	}

	public Long getDashboardId() {
		return dashboardId;
	}

	public void setDashboardId(Long dashboardId) {
		this.dashboardId = dashboardId;
	}
}
