package gov.utah.dts.openid.service;

import gov.utah.dts.openid.model.Audit;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

// TODO: Auto-generated Javadoc
/**
 * The Class SecurityUtilImpl.
 */

@Service
public class SecurityUtilImpl implements SecurityUtil{

	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(SecurityUtilImpl.class);

	/** The audit service. */
	@Autowired
	private AuditService auditService;
	
	/* (non-Javadoc)
	 * @see gov.utah.lfa.service.SecurityUtil#getActiveUser()
	 */
	public AppUserDetails getActiveUser(){
		SecurityContext context = SecurityContextHolder.getContext();
		Authentication authentication = context.getAuthentication();
		Object principal = authentication.getPrincipal();
		return (AppUserDetails) principal;
	}
	

	 
	 /* (non-Javadoc)
 	 * @see gov.utah.lfa.service.SecurityUtil#logout(javax.servlet.http.HttpServletRequest)
 	 */
 	public void logout(HttpServletRequest request){
		 	AppUserDetails user = getActiveUser();
		    auditService.createAuditRow(new Date(),user,"","MEMBER",user.getUsername(),"",auditService.getTransaction(),Audit.ACTION_LOGOUT,null,user.getId().toString());
			SecurityContextHolder.getContext().setAuthentication(null);
			request.getSession().invalidate();
			return;
	 }

	/* (non-Javadoc)
	 * @see gov.utah.lfa.service.SecurityUtil#hasRole(java.lang.String, gov.utah.lfa.service.AppUserDetails)
	 */
	public  boolean hasRole(String role, AppUserDetails userDetails) {
		    boolean hasRole = false;
		    if (userDetails != null) {
		      Collection<GrantedAuthority> authorities = userDetails.getAuthorities();
		      if (isRolePresent(authorities, role)) {
		        hasRole = true;
		      }
		    } 
		    return hasRole;
		  }

	/**
	 * Check if a role is present in the authorities of current user.
	 *
	 * @param authorities all authorities assigned to current user
	 * @param role required authority
	 * @return true if role is present in list of authorities assigned to current user, false otherwise
	 */
	  private boolean isRolePresent(Collection<GrantedAuthority> authorities, String role) {
	    boolean isRolePresent = false;
	    for (GrantedAuthority grantedAuthority : authorities) {
	      isRolePresent = grantedAuthority.getAuthority().equals(role);
	      if (isRolePresent) break;
	    }
	    return isRolePresent;
	  }

	 
	  
	
}
