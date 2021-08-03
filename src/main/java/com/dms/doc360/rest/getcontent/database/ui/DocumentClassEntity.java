package com.dms.doc360.rest.getcontent.database.ui;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * Entity class for the doc class table.
 * 
 * @author Tarun Verma
 *
 */
@Entity
@Table(name = "r_doc_cls")
@Data
public class DocumentClassEntity {
	@Id
	@Column(name = "r_doc_cls_id")
	Integer docClassId;

	@Column(name = "doc_cls_name")
	String docClassName;

	@Column(name = "preferences")
	String preferences;

	@Column(name = "doc_cls_desc")
	String docClassDescription;

	@Column(name = "doc_cls_abrv")
	String docClassAbbreviation;

	@Column(name = "doc_cls_sys_repo_cd")
	String docClassSourceCode;
	
	@Column(name = "full_txt_srch_ind")
	boolean fullTextSearchIndicator;
	
	@Column(name = "multi_key_doc_ind")
	boolean multiKeyDocumentIndicator;
	
	
}
