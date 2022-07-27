package gov.utah.dts.openid.security;

import gov.utah.dts.openid.model.DatabaseUser;
import gov.utah.dts.openid.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Optional.empty;

@Slf4j
public class CustomAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

	private UserService userService;

	public CustomAuthenticationConverter(UserService userService) {
		this.userService = userService;
	}

	public AbstractAuthenticationToken convert(Jwt jwt) {

		Map<String, Object> openIdMap = jwt.getClaims();

		List<GrantedAuthority> authList = new ArrayList<>();
		String utahId = (String)openIdMap.get("sub");
		DatabaseUser databaseUser = loadDbUser(utahId);
		GrantedAuthority ga = new SimpleGrantedAuthority("no_role");
		if (databaseUser == null || databaseUser.getRole() == null) {
			log.warn("User not found");
		} else {
			String respectMyAuthority = databaseUser.getRole();
			ga = new SimpleGrantedAuthority(respectMyAuthority);
		}
		authList.add(ga);

		OpenIdConnectUserDetails userDetails = new OpenIdConnectUserDetails(openIdMap, databaseUser, jwt);
		log.info("OpenIdConnectUserDetails -> userId: " + userDetails.getUsername());
		return new PreAuthenticatedAuthenticationToken(userDetails, empty(), authList);
	}

	/**
	 * Attempts to load a databaseUser entity for a provided uid
	 * A value of null is returned if the userInfo can not be loaded, either because the email is not found, the databaseUser is inactive,
	 * the databaseUser's role is null, or the assigned role has a name value of null
	 *
	 * @param uid
	 * @return null if invalid, UserInfo object if found/valid
	 */
	private DatabaseUser loadDbUser(String uid) {
		DatabaseUser userInfo = userService.findByUid(uid);
		if (userInfo == null) {
			log.warn("********** UNABLE TO LOAD USER FOR UTAHID: {} - UID IS NULL **********", uid);
		}

		return userInfo;
	}

}