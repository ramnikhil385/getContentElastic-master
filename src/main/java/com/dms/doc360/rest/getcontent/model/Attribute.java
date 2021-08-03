package com.dms.doc360.rest.getcontent.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Domain class for Doc class attribute.
 * 
 * @author Sudheer Rangaboina, Tarun Verma
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@NoArgsConstructor
@Getter
@Setter
public class Attribute implements Serializable {
	private static final long serialVersionUID = 1L;

	private int classId;
	private int id;
	private String alias1;
	private String alias2;
	private String conversionFormat;
	private String federateSearchAlias;
	private String labelName;
	private String wildCardIndicator;
	private String wildCardMinLengthRequired;
	private String legacyLabelName;
	private String physicalName;
	private String priority;
	private String sortNumericInd;

}
