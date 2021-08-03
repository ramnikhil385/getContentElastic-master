/**
 * 
 */
package com.dms.doc360.rest.getcontent.services.mapper;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.dms.doc360.rest.getcontent.database.ui.DocumentClassEntity;
import com.dms.doc360.rest.getcontent.exception.Doc360ApplicationException;
import com.dms.doc360.rest.getcontent.model.DocumentClass;
import com.dms.doc360.rest.getcontent.model.DocumentClassPreference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Mapper service to convert DocumentClass entity bean to DocumentClass domain.
 * 
 * @author Tarun Verma
 *
 */
@Slf4j
@Service
public class DocumentClassMapper implements Mapper<DocumentClassEntity, DocumentClass> {

	/**
	 * Map be the DocumentClassEntity to DocumentClass bean.
	 * 
	 * @see com.optum.dms.doc360.rest.types.services.mapper.Mapper#map(java.lang.Object)
	 */
	@Override
	public DocumentClass map(DocumentClassEntity input) {
		if (input == null) {
			return null;
		}

		log.debug("Mapping doc class id {}, name {}", input.getDocClassId(), input.getDocClassName());
		DocumentClass docCls = new DocumentClass();
		docCls.setId(input.getDocClassId());
		docCls.setDescription(input.getDocClassDescription());
		docCls.setAbbreviation(input.getDocClassAbbreviation());
		docCls.setName(input.getDocClassName());
		docCls.setSourceCode(input.getDocClassSourceCode());
		docCls.setFullTextSearchIndicator(input.isFullTextSearchIndicator());
		docCls.setMultiKeyDocumentIndicator(input.isMultiKeyDocumentIndicator());
		// parse the json structure, and set in the domain object
		if (StringUtils.isNotBlank(input.getPreferences())) {
			try {
				DocumentClassPreference preferences = new ObjectMapper().readValue(input.getPreferences(),
						DocumentClassPreference.class);
				docCls.setPreference(preferences);
			} catch (IOException e) {
				throw new Doc360ApplicationException(
						"Error occurred while parsing document class preferences JSON String.", e);
			}
		}
		return docCls;
	}

}
