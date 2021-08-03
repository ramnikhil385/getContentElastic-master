/**
 * 
 */
package com.dms.doc360.rest.getcontent.services.mapper;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.dms.doc360.rest.getcontent.database.ui.ApplicationEntity;
import com.dms.doc360.rest.getcontent.exception.Doc360ApplicationException;
import com.dms.doc360.rest.getcontent.model.Application;
import com.dms.doc360.rest.getcontent.model.ApplicationPreference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Mapper bean to convert application entity bean to Application domain model.
 * 
 * @author Tarun Verma
 *
 */
@Service
public class ApplicationMapper implements Mapper<ApplicationEntity, Application> {

	/**
	 * Map the entity bean to domain bean.
	 * 
	 * @see com.optum.dms.doc360.rest.types.services.mapper.Mapper#map(java.lang.Object)
	 */
	@Override
	public Application map(ApplicationEntity input) {
		if (input == null) {
			return null;
		}
		
		Application application = new Application();
		application.setId(input.getId());
		application.setApplicationDescription(input.getApplicationDescription());
		application.setApplicationId(input.getAppId());
		application.setApplicationUserId(input.getApplicationUserId());
		application.setApplicationPassword(input.getApplicationPassword());
		
		// parse the json structure, and set in the domain object
		if (StringUtils.isNotBlank(input.getPreferences())) {
			try {
				ApplicationPreference preferences = new ObjectMapper().readValue(input.getPreferences(), ApplicationPreference.class);
				application.setPreferences(preferences);
			} catch (IOException e) {
				throw new Doc360ApplicationException("Error occurred while parsing application preferences JSON String.", e);
			}
		}
		return application;
	}

}
