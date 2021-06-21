package com.flair.bi.service;

import com.flair.bi.messages.report.AddEmailConfigsRequest;
import com.flair.bi.messages.report.AddTeamConfigsRequest;
import com.flair.bi.messages.report.ChannelParameters;
import com.flair.bi.messages.report.ConfigsResponse;
import com.flair.bi.messages.report.ConnectionProperties;
import com.flair.bi.messages.report.CreateJiraTicketRequest;
import com.flair.bi.messages.report.CreateJiraTicketResponse;
import com.flair.bi.messages.report.DeleteChannelConfigRequest;
import com.flair.bi.messages.report.DeleteScheduledReportRequest;
import com.flair.bi.messages.report.DisableTicketCreationRequest;
import com.flair.bi.messages.report.Email;
import com.flair.bi.messages.report.EmailParameters;
import com.flair.bi.messages.report.ExecuteReportRequest;
import com.flair.bi.messages.report.ExecuteReportResponse;
import com.flair.bi.messages.report.GetAllJiraRequest;
import com.flair.bi.messages.report.GetAllJiraResponse;
import com.flair.bi.messages.report.GetChannelPropertiesRequest;
import com.flair.bi.messages.report.GetChannelPropertiesResponse;
import com.flair.bi.messages.report.GetEmailConfigRequest;
import com.flair.bi.messages.report.GetEmailConfigResponse;
import com.flair.bi.messages.report.GetJiraConfigRequest;
import com.flair.bi.messages.report.GetJiraConfigResponse;
import com.flair.bi.messages.report.GetScheduleReportLogRequest;
import com.flair.bi.messages.report.GetScheduleReportLogResponse;
import com.flair.bi.messages.report.GetScheduleReportLogsRequest;
import com.flair.bi.messages.report.GetScheduleReportLogsResponse;
import com.flair.bi.messages.report.GetScheduledReportRequest;
import com.flair.bi.messages.report.GetTeamConfigRequest;
import com.flair.bi.messages.report.GetTeamConfigResponse;
import com.flair.bi.messages.report.GetTeamNamesResponse;
import com.flair.bi.messages.report.IsConfigExistRequest;
import com.flair.bi.messages.report.IsConfigExistResponse;
import com.flair.bi.messages.report.JiraConfigsRequest;
import com.flair.bi.messages.report.JiraParameters;
import com.flair.bi.messages.report.JiraTickets;
import com.flair.bi.messages.report.NotifyOpenedJiraTicketRequest;
import com.flair.bi.messages.report.OpenJiraTicket;
import com.flair.bi.messages.report.RepUserCountReq;
import com.flair.bi.messages.report.RepUserCountResp;
import com.flair.bi.messages.report.RepUserReq;
import com.flair.bi.messages.report.RepUserResp;
import com.flair.bi.messages.report.Report;
import com.flair.bi.messages.report.ReportLog;
import com.flair.bi.messages.report.ReportServiceGrpc;
import com.flair.bi.messages.report.ScheduleReport;
import com.flair.bi.messages.report.ScheduleReportRequest;
import com.flair.bi.messages.report.ScheduleReportResponse;
import com.flair.bi.messages.report.SearchReportsRequest;
import com.flair.bi.messages.report.SearchReportsResponse;
import com.flair.bi.messages.report.TeamConfigParameters;
import com.flair.bi.messages.report.UpdateEmailSMTPRequest;
import com.flair.bi.messages.report.UpdateTeamWebhookURLRequest;
import com.flair.bi.service.dto.scheduler.ApiErrorDTO;
import com.flair.bi.service.dto.scheduler.AssignReport;
import com.flair.bi.service.dto.scheduler.ChannelParametersDTO;
import com.flair.bi.service.dto.scheduler.CommunicationList;
import com.flair.bi.service.dto.scheduler.ConnectionPropertiesDTO;
import com.flair.bi.service.dto.scheduler.EmailConfigParametersDTO;
import com.flair.bi.service.dto.scheduler.GetChannelConnectionDTO;
import com.flair.bi.service.dto.scheduler.GetJiraTicketResponseDTO;
import com.flair.bi.service.dto.scheduler.GetJiraTicketsDTO;
import com.flair.bi.service.dto.scheduler.GetSchedulerReportDTO;
import com.flair.bi.service.dto.scheduler.GetSchedulerReportLogDTO;
import com.flair.bi.service.dto.scheduler.GetSchedulerReportLogsDTO;
import com.flair.bi.service.dto.scheduler.GetSearchReportsDTO;
import com.flair.bi.service.dto.scheduler.JiraParametersDTO;
import com.flair.bi.service.dto.scheduler.JiraTicketsDTO;
import com.flair.bi.service.dto.scheduler.OpenJiraTicketDTO;
import com.flair.bi.service.dto.scheduler.ReportDTO;
import com.flair.bi.service.dto.scheduler.ReportLineItem;
import com.flair.bi.service.dto.scheduler.Schedule;
import com.flair.bi.service.dto.scheduler.SchedulerLogDTO;
import com.flair.bi.service.dto.scheduler.SchedulerNotificationDTO;
import com.flair.bi.service.dto.scheduler.SchedulerReportsDTO;
import com.flair.bi.service.dto.scheduler.TeamConfigParametersDTO;
import com.flair.bi.service.dto.scheduler.emailsDTO;
import com.flair.bi.web.rest.util.QueryGrpcUtils;
import com.flair.bi.websocket.grpc.config.ManagedChannelFactory;
import io.grpc.CallCredentials;
import io.grpc.ManagedChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.flair.bi.web.rest.util.GrpcUtils.orEmpty;
import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@Profile("!test")
@RequiredArgsConstructor
public class NotificationsGrpcService implements INotificationsGrpcService {

