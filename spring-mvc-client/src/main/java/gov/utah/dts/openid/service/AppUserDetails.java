package gov.utah.dts.openid.service;

import java.util.List;
import java.util.Map;

import gov.utah.dts.openid.model.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

/**
 * The Class AppUserDetails.
 */
public class AppUserDetails extends User {

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
	public AppUserDetails(Map<String, String> openIdMap, OAuth2AccessToken accessToken, Member member, List<GrantedAuthority> authList) {
		super(member.getEmail(), "", true, true, true,true, authList);
		this.name = member.getName();
		this.id = member.getId();
		this.admin = member.isAdmin();
		this.umdUserId = openIdMap.get("uid");
		this.umdEmail = openIdMap.get("email");
		this.accessToken = accessToken;
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

	/**
	 * Gets the admin.
	 *
	 * @return the admin
	 */
	public Boolean getAdmin() {
		return admin;
	}

	/**
	 * Sets the admin.
	 *
	 * @param admin the new admin
	 */
	public void setAdmin(Boolean admin) {
		this.admin = admin;
	}


	public String getUmdUserId() {
		return umdUserId;
	}

	public String getUmdEmail() {
		return umdEmail;
	}

	public OAuth2AccessToken getAccessToken() {
		return accessToken;
	}
}
