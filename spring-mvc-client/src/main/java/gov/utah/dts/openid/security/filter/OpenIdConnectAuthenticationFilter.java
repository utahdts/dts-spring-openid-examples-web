package gov.utah.dts.openid.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.utah.dts.openid.Constants;
import gov.utah.dts.openid.model.Audit;
import gov.utah.dts.openid.model.Member;
import gov.utah.dts.openid.repository.MemberRepository;
import gov.utah.dts.openid.service.AppUserDetails;
import gov.utah.dts.openid.service.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.util.Optional.empty;

/**
 * Custom authentication filter for using OpenID Connect.
 *
 */
public class OpenIdConnectAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	private final Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * The member repo.
	 */
	@Autowired
	private MemberRepository memberRepo;

	/**
	 * The audit service.
	 */
	@Autowired
	private AuditService auditService;

	@Autowired
	private OAuth2RestTemplate openAmOpenIdConnectRestTemplate;

	public OpenIdConnectAuthenticationFilter(String defaultFilterProcessesUrl) {
		super(defaultFilterProcessesUrl);
		setAuthenticationManager(new NoOpAuthenticationManager());
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		OAuth2AccessToken accessToken;

		try {
			accessToken = openAmOpenIdConnectRestTemplate.getAccessToken();
			log.info("AccessToken: value: {}", accessToken.getValue());
			log.info("AccessToken: additionalInfo: {}", accessToken.getAdditionalInformation());
			log.info("AccessToken: tokenType: {}", accessToken.getTokenType());
			log.info("AccessToken: expiration: {}", accessToken.getExpiration());
			log.info("AccessToken: expiresIn: {}", accessToken.getExpiresIn());

		} catch (OAuth2Exception e) {
			throw new BadCredentialsException("Could not obtain Access Token", e);
		}

		try {
			final String idToken = accessToken.getAdditionalInformation().get("id_token").toString();
			log.info("Encoded id_token from accessToken.additionalInformation: {}", idToken);

			final Jwt tokenDecoded = JwtHelper.decode(idToken);
			log.info("Decoded JWT id_token: {}", tokenDecoded);
			log.info("Decoded JWT id_token -> claims: {}", tokenDecoded.getClaims());

			final Map authInfo = new ObjectMapper().readValue(tokenDecoded.getClaims(), Map.class);

			return grantUserAuthority(authInfo, accessToken);

		} catch (InvalidTokenException e) {
			throw new BadCredentialsException("Could not obtain user details from Access Token", e);
		}
	}

	public void setRestTemplate(OAuth2RestTemplate restTemplate) {
		this.openAmOpenIdConnectRestTemplate = restTemplate;
	}

	/**
	 * Refreshes the security token for the user according to the granted authority list
	 *
	 * @param user the user
	 * @param authList the authorities
	 */
	private PreAuthenticatedAuthenticationToken refreshSpringSecurityToken(AppUserDetails user, List<GrantedAuthority> authList) {
		auditUserLogin(user);
		PreAuthenticatedAuthenticationToken authentication = new PreAuthenticatedAuthenticationToken(user, empty(), authList);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		return authentication;
	}

	/**
	 * Grants a userInfo access to the system based on their associated role
	 *
	 * @param openIdMap map
	 * @return a PreAuthenticatedAuthenticationToken representing the user's access in the system
	 */
	private PreAuthenticatedAuthenticationToken grantUserAuthority(Map<String, String> openIdMap, OAuth2AccessToken accessToken) {
		UserDetails userDetails;

		String email = openIdMap.get("email");
		Member dbUser = loadDatabaseUser(email);

		List<GrantedAuthority> authList = null;
		AppUserDetails user;
		if (dbUser != null) {
			authList = createUserAuthority(dbUser);
			userDetails = new AppUserDetails(openIdMap, accessToken, dbUser, authList);

			log.info("OpenIdConnectUserDetails -> userId: {}", ((AppUserDetails) userDetails).getUmdUserId());
			user = (AppUserDetails) userDetails;
		} else {
			log.error("Error loading user in loadDatabaseUser. email: {}", email);
			authList = new ArrayList<GrantedAuthority>();
			GrantedAuthority ga = new SimpleGrantedAuthority(Constants.ROLE_PUBLIC);
			authList.add(ga);
			user = (AppUserDetails) unauthorizedUser(openIdMap, authList, email);
		}
		return refreshSpringSecurityToken(user, authList);

	}

	private UserDetails unauthorizedUser(Map<String, String> openIdMap, List<GrantedAuthority> authList, String email) {
		Member member = new Member();
		member.setAdmin(false);
		member.setName(openIdMap.get("name"));
		member.setEmail(email);

		return new AppUserDetails(
				openIdMap,
				null,
				member,
				authList);
	}

	private void auditUserLogin(AppUserDetails user) {
		String userId = user.getId() != null ? user.getId().toString() : "No User Id found";
		auditService.createAuditRow(new Date(), user, "", "MEMBER", user.getUsername(), "", auditService.getTransaction(), Audit.ACTION_LOGIN, null, userId);
	}

	/**
	 * Attempts to load a db user entity for a provided email
	 * A value of null is returned if the userInfo can not be loaded, either because the email is not found, the db user is inactive,
	 * the db user's role is null, or the assigned role has a name value of null
	 *
	 * @param email the email
	 * @return null if invalid, UserInfo object if found/valid
	 */
	private Member loadDatabaseUser(String email) {
		try {
			Member m = memberRepo.findByEmailAndInactive(email, false);
			if ((m == null) || (m.getEmail() == null) || (m.isInactive()) || (m.getRole() == null) || (m.getRole().getName() == null)) {
				log.warn("Did not find user in loadUserByUsername. failed to find email: {}", email);
				return null;
			}

			return m;
		} catch (Exception e) {
			log.error("Error loading user in loadUserByUsername. email: {}", email, e);
			return null;
		}
	}

	private List<GrantedAuthority> createUserAuthority(Member member) {
		GrantedAuthority ga = new SimpleGrantedAuthority(member.getRole().getName());
		List<GrantedAuthority> authList = new ArrayList<>();
		authList.add(ga);
		if (member.isAdmin()) {
			ga = new SimpleGrantedAuthority(Constants.ROLE_ADMIN);
			authList.add(ga);
		}
		return authList;
	}

	private static class NoOpAuthenticationManager implements AuthenticationManager {
		@Override
		public Authentication authenticate(Authentication authentication) {
			throw new UnsupportedOperationException("No authentication should be done with this AuthenticationManager");
		}
	}


}
