package gov.utah.dts.openid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * Add @ServletComponentScan("gov.utah.lfa.cobi.canary") if jar deploy to pick up @webfilter @weblistener
 */
@ServletComponentScan("gov.utah.dts.openid.canary")
@SpringBootApplication(exclude = RepositoryRestMvcAutoConfiguration.class)
public class StartBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(StartBootApplication.class, args);
	}
	
}