    private final ManagedChannelFactory notificationsChannelFactory;
    private final GrpcCredentialsService grpcCredentialsService;
    private volatile ReportServiceGrpc.ReportServiceBlockingStub reportServiceBlockingStub;
    private volatile ManagedChannel channel;

    private ReportServiceGrpc.ReportServiceBlockingStub getReportStub() {
        if (reportServiceBlockingStub == null || (channel != null && channel.isShutdown())) {
            synchronized (this) {
                if (reportServiceBlockingStub == null || (channel != null && channel.isShutdown())) {
                    reportServiceBlockingStub = ReportServiceGrpc.newBlockingStub(getChannel())
                            .withCallCredentials(getCredentials().orElse(null));
                }
            }
        }
        return reportServiceBlockingStub;
    }

    private Optional<CallCredentials> getCredentials() {
        return grpcCredentialsService.getCredentials();
    }

    private ManagedChannel getChannel() {
        if (channel == null || channel.isShutdown()) {
            synchronized (this) {
                if (channel == null || channel.isShutdown()) {
                    channel = notificationsChannelFactory.getInstance();
                }
            }
        }
        return channel;
    }

    @Override
    public GetSchedulerReportDTO getSchedulerReport(String visualizationId) {
        GetScheduledReportRequest request = GetScheduledReportRequest.newBuilder()
                .setVisualizationId(visualizationId)
                .build();
        ScheduleReportResponse response = getReportStub().getScheduledReport(request);
        return createSchedulerReportDto(response);
    }

    @Override
    public GetSchedulerReportDTO createSchedulerReport(SchedulerNotificationDTO schedulerNotificationDTO) {
        ScheduleReportResponse response = getReportStub().scheduleReport(ScheduleReportRequest.newBuilder()
                .setReport(toReportProto(schedulerNotificationDTO))
                .build());
        return createSchedulerReportDto(response);
    }

    @Override
    public GetSchedulerReportDTO updateSchedulerReport(SchedulerNotificationDTO schedulerNotificationDTO) {
        ScheduleReportResponse response = getReportStub().updateScheduledReport(ScheduleReportRequest.newBuilder()
                .setReport(toReportProto(schedulerNotificationDTO))
                .build());
        return createSchedulerReportDto(response);
    }

    @Override
    public SchedulerReportsDTO getScheduledReportsByUser(String username, Integer pageSize, Integer page) {
        RepUserResp response = getReportStub().getAllScheduledReportsByUser(RepUserReq.newBuilder()
                .setUsername(username)
                .setPage(page)
                .setPageSize(pageSize)
                .build());
        List<SchedulerNotificationDTO> dtos = response.getReportsList()
                .stream()
                .map(r -> createSchedulerNotificationDTO(r))
                .collect(toList());

        return SchedulerReportsDTO.builder()
                .message(StringUtils.isEmpty(response.getMessage()) ? null : response.getMessage())
                .reports(dtos)
                .build();
    }

