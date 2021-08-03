package com.dms.doc360.rest.getcontent.model;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * This class is used to represent Doc Class information.
 * 
 * @author Sudheer Rangaboina, Tarun Verma
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@NoArgsConstructor
@Getter
@Setter
public class DocumentClass implements Serializable {
	private static final long serialVersionUID = 1L;

	private int id;
	private String name;
	private String description;
	private String abbreviation;
	private String sourceCode;
	private DocumentClassPreference preference;
	private boolean multiKeyDocumentIndicator;
	private boolean fullTextSearchIndicator;
	private Map<String, Attribute> attributesMap;

}
