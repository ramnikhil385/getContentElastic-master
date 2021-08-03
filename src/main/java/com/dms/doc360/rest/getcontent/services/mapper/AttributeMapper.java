/**
 * 
 */
package com.dms.doc360.rest.getcontent.services.mapper;

import org.springframework.stereotype.Service;

import com.dms.doc360.rest.getcontent.database.ui.AttributeEntity;
import com.dms.doc360.rest.getcontent.model.Attribute;

/**
 * Mapper service for converting AttributeEntity entity bean to Attribute domain
 * bean.
 * 
 * @author Tarun Verma
 *
 */
@Service
public class AttributeMapper implements Mapper<AttributeEntity, Attribute> {

	/**
	 * Map entity bean to domain bean.
	 * 
	 * @see com.optum.dms.doc360.rest.types.services.mapper.Mapper#map(java.lang.Object)
	 */
	@Override
	public Attribute map(AttributeEntity input) {
		if (input == null) {
			return null;
		}

		Attribute docClsAttribute = new Attribute();
		docClsAttribute.setId(input.getDocClassAttributeId());
		docClsAttribute.setAlias1(input.getDocClassAttributeAlias1());
		docClsAttribute.setAlias2(input.getDocClassAttributeAlias2());
		docClsAttribute.setConversionFormat(input.getDocClassAttributeConversionFormat());
		// docClsAttribute.setFederateSearchAlias(input.getDocClassAttributeFederateSearchAlias());
		docClsAttribute.setLabelName(input.getDocClassAttributeLabelName());
		docClsAttribute.setLegacyLabelName(input.getDocClassAttributeLegacyLabelName());
		docClsAttribute.setPhysicalName(input.getDocClassAttributePhysicalName());
		docClsAttribute.setPriority(input.getDocClassAttributePriority());
		docClsAttribute.setSortNumericInd(input.getDocClassAttributeSortNumericInd());
		docClsAttribute.setWildCardIndicator(input.getDocClassAttributeWildCardIndicator());
		docClsAttribute.setWildCardMinLengthRequired(input.getDocClassAttributeWildCardMinLengthRequired());
		docClsAttribute.setClassId(input.getDocClassId());
		return docClsAttribute;
	}

}