    @Override
    public GetSchedulerReportDTO deleteSchedulerReport(String visualizationId) {
        ScheduleReportResponse response = getReportStub().deleteScheduledReport(DeleteScheduledReportRequest.newBuilder()
                .setVisualizationId(visualizationId)
                .build());
        return createSchedulerReportDto(response);
    }

    @Override
    public Integer getScheduledReportsCount(String username) {
        RepUserCountResp response = getReportStub().getAllScheduledReportsCountsByUser(RepUserCountReq.newBuilder()
                .setUsername(username)
                .build());
        return response.getTotalReports();
    }

    @Override
    public void executeImmediateScheduledReport(String visualizationId) {
        ExecuteReportResponse response = getReportStub().executeReport(ExecuteReportRequest.newBuilder()
                .setVisualizationId(visualizationId)
                .build());
    }

    @Override
    public GetSchedulerReportLogsDTO getScheduleReportLogs(String visualizationid, Integer pageSize, Integer page) {
        GetScheduleReportLogsResponse result = getReportStub().getScheduleReportLogs(
                GetScheduleReportLogsRequest.newBuilder()
                        .setVisualizationId(visualizationid)
                        .setPageSize(pageSize)
                        .setPage(page)
                        .build()
        );
        return GetSchedulerReportLogsDTO.builder()
                .message(StringUtils.isEmpty(result.getMessage()) ? null : result.getMessage())
                .schedulerLogs(toLogs(result.getSchedulerLogsList()))
                .totalRecords(result.getTotalRecords())
                .build();
    }

    @Override
    public GetSearchReportsDTO searchReports(String username, String reportName, String startDate, String endDate, Integer pageSize, Integer page,Boolean thresholdAlert,String dashboardName,String viewName) {
        SearchReportsResponse result = getReportStub().searchReports(
                SearchReportsRequest.newBuilder()
                        .setUsername(username)
                        .setReportName(reportName)
                        .setStartDate(startDate)
                        .setEndDate(endDate)
                        .setPageSize(pageSize)
                        .setPage(page)
                        .setThresholdAlert(thresholdAlert)
                        .setDashboardName(dashboardName)
                        .setViewName(viewName)
                        .build()
        );
        return GetSearchReportsDTO.builder()
                .totalRecords(result.getTotalRecords())
                .reports(toReportsDto(result.getRecordsList()))
                .build();
    }

    @Override
    public GetSchedulerReportLogDTO getReportLogByMetaId(Long taskLogMetaId) {
        GetScheduleReportLogResponse result = getReportStub().getScheduleReportLog(
                GetScheduleReportLogRequest.newBuilder()
                        .setTaskLogMetaId(taskLogMetaId)
                        .build()
        );
        return GetSchedulerReportLogDTO.builder()
                .reportLog(toReportLog(result.getReportLog()))
                .error(toApiError(result.getError()))
                .build();
    }

    private ApiErrorDTO toApiError(com.flair.bi.messages.report.ApiError error) {
        if (StringUtils.isEmpty(error.getMessage())) {
            return null;
        }
        return ApiErrorDTO.builder()
                .message(error.getMessage())
                .build();
    }

    private List<SchedulerNotificationDTO> toReportsDto(List<ScheduleReport> list) {
        return list.stream()
                .map(item -> createSchedulerNotificationDTO(item))
                .collect(toList());
    }

    private List<SchedulerLogDTO> toLogs(List<ReportLog> list) {
        return list.stream()
                .map(item -> toReportLog(item))
                .collect(toList());
    }

    private SchedulerLogDTO toReportLog(ReportLog item) {
        return SchedulerLogDTO.builder()
                .taskExecuted(item.getTaskExecuted())
                .taskStatus(item.getTaskStatus())
                .channel(item.getChannel())
                .comment(item.getComment())
                .dashboardName(item.getDashboardName())
                .descripition(item.getDescripition())
                .notificationSent(item.getNotificationSent())
                .thresholdMet(item.getThresholdMet())
                .schedulerTaskMetaId(item.getSchedulerTaskMetaId())
                .viewData(item.getViewData())
                .viewName(item.getViewName())
                .enableTicketCreation(item.getEnableTicketCreation())
                .isTicketCreated(item.getIsTicketCreated())
                .viewTicket(item.getViewTicket())
                .query(QueryGrpcUtils.mapToQueryDTO(item.getQuery()))
				.visualizationId(item.getVisualizationId())
                .build();
    }

