package gov.utah.dts.openid.service;

import gov.utah.dts.openid.repository.MemberRepository;
import gov.utah.dts.openid.model.Member;
import org.springframework.stereotype.Service;

import java.util.List;


// TODO: Auto-generated Javadoc
/**
 * The Class MemberServiceImpl.
 */

@Service
public class MemberServiceImpl implements MemberService {

	/** The member repository. */
	private MemberRepository memberRepository;
	
	/* (non-Javadoc)
	 * @see gov.utah.lfa.service.MemberService#getMembers()
	 */
	public List<Member> getMembers() {
		//return memberRepository.findAll();
		return null;
	}
	
}
