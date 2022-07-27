package gov.utah.dts.openid.controller;

import gov.utah.dts.openid.security.OpenIdConnectUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.Map;

@Slf4j
@RestController
public class HomeController {

	@Value("${version}")
	protected String version;

	@Value("${security.oauth2.resource.user-info-uri}")
	private String userInfoUri;

	@RequestMapping("/")
	public String index() {

		return "App version: " + version;
	}

	@RequestMapping(value = "/version.txt", method = RequestMethod.GET)
	@ResponseBody
	String versionEndpoint() {
		return version;
	}

	@GetMapping("/userInfoAdmin")
	public String userInfoAdmin() {
		return getUserInfo();
	}

	@GetMapping("/userInfo")
	public String userInfo() {
		return getUserInfo();
	}

	private String getUserInfo() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		final Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		final String username = authentication.getName();
		log.info("Authenticated Username: {}", username);

		final OpenIdConnectUserDetails userDetails = (OpenIdConnectUserDetails) authentication.getPrincipal();
		log.info("Authentication Principal: {}", userDetails);
		final Map<String, Object> initialClaims = userDetails.getJwt().getClaims();

		final Map openAmUserInfo = getUserInfo(userDetails);
		log.info("UserInfo endpoint response: {}", openAmUserInfo);

		return "<h2>Secured Homepage</h2>User Id: " + userDetails.getUsername()
				+ (userDetails.getDatabaseUser() != null ? "<br>DB User: " + userDetails.getDatabaseUser().toString() : "<br>DB User: Not Found/Authorized")
				+ (initialClaims.get("auth_level") != null ? "<br>Auth Level: " + initialClaims.get("auth_level") : "<br>Auth Level: 0")
				+ (openAmUserInfo.get("email") != null ? "<br>Email: " + openAmUserInfo.get("email") : "<br>")
				+ (authorities != null ? "<br>Roles: " + authorities.toString() : "<br>")
				+ (openAmUserInfo.get("name") != null ? "<br>Name: " + openAmUserInfo.get("name") : "<br>")
				+ (openAmUserInfo.get("uid") != null ? "<br>UID: " + openAmUserInfo.get("uid") : "<br>")
				+ (openAmUserInfo.get("account_type") != null ? "<br>Account Type: " + openAmUserInfo.get("account_type") : "<br>")
				;
	}

	private Map getUserInfo(OpenIdConnectUserDetails userDetails) {
		final RestTemplate restTemplate = new RestTemplate();
		final HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Authorization", "Bearer " + userDetails.getJwt().getTokenValue());
		final HttpEntity<?> httpEntity = new HttpEntity<>(httpHeaders);
		final ResponseEntity<Map> userInfoResponseEntity = restTemplate.exchange(userInfoUri, HttpMethod.GET, httpEntity, Map.class);
		return userInfoResponseEntity.getBody();
	}
}