    private GetSchedulerReportDTO createSchedulerReportDto(ScheduleReportResponse response) {
        return GetSchedulerReportDTO.builder()
                .message(StringUtils.isEmpty(response.getMessage()) ? null : response.getMessage())
                .report(createSchedulerNotificationDTO(response))
                .build();
    }

    private ScheduleReport toReportProto(SchedulerNotificationDTO dto) {
        return ScheduleReport.newBuilder()
                .setReport(
                        Report.newBuilder()
                                .setUserId(orEmpty(dto.getReport().getUserId()))
                                .setDashboardName(orEmpty(dto.getReport().getDashboardName()))
                                .setViewName(orEmpty(dto.getReport().getViewName()))
                                .setShareLink(orEmpty(dto.getReport().getShareLink()))
                                .setViewId(orEmpty(String.valueOf(dto.getReport().getViewId())))
                                .setBuildUrl(orEmpty(dto.getReport().getBuildUrl()))
                                .setMailBody(orEmpty(dto.getReport().getMailBody()))
                                .setSubject(orEmpty(dto.getReport().getSubject()))
                                .setReportName(orEmpty(dto.getReport().getReportName()))
                                .setTitleName(orEmpty(dto.getReport().getTitleName()))
                                .setThresholdAlert(dto.getReport().getThresholdAlert())
                                .build()
                )
                .setReportLineItem(
                        com.flair.bi.messages.report.ReportLineItem.newBuilder()
                                .setVisualizationId(orEmpty(dto.getReportLineItem().getVisualizationId()))
                                .addAllDimensions(Arrays.asList(dto.getReportLineItem().getDimensions()))
                                .addAllMeasures(Arrays.asList(dto.getReportLineItem().getMeasures()))
                                .setVisualizationType(orEmpty(dto.getReportLineItem().getVisualizationType()))
                                .build()
                )
				.setAssignReport(com.flair.bi.messages.report.AssignReport.newBuilder()
						.addAllChannels(Arrays.asList(dto.getAssignReport().getChannels()))
						.setSlackAPIToken(orEmpty(dto.getAssignReport().getSlackAPIToken()))
						.setChannelId(orEmpty(dto.getAssignReport().getChannelId()))
						.setStrideAPIToken(orEmpty(dto.getAssignReport().getStrideAPIToken()))
						.setStrideCloudId(orEmpty(dto.getAssignReport().getStrideCloudId()))
						.setStrideConversationId(orEmpty(dto.getAssignReport().getStrideConversationId()))
						.setCommunicationList(com.flair.bi.messages.report.CommunicationList.newBuilder()
								.addAllTeams(Arrays.asList(dto.getAssignReport().getCommunicationList().getTeams()))
								.addAllEmails(Arrays.stream(dto.getAssignReport().getCommunicationList().getEmails())
										.map(i -> Email.newBuilder().setUserEmail(orEmpty(i.getUserEmail()))
												.setUserName(orEmpty(i.getUserName())).build())
										.collect(toList()))
								.build())
						.build())
				.setSchedule(com.flair.bi.messages.report.Schedule.newBuilder()
						.setCronExp(orEmpty(dto.getSchedule().getCronExp()))
						.setTimezone(orEmpty(dto.getSchedule().getTimezone()))
						.setStartDate(orEmpty(dto.getSchedule().getStartDate()))
						.setEndDate(orEmpty(dto.getSchedule().getEndDate())).build())
				.setConstraints(orEmpty(dto.getConstraints()))
				.setQuery(dto.getQuery()).build();
    }

    private SchedulerNotificationDTO createSchedulerNotificationDTO(ScheduleReportResponse response) {
        if (!response.hasReport()) {
            return null;
        }
        return createSchedulerNotificationDTO(response.getReport());
    }

