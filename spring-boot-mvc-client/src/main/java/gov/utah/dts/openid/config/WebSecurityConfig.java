package gov.utah.dts.openid.config;

import gov.utah.dts.openid.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private OAuth2UserService<OidcUserRequest, OidcUser> customOidcUserService;

	@Autowired
	public WebSecurityConfig(OAuth2UserService<OidcUserRequest, OidcUser> customOidcUserService) {
		this.customOidcUserService = customOidcUserService;
	}


	@Override
	public void configure(WebSecurity web) {
		web
			.ignoring()
			.antMatchers("/error.htm", "/css/**", "/ico/**", "/img/**", "/images/**", "/js/**", "/js/vendor/**", "/canary/**", "/403.html");
	}

	@Override
	/**
	 * Using hasAnyRole should not start with "ROLE_" as this is automatically inserted.  When the db role name starts with "ROLE_"
	 * Using hasAnyAuthority should match exactly the role name in the db.
	 */
	public void configure(HttpSecurity http) throws Exception {
		
		http
			.authorizeRequests(authorize -> authorize
				.antMatchers("/secure.html").hasAnyAuthority(Constants.ROLE_ADMIN)
				.antMatchers("/**").authenticated()
			)
			.oauth2Login()
				.loginPage("/oauth2/authorization/utahid")
				.failureUrl("/error")
				.userInfoEndpoint()
				.oidcUserService(customOidcUserService)
		;
		http.cors().and().csrf().disable();
	}

}
