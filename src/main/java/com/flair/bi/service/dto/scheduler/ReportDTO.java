package com.flair.bi.service.dto.scheduler;

import lombok.Getter;
import lombok.Setter;

public class ReportDTO {

	private String userId;
	private String mailBody;
	private String subject;
	private String reportName;
	private String titleName;
	private String dashboardName;
	private String viewName;
	@Getter
	@Setter
	private Long viewId;
	private String shareLink;
	private String buildUrl;
	private boolean thresholdAlert;
	private String createdDate;

	public ReportDTO() {
	}

	public String getMailBody() {
		return mailBody;
	}

	public void setMailBody(String mailBody) {
		this.mailBody = mailBody;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public String getTitleName() {
		return titleName;
	}

	public void setTitleName(String titleName) {
		this.titleName = titleName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getDashboardName() {
		return dashboardName;
	}

	public void setDashboardName(String dashboardName) {
		this.dashboardName = dashboardName;
	}

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public String getShareLink() {
		return shareLink;
	}

	public void setShareLink(String shareLink) {
		this.shareLink = shareLink;
	}

	public String getBuildUrl() {
		return buildUrl;
	}

	public void setBuildUrl(String buildUrl) {
		this.buildUrl = buildUrl;
	}

	public boolean getThresholdAlert() {
		return thresholdAlert;
	}

	public void setThresholdAlert(boolean thresholdAlert) {
		this.thresholdAlert = thresholdAlert;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("ReportDTO{");
		sb.append("userId='").append(userId).append('\'');
		sb.append(", mailBody='").append(mailBody).append('\'');
		sb.append(", subject='").append(subject).append('\'');
		sb.append(", reportName='").append(reportName).append('\'');
		sb.append(", titleName='").append(titleName).append('\'');
		sb.append(", dashboardName='").append(dashboardName).append('\'');
		sb.append(", viewName='").append(viewName).append('\'');
		sb.append(", viewId=").append(viewId);
		sb.append(", shareLink='").append(shareLink).append('\'');
		sb.append(", buildUrl='").append(buildUrl).append('\'');
		sb.append(", thresholdAlert=").append(thresholdAlert);
		sb.append(", createdDate=").append(createdDate);
		sb.append('}');
		return sb.toString();
	}

}
