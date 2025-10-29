package gov.utah.dts.openid.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * The Class Member.
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "MEMBER")
public class Member {
    
	/** The id. */
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	/** The role. */
	@ManyToOne
	@JoinColumn(name="RoleId")
	private Role role;
	
	/** The name. */
	@Column(name = "Name")
	private String name;
	
	/** The email. */
	@Column(name = "Email")
	private String email;
	
	/** The inactive. */
	@Column(name = "Inactive")
	private boolean inactive;

	/** The admin. */
	@Column(name = "Admin")
	private boolean admin;
	
	/** The umd unique id. */
	@Column(name = "UMDUniqueId")
	private String umdUniqueId;
	
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
	 * Gets the role.
	 *
	 * @return the role
	 */
	public Role getRole() {
		return role;
	}

	/**
	 * Sets the role.
	 *
	 * @param role the new role
	 */
	public void setRole(Role role) {
		this.role = role;
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
	 * Gets the email.
	 *
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets the email.
	 *
	 * @param email the new email
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Checks if is inactive.
	 *
	 * @return true, if is inactive
	 */
	public boolean isInactive() {
		return inactive;
	}

	/**
	 * Sets the inactive.
	 *
	 * @param inactive the new inactive
	 */
	public void setInactive(boolean inactive) {
		this.inactive = inactive;
	}

	/**
	 * Checks if is admin.
	 *
	 * @return true, if is admin
	 */
	public boolean isAdmin() {
		return admin;
	}

	/**
	 * Sets the admin.
	 *
	 * @param admin the new admin
	 */
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	/**
	 * Gets the umd unique id.
	 *
	 * @return the umd unique id
	 */
	public String getUmdUniqueId() {
		return umdUniqueId;
	}

	/**
	 * Sets the umd unique id.
	 *
	 * @param umdUniqueId the new umd unique id
	 */
	public void setUmdUniqueId(String umdUniqueId) {
		this.umdUniqueId = umdUniqueId;
	}
	
	

}
