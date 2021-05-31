package com.flair.bi.service.dto.scheduler;

public class Schedule {
	private String cronExp;
	private String timezone;
	private String startDate;
	private String endDate;

	public Schedule() {
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getCronExp() {
		return cronExp;
	}

	public void setCronExp(String cronExp) {
		this.cronExp = cronExp;
	}

	@Override
	public String toString() {
		return "Schedule [cronExp=" + cronExp + ", timezone=" + timezone + ", startDate=" + startDate
				+ ", endDate=" + endDate + "]";
	}

}
