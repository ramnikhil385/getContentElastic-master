package com.dms.doc360.rest.getcontent.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Domain class for Doc Class preference.
 * 
 * @author Sudheer Rangaboina
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@NoArgsConstructor
@Getter
@Setter
public class DocumentClassPreference implements Serializable {
	private static final long serialVersionUID = 1L;

	private int maxItems;
	private boolean reprint;
	private boolean download;
	private String docInputType;
	private String docOutputType;
	private String internalHostName;
	private String externalHostName;
	private String epsHostName;
	private String hostName;
	private String farmIndexSuffix;
	private String rioIndexSuffix;
	private String boneyardIndexSuffix;
	private boolean farmSearchDocvalues;
	private boolean rioSearchDocvalues;
	private boolean boneyardSearchDocvalues;
	private String rioAltHost;
	private String createDocumentWaitTime;
	private boolean enablePdfUAFlag;
	private boolean enableContentInZip;
}
