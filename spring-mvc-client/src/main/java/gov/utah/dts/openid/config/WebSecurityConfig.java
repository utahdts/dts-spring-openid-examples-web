package gov.utah.dts.openid.config;

import gov.utah.dts.openid.Constants;
import gov.utah.dts.openid.security.filter.OpenIdConnectAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private final OAuth2RestTemplate oAuth2RestTemplate;

	@Autowired
	public WebSecurityConfig(OAuth2RestTemplate oAuth2RestTemplate) {
		this.oAuth2RestTemplate = oAuth2RestTemplate;
	}

	@Override
	public void configure(WebSecurity web) {
		web
			.ignoring()
			.antMatchers("/error.htm", "/css/**", "/ico/**", "/img/**", "/images/**", "/js/**", "/js/vendor/**", "/canary/**", "/403.html");
	}

	@Bean
	public OpenIdConnectAuthenticationFilter createOpenIdConnectFilter() {
		final OpenIdConnectAuthenticationFilter openIdConnectFilter = new OpenIdConnectAuthenticationFilter("/login");
		openIdConnectFilter.setRestTemplate(oAuth2RestTemplate);
		return openIdConnectFilter;
	}

	@Override
	/**
	 * Using hasAnyRole should not start with "ROLE_" as this is automatically inserted.  When the db role name starts with "ROLE_"
	 * Using hasAnyAuthority should match exactly the role name in the db.
	 */
	public void configure(HttpSecurity http) throws Exception {
		http
			.addFilterAfter(new OAuth2ClientContextFilter(), AbstractPreAuthenticatedProcessingFilter.class)
			.addFilterAfter(createOpenIdConnectFilter(), OAuth2ClientContextFilter.class)
			.httpBasic().authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
		.and()
			.exceptionHandling().accessDeniedPage("/403")
		.and()
			.authorizeRequests()
			.antMatchers("/**").hasAnyAuthority(Constants.ROLE_AGENCY_STAFF, Constants.ROLE_USER, Constants.ROLE_ADMIN)
		;
	}

	@Bean
	public Http403ForbiddenEntryPoint forbiddenEntryPoint() {
		return new Http403ForbiddenEntryPoint();
	}

}
