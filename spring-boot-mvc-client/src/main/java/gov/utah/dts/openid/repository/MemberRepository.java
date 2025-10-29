package gov.utah.dts.openid.repository;

import java.util.List;

import gov.utah.dts.openid.model.Member;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

// TODO: Auto-generated Javadoc
/**
 * The Interface MemberRepository.
 */

@Repository
public interface MemberRepository extends CrudRepository<Member, Long> {

	/**
	 * Find by name.
	 *
	 * @param name the name
	 * @return the member
	 */
	Member findByName(String name);

	/**
	 * Find by email and inactive.
	 *
	 * @param email the email
	 * @param inactive the inactive
	 * @return the member
	 */
	@Query("select m from Member m where upper(m.email)=upper(:email) and inactive = :inactive ")
	Member findByEmailAndInactive(@Param("email") String email, @Param("inactive") Boolean inactive);

	@Query("select m from Member m where m.name like :name and m.inactive = :inactive")
	List<Member> findByNameAndInactive(@Param("name") String name, @Param("inactive") Boolean inactive);
	
	@Query("select m from Member m where upper(m.email)=upper(:email) ")
	Member findByEmail(@Param("email") String email);
	
//	@Query("select m from Member m where m.id=:id ")
//	Member findById(@Param("id") Long id);

	@Query("select m from Member m where m.inactive = false")
	List<Member> getActiveMembers();

	@Query("select m from Member m where m.inactive = true")
	List<Member> getInactiveMembers();
}