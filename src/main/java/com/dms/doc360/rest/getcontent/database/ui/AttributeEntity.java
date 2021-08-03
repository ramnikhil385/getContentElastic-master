/**
 * 
 */
package com.dms.doc360.rest.getcontent.database.ui;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * Entity class to represent doc class attribute.
 * 
 * @author Tarun Verma
 *
 */
@Entity
@Table(name = "r_doc_cls_attr")
@Data
public class AttributeEntity {
	@Id
	@Column(name = "r_doc_cls_attr_id")
	Integer docClassAttributeId;

	@Column(name = "doc_cls_ui_attr_phys_nm")
	String docClassAttributePhysicalName;

	@Column(name = "doc_cls_ui_attr_label_nm")
	String docClassAttributeLabelName;

	@Column(name = "r_doc_cls_id")
	Integer docClassId;

	@Column(name = "doc_cls_ui_attr_alias1")
	String docClassAttributeAlias1;

	@Column(name = "doc_cls_ui_attr_alias2")
	String docClassAttributeAlias2;

	@Column(name = "doc_cls_ui_attr_legacy_nm")
	String docClassAttributeLegacyLabelName;

	@Column(name = "doc_cls_ui_attr_conv_fmt")
	String docClassAttributeConversionFormat;

	@Column(name = "doc_cls_ui_attr_priority")
	String docClassAttributePriority;

	@Column(name = "sort_numeric_ind")
	String docClassAttributeSortNumericInd;

	// @Column(name = "doc_cls_ui_attr_fed_alias")
	// String docClassAttributeFederateSearchAlias;

	@Column(name = "wild_card_ind_deflt")
	String docClassAttributeWildCardIndicator;

	@Column(name = "wild_card_min_len_reqd")
	String docClassAttributeWildCardMinLengthRequired;

}
