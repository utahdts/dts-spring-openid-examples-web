package gov.utah.dts.openid.service;

import gov.utah.dts.openid.model.Member;

import java.util.List;


// TODO: Auto-generated Javadoc
/**
 * The Interface MemberService.
 */
public interface MemberService {

	/**
	 * Gets the members.
	 *
	 * @return the members
	 */
	public abstract List<Member> getMembers();

}