    private SchedulerNotificationDTO createSchedulerNotificationDTO(ScheduleReport scheduleReport) {
        SchedulerNotificationDTO responseDTO = new SchedulerNotificationDTO();
        ReportDTO report = new ReportDTO();
        report.setDashboardName(scheduleReport.getReport().getDashboardName());
        report.setBuildUrl(scheduleReport.getReport().getBuildUrl());
        report.setMailBody(scheduleReport.getReport().getMailBody());
        report.setReportName(scheduleReport.getReport().getReportName());
        report.setShareLink(scheduleReport.getReport().getShareLink());
        report.setSubject(scheduleReport.getReport().getSubject());
        report.setTitleName(scheduleReport.getReport().getTitleName());
        report.setUserId(scheduleReport.getReport().getUserId());
        report.setViewName(scheduleReport.getReport().getViewName());
        report.setViewId(Long.valueOf(scheduleReport.getReport().getViewId()));
        report.setThresholdAlert(scheduleReport.getReport().getThresholdAlert());
        report.setCreatedDate(scheduleReport.getReport().getCreatedDate());
        responseDTO.setReport(report);
        ReportLineItem reportLineItem = new ReportLineItem();
        reportLineItem.setDimensions(scheduleReport.getReportLineItem().getDimensionsList().toArray(new String[]{}));
        reportLineItem.setMeasures(scheduleReport.getReportLineItem().getMeasuresList().toArray(new String[]{}));
        reportLineItem.setVisualizationType(scheduleReport.getReportLineItem().getVisualizationType());
        reportLineItem.setVisualizationId(scheduleReport.getReportLineItem().getVisualizationId());
        responseDTO.setReportLineItem(reportLineItem);
        responseDTO.setQuery(scheduleReport.getQuery());
        AssignReport assignReport = new AssignReport();
        CommunicationList communicationList= new CommunicationList();
        communicationList.setEmails(scheduleReport.getAssignReport().getCommunicationList().getEmailsList()
                .stream()
                .map(item -> toEmailDto(item))
                .collect(toList()).toArray(new emailsDTO[]{}));
        communicationList.setTeams(scheduleReport.getAssignReport().getCommunicationList().getTeamsList().toArray(new Integer[]{}));
        assignReport.setCommunicationList(communicationList);
        assignReport.setChannels(scheduleReport.getAssignReport().getChannelsList().toArray(new String[]{}));;
        assignReport.setChannelId(scheduleReport.getAssignReport().getChannelId());
        assignReport.setSlackAPIToken(scheduleReport.getAssignReport().getSlackAPIToken());
        assignReport.setStrideAPIToken(scheduleReport.getAssignReport().getStrideAPIToken());
        assignReport.setStrideCloudId(scheduleReport.getAssignReport().getStrideCloudId());
        assignReport.setStrideConversationId(scheduleReport.getAssignReport().getStrideConversationId());
        responseDTO.setAssignReport(assignReport);
        Schedule schedule = new Schedule();
        schedule.setCronExp(scheduleReport.getSchedule().getCronExp());
        schedule.setEndDate(scheduleReport.getSchedule().getEndDate());
        schedule.setStartDate(scheduleReport.getSchedule().getStartDate());
        schedule.setTimezone(scheduleReport.getSchedule().getTimezone());
        responseDTO.setSchedule(schedule);
        responseDTO.setConstraints(scheduleReport.getConstraints());
        return responseDTO;
    }

    private emailsDTO toEmailDto(Email item) {
        emailsDTO emailsDTO = new emailsDTO();
        emailsDTO.setUserEmail(item.getUserEmail());
        emailsDTO.setUserName(item.getUserName());
        return emailsDTO;
    }

	@Override
	public GetChannelConnectionDTO getChannelParameters(String channel) {
		GetChannelPropertiesResponse response = getReportStub()
				.getChannelProperties(GetChannelPropertiesRequest.newBuilder().setChannel(channel).build());
		return GetChannelConnectionDTO.builder()
				.channelParameters(toChannelParametersDTO(response.getChannelParametersList())).build();
	}

	private List<ChannelParametersDTO> toChannelParametersDTO(List<ChannelParameters> list) {
		return list.stream().map(item -> createChannelParametersDTO(item)).collect(toList());
	}

