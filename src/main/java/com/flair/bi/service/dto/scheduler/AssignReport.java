package com.flair.bi.service.dto.scheduler;

public class AssignReport {
	private String channels[];
	private String slackAPIToken;
	private String channelId;
	private String strideAPIToken;
	private String strideCloudId;
	private String strideConversationId;
	private CommunicationList communicationList;

	public AssignReport() {
	}

	public String[] getChannels() {
		return channels;
	}

	public void setChannels(String[] channels) {
		this.channels = channels;
	}

	public String getSlackAPIToken() {
		return slackAPIToken;
	}

	public void setSlackAPIToken(String slackAPIToken) {
		this.slackAPIToken = slackAPIToken;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getStrideAPIToken() {
		return strideAPIToken;
	}

	public void setStrideAPIToken(String strideAPIToken) {
		this.strideAPIToken = strideAPIToken;
	}

	public String getStrideCloudId() {
		return strideCloudId;
	}

	public void setStrideCloudId(String strideCloudId) {
		this.strideCloudId = strideCloudId;
	}

	public String getStrideConversationId() {
		return strideConversationId;
	}

	public void setStrideConversationId(String strideConversationId) {
		this.strideConversationId = strideConversationId;
	}

	public CommunicationList getCommunicationList() {
		return communicationList;
	}

	public void setCommunicationList(CommunicationList communicationList) {
		this.communicationList = communicationList;
	}

	@Override
	public String toString() {
		return "AssignReport [channel=" + channels + ", slack_API_Token=" + slackAPIToken + ", channel_id="
				+ channelId + ", stride_API_Token=" + strideAPIToken + ", stride_cloud_id=" + strideCloudId
				+ ", stride_conversation_id=" + strideConversationId + ", communication_list=" + communicationList
				+ "]";
	}

}
