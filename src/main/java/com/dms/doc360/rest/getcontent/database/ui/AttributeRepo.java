/**
 * 
 */
package com.dms.doc360.rest.getcontent.database.ui;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;

/**
 * CRUD repository for doc class attribute table.
 * 
 * @author Tarun Verma
 *
 */
public interface AttributeRepo extends CrudRepository<AttributeEntity, Integer> {

	/**
	 * Find the doc class attributes based on class id.
	 * 
	 * @param docClassId
	 * @return List<AttributeEntity>
	 */
	@Cacheable("docClassAttributes")
	List<AttributeEntity> findByDocClassId(Integer docClassId);

	/**
	 * Find the doc class attribute based on attribute id.
	 * 
	 * @param docClassAttributeId
	 * @return AttributeEntity
	 */
	@Cacheable("attributes")
	AttributeEntity findByDocClassAttributeId(Integer docClassAttributeId);

}
