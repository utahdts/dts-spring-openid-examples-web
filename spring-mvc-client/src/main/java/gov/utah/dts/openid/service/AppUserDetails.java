package gov.utah.dts.openid.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import gov.utah.dts.openid.model.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

/**
 * The Class AppUserDetails.
 */
public class AppUserDetails extends DefaultOidcUser {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 111113L;

	/** The name. */
	private String name;

	/** The id. */
	private Long id;

	/** The admin. */
	private Boolean admin;

	private String umdUserId;

	private String umdEmail;

	private OAuth2AccessToken accessToken;

	/**
	 * Instantiates a new app user details.
	 *
	 * @param openIdMap the openAm openId map
	 * @param accessToken the accessToken
	 * @param member the db member
	 * @param authList the authorities
	 */
	/**
	 * Instantiates a new app user details.
	 *
	 * @param member the db member record
	 * @param idToken the accessToken
	 * @param userInfo contains openId claims map
	 * @param authorities the roles the user has
	 */
	public AppUserDetails(Member member,
						  OidcIdToken idToken,
						  OidcUserInfo userInfo,
						  Collection<? extends GrantedAuthority> authorities) {
		super(authorities, idToken, userInfo);
		this.name = member.getName();
		this.id = member.getId();
		this.admin = member.isAdmin();
		this.umdUserId = (String) userInfo.getClaims().get("uid");
		this.umdEmail = member.getEmail();
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	public String getUmdUserId() {
		return umdUserId;
	}

	public String getUmdEmail() {
		return umdEmail;
	}

}