	private List<ConnectionPropertiesDTO> toConnectionPropertiesDTO(List<ConnectionProperties> list) {
		return list.stream().map(item -> createConnectionPropertiesDTO(item)).collect(toList());
	}

	private ChannelParametersDTO createChannelParametersDTO(ChannelParameters channelParameters) {
		ChannelParametersDTO channelParametersDTO = new ChannelParametersDTO();
		channelParametersDTO
				.setConnectionProperties(toConnectionPropertiesDTO(channelParameters.getConnectionPropertiesList()));
		channelParametersDTO.setId(channelParameters.getId());
		return channelParametersDTO;
	}

	private ConnectionPropertiesDTO createConnectionPropertiesDTO(ConnectionProperties connectionProperties) {
		ConnectionPropertiesDTO connectionPropertiesDTO = new ConnectionPropertiesDTO();
		connectionPropertiesDTO.setDisplayName(connectionProperties.getDisplayName());
		connectionPropertiesDTO.setFieldName(connectionProperties.getFieldName());
		connectionPropertiesDTO.setFieldType(connectionProperties.getFieldType());
		connectionPropertiesDTO.setOrder(connectionProperties.getOrder());
		connectionPropertiesDTO.setRequired(connectionProperties.getRequired());
		return connectionPropertiesDTO;
	}

	@Override
	public String createTeamConfig(TeamConfigParametersDTO teamConfigParametersDTO) {
		ConfigsResponse response = getReportStub().addTeamConfigs(AddTeamConfigsRequest.newBuilder()
				.setTeamConfigParameter(toTeamConfigParameters(teamConfigParametersDTO)).build());
		return response.getMessage();
	}

	@Override
	public String updateTeamConfig(TeamConfigParametersDTO teamConfigParametersDTO) {
		ConfigsResponse response = getReportStub().updateTeamWebhookURL(UpdateTeamWebhookURLRequest.newBuilder()
				.setTeamConfigParameter(toUpdateTeamConfigParameters(teamConfigParametersDTO)).build());
		return response.getMessage();
	}

	private TeamConfigParameters toTeamConfigParameters(TeamConfigParametersDTO teamConfigParametersDTO) {
		return TeamConfigParameters.newBuilder().setWebhookName(teamConfigParametersDTO.getWebhookName())
				.setWebhookURL(teamConfigParametersDTO.getWebhookURL()).build();
	}

	private TeamConfigParameters toUpdateTeamConfigParameters(TeamConfigParametersDTO teamConfigParametersDTO) {
		return TeamConfigParameters.newBuilder().setWebhookName(teamConfigParametersDTO.getWebhookName())
				.setWebhookURL(teamConfigParametersDTO.getWebhookURL()).setId(teamConfigParametersDTO.getId()).build();
	}

	@Override
	public String createEmailConfig(EmailConfigParametersDTO emailConfigParametersDTO) {
		ConfigsResponse response = getReportStub().addEmailConfigs(AddEmailConfigsRequest.newBuilder()
				.setEmailParameter(toEmailConfigParameters(emailConfigParametersDTO)).build());
		return response.getMessage();
	}

	private EmailParameters toEmailConfigParameters(EmailConfigParametersDTO emailConfigParametersDTO) {
		return EmailParameters.newBuilder().setHost(emailConfigParametersDTO.getHost())
				.setPassword(emailConfigParametersDTO.getPassword()).setPort(emailConfigParametersDTO.getPort())
				.setSender(emailConfigParametersDTO.getSender()).setUser(emailConfigParametersDTO.getUser()).build();
	}

	@Override
	public String updateEmailConfig(EmailConfigParametersDTO emailConfigParametersDTO) {
		ConfigsResponse response = getReportStub().updateEmailSMTP(UpdateEmailSMTPRequest.newBuilder()
				.setEmailParameter(toUpdateEmailConfigParameters(emailConfigParametersDTO)).build());
		return response.getMessage();
	}

	private EmailParameters toUpdateEmailConfigParameters(EmailConfigParametersDTO emailConfigParametersDTO) {
		return EmailParameters.newBuilder().setHost(emailConfigParametersDTO.getHost())
				.setPassword(emailConfigParametersDTO.getPassword()).setPort(emailConfigParametersDTO.getPort())
				.setSender(emailConfigParametersDTO.getSender()).setUser(emailConfigParametersDTO.getUser())
				.setId(emailConfigParametersDTO.getId()).build();
	}

