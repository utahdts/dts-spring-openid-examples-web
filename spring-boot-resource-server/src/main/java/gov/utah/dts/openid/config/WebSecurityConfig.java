package gov.utah.dts.openid.config;

import gov.utah.dts.openid.security.CustomAuthenticationConverter;
import gov.utah.dts.openid.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;

import static org.springframework.http.HttpMethod.GET;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private UserService userService;

	@Autowired
	public WebSecurityConfig(UserService userService) {
		this.userService = userService;
	}

	@Override
	public void configure(WebSecurity webSecurity) throws Exception {
		webSecurity
				.ignoring()
				.antMatchers("/resources/**", "/canary/**", "/error", "/403.html", "/version.txt");
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
				.cors()
				.and()
				.headers().frameOptions().disable()
				.and()
				.csrf().disable()
				.authorizeRequests()
				/* Most restrictive first */
				.antMatchers(GET, "/userInfoAdmin").hasAnyAuthority("ROLE_ADMIN")
				.antMatchers(GET, "/userInfo").authenticated()
				.antMatchers(GET, "/").permitAll()
				.and()
				.oauth2ResourceServer()
				.jwt()
				.jwtAuthenticationConverter(authenticationConverter())

		;
	}

	private Converter<Jwt, AbstractAuthenticationToken> authenticationConverter() {
		return new CustomAuthenticationConverter(userService);
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowCredentials(true);
		configuration.setAllowedOrigins(Collections.singletonList("*"));
		configuration.setAllowedMethods(Collections.singletonList("*"));
		configuration.setAllowedHeaders(Collections.singletonList("*"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

}