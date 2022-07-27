package gov.utah.dts.openid.security;

import gov.utah.dts.openid.model.DatabaseUser;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Encapsulates OpenID Connect user details.
 */
public class OpenIdConnectUserDetails implements UserDetails {

	private static final long serialVersionUID = -4153740588312462691L;

	private String utahId;
	private String email;
	private DatabaseUser databaseUser;
	private Jwt jwt;


	public OpenIdConnectUserDetails(Map<String, Object> userInfo, DatabaseUser databaseUser, Jwt jwt) {
		this.utahId = (String) userInfo.get("sub");
		if (databaseUser != null) {
			this.email = databaseUser.getEmail();
		}
		this.jwt = jwt;
		this.databaseUser = databaseUser;
	}

	@Override
	public String getUsername() {
		return utahId;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
	}

	@Override
	public String getPassword() {
		/*
		 * not relevant for OpenID Connect
		 */
		return null;
	}

	public DatabaseUser getDatabaseUser() {
		return databaseUser;
	}

	public Jwt getJwt() {
		return jwt;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}


	public String getEmail() {
		return email;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("userId", utahId)
				.append("email", email)
				.append("databaseUser", databaseUser)
				.toString();
	}
}
