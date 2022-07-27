package gov.utah.dts.openid.service;

import gov.utah.dts.openid.model.DatabaseUser;

public interface UserService {
	/**
	 * Used by SecurityUserDetailService so no security
	 * @param uid is a string
	 * @return UserInfo object
	 */
	DatabaseUser findByUid(String uid);
}
