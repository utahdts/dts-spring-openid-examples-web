package gov.utah.dts.openid.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

import java.net.URI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class HomeControllerIT {

	@Value(value="${local.server.port}")
	private int port;

	private URI base;

	@Value("${server.servlet.contextPath}")
	protected String contextPath;

	@Autowired
	private TestRestTemplate template;

	@BeforeEach
	void setUp() throws Exception {
		this.base = new URI("http://localhost:" + port + contextPath + "/");
	}

	@Test
	void getHello() {
		ResponseEntity<String> response = template.getForEntity(base, String.class);
		assertThat(response.getBody(), containsString("App version:"));
	}
}
