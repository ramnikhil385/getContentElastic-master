/**
 * 
 */
package com.dms.doc360.rest.getcontent.database.ui;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository inteface for ApplicationEntity entity.
 * 
 * @author Tarun Verma
 *
 */
public interface ApplicationRepo extends CrudRepository<ApplicationEntity, Integer> {
	/**
	 * Find the application based on app id
	 * 
	 * @param appId
	 * @return ApplicationEntity
	 */
	@Cacheable("applications")
	ApplicationEntity findByAppId(String appId);

	/**
	 * Get all the applications.
	 * 
	 * @see org.springframework.data.repository.CrudRepository#findAll()
	 */
	@Cacheable("applications")
	Iterable<ApplicationEntity> findAll();

}
