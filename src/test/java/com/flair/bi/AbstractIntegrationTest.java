package com.flair.bi;

import org.jetbrains.annotations.NotNull;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = { "integration" })
public abstract class AbstractIntegrationTest {

	@Autowired
	protected TestRestTemplate restTemplate;

	@LocalServerPort
	private int port;

	@NotNull
	protected String getUrl() {
		return "http://localhost:" + port;
	}
}
