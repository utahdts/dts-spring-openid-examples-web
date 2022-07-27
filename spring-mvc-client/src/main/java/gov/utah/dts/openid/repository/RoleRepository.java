package gov.utah.dts.openid.repository;
import gov.utah.dts.openid.model.Role;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {

	/**
	 * Find by name.
	 *
	 * @param name the name
	 * @return the role
	 */
	Role findByName(String name);
	

}
