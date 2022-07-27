package gov.utah.dts.openid.service;

/**
 * The Interface SecurityUtil.
 */
public interface SecurityUtil {

	 /**
 	 * Gets the active user.
 	 *
 	 * @return the active user
 	 */
 	public AppUserDetails getActiveUser();

	 /**
 	 * Checks for role.
 	 *
 	 * @param role the role
 	 * @param userDetails the user details
 	 * @return true, if successful
 	 */
 	public  boolean hasRole(String role, AppUserDetails userDetails);

}