	@Override
	public EmailConfigParametersDTO getEmailConfig(Integer id) {
		GetEmailConfigResponse response = getReportStub()
				.getEmailConfig(GetEmailConfigRequest.newBuilder().setId(id).build());
		return toEmailConfigParametersDTO(response.getRecord());
	}

	private EmailConfigParametersDTO toEmailConfigParametersDTO(EmailParameters emailParameters) {
		EmailConfigParametersDTO emailConfigParametersDTO = new EmailConfigParametersDTO();
		emailConfigParametersDTO.setHost(emailParameters.getHost());
		emailConfigParametersDTO.setId(emailParameters.getId());
		emailConfigParametersDTO.setPassword(emailParameters.getPassword());
		emailConfigParametersDTO.setPort(emailParameters.getPort());
		emailConfigParametersDTO.setSender(emailParameters.getSender());
		emailConfigParametersDTO.setUser(emailParameters.getUser());
		return emailConfigParametersDTO;
	}

	@Override
	public List<TeamConfigParametersDTO> getTeamConfig(Integer id) {
		GetTeamConfigResponse response = getReportStub()
				.getTeamConfig(GetTeamConfigRequest.newBuilder().setId(id).build());
		return toTeamConfigParametersDTOList(response.getRecordsList());
	}

	@Override
	public List<String> getTeamNames(Integer id) {
		GetTeamNamesResponse response = getReportStub()
				.getTeamNames(GetTeamConfigRequest.newBuilder().setId(id).build());
		return response.getRecordsList();
	}

	private List<TeamConfigParametersDTO> toTeamConfigParametersDTOList(List<TeamConfigParameters> list) {
		return list.stream().map(item -> toTeamConfigParametersDTO(item)).collect(toList());

	}

	private TeamConfigParametersDTO toTeamConfigParametersDTO(TeamConfigParameters teamConfigParameters) {
		TeamConfigParametersDTO teamConfigParametersDTO = new TeamConfigParametersDTO();
		teamConfigParametersDTO.setWebhookName(teamConfigParameters.getWebhookName());
		teamConfigParametersDTO.setWebhookURL(teamConfigParameters.getWebhookURL());
		teamConfigParametersDTO.setId(teamConfigParameters.getId());
		return teamConfigParametersDTO;
	}

	@Override
	public String deleteChannelConfig(Integer id) {
		ConfigsResponse response = getReportStub()
				.deleteChannelConfig(DeleteChannelConfigRequest.newBuilder().setId(id).build());
		return response.getMessage();
	}

	@Override
	public String createJiraConfig(JiraParametersDTO jiraParametersDTO) {
		ConfigsResponse response = getReportStub().addJiraConfigs(
				JiraConfigsRequest.newBuilder().setJiraParameter(toJiraParametersDTO(jiraParametersDTO)).build());
		return response.getMessage();
	}

	private JiraParameters toJiraParametersDTO(JiraParametersDTO jiraParametersDTO) {
		return JiraParameters.newBuilder().setApiToken(jiraParametersDTO.getApiToken())
				.setKey(jiraParametersDTO.getKey()).setOrganization(jiraParametersDTO.getOrganization())
				.setUserName(jiraParametersDTO.getUserName()).build();
	}

	@Override
	public String updateJiraConfig(JiraParametersDTO jiraParametersDTO) {
		ConfigsResponse response = getReportStub().updateJiraConfigs(
				JiraConfigsRequest.newBuilder().setJiraParameter(toUpdateJiraParametersDTO(jiraParametersDTO)).build());
		return response.getMessage();
	}

	private JiraParameters toUpdateJiraParametersDTO(JiraParametersDTO jiraParametersDTO) {
		return JiraParameters.newBuilder().setApiToken(jiraParametersDTO.getApiToken())
				.setKey(jiraParametersDTO.getKey()).setOrganization(jiraParametersDTO.getOrganization())
				.setUserName(jiraParametersDTO.getUserName()).setId(jiraParametersDTO.getId()).build();
	}

