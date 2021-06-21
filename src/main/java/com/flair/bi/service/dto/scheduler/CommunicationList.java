package com.flair.bi.service.dto.scheduler;

import java.util.Arrays;

public class CommunicationList {
	emailsDTO emails[];
	Integer teams[];

	public CommunicationList() {
	}

	public emailsDTO[] getEmails() {
		return emails;
	}

	public void setEmails(emailsDTO[] emails) {
		this.emails = emails;
	}

	public Integer[] getTeams() {
		return teams;
	}

	public void setTeams(Integer[] teams) {
		this.teams = teams;
	}

	@Override
	public String toString() {
		return "CommunicationList [emails=" + Arrays.toString(emails) + ", teams=" + Arrays.toString(teams) + "]";
	}

}