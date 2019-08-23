package com.flair.bi.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.flair.bi.domain.Datasource;
import com.flair.bi.domain.visualmetadata.VisualMetadata;
import com.flair.bi.security.SecurityUtils;
import com.flair.bi.service.DatasourceService;
import com.flair.bi.service.SchedulerService;
import com.flair.bi.service.dto.CountDTO;
import com.flair.bi.service.dto.scheduler.GetSchedulerReportDTO;
import com.flair.bi.service.dto.scheduler.SchedulerDTO;
import com.flair.bi.service.dto.scheduler.SchedulerNotificationDTO;
import com.flair.bi.service.dto.scheduler.SchedulerReportsDTO;
import com.flair.bi.service.dto.scheduler.SchedulerResponse;
import com.flair.bi.view.VisualMetadataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class SchedulerResource {

	@Value("${flair-notifications.username}")
	private String userName;

	@Value("${flair-notifications.password}")
	private String password;
	
	@Value("${flair-notifications.host}")
	private String host;

	@Value("${flair-notifications.port}")
	private String port;

	@Value("${flair-notifications.scheduled-report-url}")
	private String scheduledReportUrl;

	@Value("${flair-notifications.auth-url}")
	private String authUrl;

	@Value("${flair-notifications.mail_body}")
	private String mail_body;

	@Value("${flair-notifications.scheduled-reports-url}")
	private String scheduledReportsUrl;
	
	@Value("${flair-notifications.scheduled-reports-count-url}")
	private String scheduledReportsCountUrl;
	
	@Value("${flair-notifications.scheduled-report-param-url}")
	private String scheduledReportParamUrl;

	private final VisualMetadataService visualMetadataService;
	
	private final DatasourceService datasourceService;

	private final SchedulerService schedulerService;

	@PostMapping("/schedule")
	@Timed
	public ResponseEntity<SchedulerResponse> scheduleReport(@Valid @RequestBody SchedulerDTO schedulerDTO)
			throws Exception {
		log.info("Creating schedule report {}", schedulerDTO);
		VisualMetadata visualMetadata = visualMetadataService.findOne(schedulerDTO.getReport_line_item().getVisualizationid());
		Datasource datasource =datasourceService.findOne(schedulerDTO.getDatasourceid());
		if(!schedulerDTO.getEmailReporter() || !SecurityUtils.iAdmin()) {
			schedulerDTO.getAssign_report().setEmail_list(schedulerService.getEmailList(SecurityUtils.getCurrentUserLogin()));
		}
		schedulerDTO.getReport().setUserid(SecurityUtils.getCurrentUserLogin());
		schedulerDTO.getReport().setSubject("Report : " + visualMetadata.getMetadataVisual().getName() + " : " + new Date());
		schedulerDTO.getReport().setTitle_name(visualMetadata.getTitleProperties().getTitleText());
		schedulerDTO.getReport_line_item().setVisualization(visualMetadata.getMetadataVisual().getName());
		String query=schedulerService.buildQuery(schedulerDTO.getQueryDTO(),visualMetadata, datasource, schedulerDTO.getReport_line_item(), schedulerDTO.getReport_line_item().getVisualizationid(), schedulerDTO.getReport().getUserid(),schedulerDTO.getReport().getThresholdAlert());
		SchedulerNotificationDTO schedulerNotificationDTO= new SchedulerNotificationDTO(schedulerDTO.getReport(),schedulerDTO.getReport_line_item(),schedulerDTO.getAssign_report(),schedulerDTO.getSchedule(),query);

		schedulerService.setChannelCredentials(schedulerNotificationDTO);
        log.info("Sending schedule report {}", schedulerNotificationDTO);

        GetSchedulerReportDTO schedulerReportDTO;
        if (schedulerDTO.getPutcall()) {
            schedulerReportDTO = schedulerService.updateSchedulerReport(schedulerNotificationDTO);
        } else {
            schedulerReportDTO = schedulerService.createSchedulerReport(schedulerNotificationDTO);
        }

		log.info("Receiving schedule report {}", schedulerReportDTO);

		SchedulerResponse response = new SchedulerResponse();
		response.setMessage(schedulerReportDTO.getMessage());

		return ResponseEntity.ok(response);
	}


    @GetMapping("/schedule/reports/{pageSize}/{page}")
    @Timed
    public ResponseEntity<List<SchedulerNotificationDTO>> getSchedulerReports(@PathVariable Integer pageSize,@PathVariable Integer page)
        throws URISyntaxException {
		SchedulerReportsDTO reports = schedulerService.getScheduledReportsByUser(SecurityUtils.getCurrentUserLogin(), pageSize, page);
		return ResponseEntity.ok(reports.getReports());
    }
    
    @GetMapping("/schedule/reports/count")
    @Timed
    public CountDTO getScheduledReportsCount() throws JSONException {
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> responseEntity = restTemplate.exchange(schedulerService.buildUrl(host, port,scheduledReportsCountUrl), HttpMethod.GET,null,new ParameterizedTypeReference<String>() {
		}, SecurityUtils.getCurrentUserLogin());
		JSONObject jsonObject = new JSONObject(responseEntity.getBody());
		Long count = Long.parseLong(jsonObject.getString("totalReports"));
		return new CountDTO(count);
    }

	@GetMapping("/schedule/{visualizationid}")
	@Timed
	public ResponseEntity<GetSchedulerReportDTO> getSchedulerReport(@PathVariable String visualizationid) {
	    log.info("Get scheduled report {}", visualizationid);
		GetSchedulerReportDTO responseDTO = schedulerService.getSchedulerReport(visualizationid);

		log.info("Get scheduled report {} finished", visualizationid);
		return ResponseEntity.ok(responseDTO);
	}
	
	@DeleteMapping("/schedule/{visualizationid}")
	@Timed
	public ResponseEntity<GetSchedulerReportDTO> deleteSchedulerReport(@PathVariable String visualizationid) {
		log.info("Delete scheduled report {}", visualizationid);
		GetSchedulerReportDTO responseDTO = schedulerService.deleteSchedulerReport(visualizationid);

		log.info("Delete scheduled report {} finished {}", visualizationid, responseDTO);
		return ResponseEntity.ok(responseDTO);
	}

	@GetMapping("/executeImmediate/{visualizationid}")
	@Timed
	public ResponseEntity<SchedulerResponse> executeImmediate(@PathVariable String visualizationid)
			throws URISyntaxException {
		return schedulerService.executeImmediateScheduledReport(visualizationid);
	}

	@GetMapping("/schedule/report/logs/{visualizationid}/{pageSize}/{page}")
	@Timed
	public ResponseEntity<String> reportLogs(@PathVariable String visualizationid,@PathVariable Integer pageSize,@PathVariable Integer page)
			throws URISyntaxException {
		return schedulerService.scheduleReportLogs(visualizationid, pageSize, page);
	}
	@GetMapping("/schedule/searchReports/")
	@Timed
	public ResponseEntity<String> searchReports(@RequestParam String userName,@RequestParam String reportName,@RequestParam String startDate,@RequestParam String endDate,@RequestParam Integer pageSize,@RequestParam Integer page)
			throws URISyntaxException {
		if (!SecurityUtils.iAdmin()){
			userName=SecurityUtils.getCurrentUserLogin();
		}
		return schedulerService.searchScheduledReport(userName,reportName,startDate,endDate, pageSize, page);
	}
}