	@Override
	public JiraParametersDTO getJiraConfig(Integer id) {
		GetJiraConfigResponse response = getReportStub()
				.getJiraConfig(GetJiraConfigRequest.newBuilder().setId(id).build());
		return createJiraParametersDTO(response.getRecord());
	}

	private JiraParametersDTO createJiraParametersDTO(JiraParameters jiraParameters) {
		JiraParametersDTO jiraParametersDTO = new JiraParametersDTO();
		jiraParametersDTO.setApiToken(jiraParameters.getApiToken());
		jiraParametersDTO.setId(jiraParameters.getId());
		jiraParametersDTO.setKey(jiraParameters.getKey());
		jiraParametersDTO.setOrganization(jiraParameters.getOrganization());
		jiraParametersDTO.setUserName(jiraParameters.getUserName());
		return jiraParametersDTO;
	}

	@Override
	public GetJiraTicketResponseDTO createJiraTicket(Integer id) {
		CreateJiraTicketResponse response = getReportStub()
				.createJiraTicket(CreateJiraTicketRequest.newBuilder().setId(id).build());
		return GetJiraTicketResponseDTO.builder().jiraTicketLink(response.getJiraTicketLink())
				.message(response.getMessage()).build();
	}

	@Override
	public GetJiraTicketsDTO getJiraTickets(String status, Integer page, Integer pageSize) {
		GetAllJiraResponse response = getReportStub().getAllJira(
				GetAllJiraRequest.newBuilder().setStatus(status).setPage(page).setPageSize(pageSize).build());
		return GetJiraTicketsDTO.builder().records(toJiraTicketsDTOList(response.getRecordsList()))
				.totalRecords(response.getTotalRecords()).build();
	}

	private List<JiraTicketsDTO> toJiraTicketsDTOList(List<JiraTickets> list) {
		return list.stream().map(item -> toJiraTicketsDTO(item)).collect(toList());

	}

	private JiraTicketsDTO toJiraTicketsDTO(JiraTickets jiraTickets) {
		JiraTicketsDTO jiraTicketsDTO = new JiraTicketsDTO();
		jiraTicketsDTO.setAssignPerson(jiraTickets.getAssignPerson());
		jiraTicketsDTO.setCreateDate(jiraTickets.getCreateDate());
		jiraTicketsDTO.setIssueID(jiraTickets.getIssueID());
		jiraTicketsDTO.setPriority(jiraTickets.getPriority());
		jiraTicketsDTO.setProjectKey(jiraTickets.getProjectKey());
		jiraTicketsDTO.setReporter(jiraTickets.getReporter());
		jiraTicketsDTO.setStatus(jiraTickets.getStatus());
		jiraTicketsDTO.setSummary(jiraTickets.getSummary());
		jiraTicketsDTO.setViewTicket(jiraTickets.getViewTicket());
		jiraTicketsDTO.setCreatedBy(jiraTickets.getCreatedBy());
		return jiraTicketsDTO;
	}

	@Override
	public String disableTicketCreationRequest(Integer schedulerTaskLogId) {
		ConfigsResponse response = getReportStub().disableTicketCreation(
				DisableTicketCreationRequest.newBuilder().setSchedulerTaskLogId(schedulerTaskLogId).build());
		return response.getMessage();
	}

	@Override
	public String notifyOpenedJiraTicket(OpenJiraTicketDTO openJiraTicketDTO) {
		ConfigsResponse response = getReportStub().notifyOpenedJiraTicket(NotifyOpenedJiraTicketRequest.newBuilder()
				.setOpenJiraTicket(toOpenJiraTicket(openJiraTicketDTO)).build());
		return response.getMessage();
	}

	private OpenJiraTicket toOpenJiraTicket(OpenJiraTicketDTO openJiraTicketDTO) {
		return OpenJiraTicket.newBuilder().setProject(openJiraTicketDTO.getProject())
				.setWebhookID(openJiraTicketDTO.getWebhookID())
				.addAllChannels(Arrays.asList(openJiraTicketDTO.getChannels())).build();
	}

	@Override
	public Boolean isConfigExist(Integer id) {
		IsConfigExistResponse response = getReportStub()
				.isConfigExist(IsConfigExistRequest.newBuilder().setId(id).build());
		return response.getIsConfigExist();
	}

}
