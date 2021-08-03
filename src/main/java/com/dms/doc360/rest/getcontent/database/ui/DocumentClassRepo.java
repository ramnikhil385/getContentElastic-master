package com.dms.doc360.rest.getcontent.database.ui;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;

/**
 * CRUD repository for the doc class table.
 * 
 * @author Tarun Verma
 *
 */
public interface DocumentClassRepo extends CrudRepository<DocumentClassEntity, Integer> {

	/**
	 * Find the doc class based on class name.
	 * 
	 * @param docClassName
	 * @return DocumentClassEntity
	 */
	@Cacheable("documentClasses")
	DocumentClassEntity findByDocClassName(String docClassName);

	/**
	 * Get all the doc classes.
	 * 
	 * @see org.springframework.data.repository.CrudRepository#findAll()
	 */
	@Cacheable("documentClasses")
	Iterable<DocumentClassEntity> findAll();
}
