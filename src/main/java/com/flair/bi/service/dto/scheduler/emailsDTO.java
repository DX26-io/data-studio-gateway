package com.flair.bi.service.dto.scheduler;

public class emailsDTO {

	private String userEmail;
	private String userName;

	public emailsDTO() {
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public String toString() {
		return "emailsDTO [userEmail=" + userEmail + ", userName=" + userName + "]";
	}

}
