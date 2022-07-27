package gov.utah.dts.openid.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.AccessTokenProviderChain;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

import java.util.Arrays;

/**
 * OpenID Connect config for authenticating using OpenAm.
 *
 * Loaded from the openid.properties.
 *
 */
@Configuration
@EnableOAuth2Client
@PropertySource("classpath:openid.properties")
public class OpenAmOpenIdConnectConfig {

	@Autowired
	Environment env;

    @Bean
    public OAuth2RestTemplate openAmOpenIdConnectRestTemplate(@Qualifier("oauth2ClientContext") OAuth2ClientContext clientContext) {
        OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(openAm(), clientContext);
		AccessTokenProviderChain providerChain = new AccessTokenProviderChain(Arrays.asList(new AuthorizationCodeAccessTokenProvider()));
		restTemplate.setAccessTokenProvider(providerChain);
        return restTemplate;
    }

	@Bean
	public AuthorizationCodeResourceDetails openAm() {
		AuthorizationCodeResourceDetails authorizationCodeResourceDetails = new AuthorizationCodeResourceDetails();
		authorizationCodeResourceDetails.setClientId(env.getProperty("security.oauth2.client.client-id"));
		authorizationCodeResourceDetails.setClientSecret(env.getProperty("security.oauth2.client.client-secret"));
		authorizationCodeResourceDetails.setAccessTokenUri(env.getProperty("security.oauth2.client.access-token-uri"));
		authorizationCodeResourceDetails.setUserAuthorizationUri(env.getProperty("security.oauth2.client.user-authorization-uri"));
		authorizationCodeResourceDetails.setPreEstablishedRedirectUri(env.getProperty("security.oauth2.client.pre-established-redirect-uri"));
		authorizationCodeResourceDetails.setClientAuthenticationScheme(AuthenticationScheme.valueOf(env.getProperty("security.oauth2.client.client-authentication-scheme")));
		authorizationCodeResourceDetails.setScope(Arrays.asList(env.getProperty("security.oauth2.client.scope").split(",")));
		authorizationCodeResourceDetails.setUseCurrentUri(Boolean.valueOf("security.oauth2.client.use-current-uri"));
		authorizationCodeResourceDetails.setId(env.getProperty("security.oauth2.client.id"));
		return authorizationCodeResourceDetails;
	}
}
