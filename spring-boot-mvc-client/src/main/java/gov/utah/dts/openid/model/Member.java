package gov.utah.dts.openid.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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

	/** The umd unique id. */
	@Column(name = "UtahId")
	private String utahId;
	
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
	 * Gets the umd unique id.
	 *
	 * @return the umd unique id
	 */
	public String getUtahId() {
		return utahId;
	}

	/**
	 * Sets the umd unique id.
	 *
	 * @param utahId the new umd unique id
	 */
	public void setUtahId(String utahId) {
		this.utahId = utahId;
	}
	
	

}