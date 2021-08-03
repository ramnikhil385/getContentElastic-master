package com.dms.doc360.rest.getcontent.model;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Domain class to indicate the document.
 * 
 * @author Tarun Verma
 *
 */
@ToString
@NoArgsConstructor
@Getter
@Setter
public class Document implements Serializable {
	private static final long serialVersionUID = 1L;

	private String objectId;
	private String objectName;
	private String mimeType;
	private String contentType;
	private long contentSize;
	private String contentFileExtension;
	private int totalPages;
	private transient InputStream contentStream;
	private transient List<Attribute> attributes = new ArrayList<Attribute>();

}
