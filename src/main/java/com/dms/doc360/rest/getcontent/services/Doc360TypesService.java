/**
 * 
 */
package com.dms.doc360.rest.getcontent.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dms.doc360.rest.getcontent.database.ui.ApplicationRepo;
import com.dms.doc360.rest.getcontent.database.ui.AttributeEntity;
import com.dms.doc360.rest.getcontent.database.ui.AttributeRepo;
import com.dms.doc360.rest.getcontent.database.ui.DocumentClassEntity;
import com.dms.doc360.rest.getcontent.database.ui.DocumentClassRepo;
import com.dms.doc360.rest.getcontent.model.Application;
import com.dms.doc360.rest.getcontent.model.Attribute;
import com.dms.doc360.rest.getcontent.model.DocumentClass;
import com.dms.doc360.rest.getcontent.services.mapper.ApplicationMapper;
import com.dms.doc360.rest.getcontent.services.mapper.AttributeMapper;
import com.dms.doc360.rest.getcontent.services.mapper.DocumentClassMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * The service to provide access to Doc360 Types and their attributes data.
 * 
 * @author Tarun Verma
 *
 */
@Slf4j
@Service
public class Doc360TypesService {

	@Autowired
	private DocumentClassRepo documentClassRepo;

	@Autowired
	private AttributeRepo attributeRepo;

	@Autowired
	private ApplicationRepo applicationRepo;

	@Autowired
	private ApplicationMapper applicationMapper;

	@Autowired
	private DocumentClassMapper documentClassMapper;

	@Autowired
	private AttributeMapper attributeMapper;

	/**
	 * Find the application by app id.
	 * 
	 * @param appId
	 * @return Application
	 */
	public Application findByApplicationId(String appId) {
		return applicationMapper.map(applicationRepo.findByAppId(appId));

	}

	/**
	 * Find the doc class based on class name.
	 * 
	 * @param docClassName
	 * @return DocumentClass
	 */
	public DocumentClass findByDocClassName(String docClassName) {
		return documentClassMapper.map(documentClassRepo.findByDocClassName(docClassName));
	}

	/**
	 * Find the doc class based on class id.
	 * 
	 * @param docClassId
	 * @return DocumentClass
	 */
	public DocumentClass findByDocClassId(int docClassId) {
		Optional<DocumentClassEntity> documentClassEntity = documentClassRepo.findById(docClassId);
		return documentClassEntity.isPresent() ? documentClassMapper.map(documentClassEntity.get()) : null;
	}

	/**
	 * Find the doc class attributes based on class id.
	 * 
	 * @param docClassId
	 * @return List<Attribute>
	 */
	public List<Attribute> findAttributesByDocClassId(Integer docClassId) {
		List<Attribute> attributes = null;
		// get the list from db first, then map to domain beans
		List<AttributeEntity> attributeList = attributeRepo.findByDocClassId(docClassId);
		if (CollectionUtils.isNotEmpty(attributeList)) {
			attributes = attributeList.stream().map(input -> attributeMapper.map(input)).collect(Collectors.toList());
		}
		return attributes;
	}

	/**
	 * Find the doc class attributes based on class name.
	 * 
	 * @param docClassName
	 * @return List<Attribute>
	 */
	public List<Attribute> findAttributesByDocClassName(String docClassName) {
		DocumentClassEntity documentClassEntity = documentClassRepo.findByDocClassName(docClassName);
		if (documentClassEntity != null && documentClassEntity.getDocClassId() != null) {
			return findAttributesByDocClassId(documentClassEntity.getDocClassId());
		} else {
			return null;
		}
	}

	/**
	 * Find the doc class attribute based on attribute id.
	 * 
	 * @param docClassAttributeId
	 * @return Attribute
	 */
	public Attribute findByDocClassAttributeId(Integer docClassAttributeId) {
		return attributeMapper.map(attributeRepo.findByDocClassAttributeId(docClassAttributeId));
	}

	/**
	 * Get all the doc classes.
	 * 
	 * @return List<DocumentClass>
	 */
	public List<DocumentClass> getAllDocClassses() {
		List<DocumentClass> docClasses = new ArrayList<DocumentClass>();
		for (Iterator<DocumentClassEntity> it = documentClassRepo.findAll().iterator(); it.hasNext();) {
			docClasses.add(documentClassMapper.map(it.next()));
		}
		return docClasses;
	}
}
