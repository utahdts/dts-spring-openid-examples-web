package gov.utah.dts.openid.repository;
import gov.utah.dts.openid.model.Audit;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditRepository extends CrudRepository<Audit, Long> {

	
	/**
	 * Find all sorted.
	 *
	 * @return the list
	 */
	@Query(" select a from Audit a  order by auditWhen desc ")
	List<Audit> findAllSorted();
	

}
