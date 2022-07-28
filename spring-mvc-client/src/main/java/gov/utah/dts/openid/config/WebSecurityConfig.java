package gov.utah.dts.openid.config;

import gov.utah.dts.openid.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Arrays;
import java.util.List;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@PropertySource("classpath:openid.properties")
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private Environment env;

	private OAuth2UserService<OidcUserRequest, OidcUser> customOidcUserService;

	@Autowired
	public WebSecurityConfig(OAuth2UserService<OidcUserRequest, OidcUser> customOidcUserService, Environment env) {
		this.customOidcUserService = customOidcUserService;
		this.env = env;
	}

	@Override
	public void configure(WebSecurity web) {
		web
			.ignoring()
			.antMatchers("/error.htm", "/css/**", "/ico/**", "/img/**", "/images/**", "/js/**", "/js/vendor/**", "/canary/**", "/403.html");
	}

	@Override
	/**
	 *
	 * Using hasAnyRole should not start with "ROLE_" as this is automatically inserted.  When the db role name starts with "ROLE_"
	 * Using hasAnyAuthority should match exactly the role name in the db.
	 */
	public void configure(HttpSecurity http) throws Exception {
		http
				.authorizeRequests(authorize -> authorize
						.antMatchers("/**").hasAnyAuthority(Constants.ROLE_AGENCY_STAFF, Constants.ROLE_USER, Constants.ROLE_ADMIN)
				)
				.oauth2Login()
//				.loginPage("/oauth2/authorization/utahid")
				.failureUrl("/error")
				.userInfoEndpoint()
				.oidcUserService(customOidcUserService)
				;
	}

	@Bean
	public ClientRegistrationRepository clientRegistrationRepository() {
		return new InMemoryClientRegistrationRepository(this.utahIdClientRegistration());
	}

	private ClientRegistration utahIdClientRegistration() {
		final String BASE = "security.oauth2.client.";
		final String CLIENT_NAME = "utahid";
		String scopes = env.getProperty(BASE + "scope");
		assert scopes != null;
		List<String> scopeList = Arrays.asList(scopes.split(","));

		return ClientRegistration.withRegistrationId(CLIENT_NAME)
				.clientId(env.getProperty(BASE + "client-id"))
				.clientSecret(env.getProperty(BASE + "client-secret"))
				.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
				.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
				.redirectUri(env.getProperty(BASE + "pre-established-redirect-uri"))
				.scope(scopeList)
				.authorizationUri(env.getProperty(BASE + "user-authorization-uri"))
				.tokenUri(env.getProperty(BASE + "access-token-uri"))
				.userInfoUri(env.getProperty(BASE + "user-info-uri"))
				.userNameAttributeName(IdTokenClaimNames.SUB)
				.jwkSetUri(env.getProperty(BASE + "jwk-uri"))
				.clientName(CLIENT_NAME)
				.build();
	}

}
