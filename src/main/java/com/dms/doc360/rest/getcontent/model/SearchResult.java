/**
 * Jun 4, 2015
 */
package com.dms.doc360.rest.getcontent.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * This class is be used to represent search result.
 * 
 * @author Tarun Verma
 * @param T
 */
@ToString
@NoArgsConstructor
@Getter
@Setter
public class SearchResult implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * true if more records are left, otherwise false.
	 */
	private boolean moreRecordsLeft;

	/**
	 * If provided, it can be used for getting next set of result using this
	 * value.
	 */
	private int startRow;

	/**
	 * Total number of records returned in hit list
	 */
	private long totalRecords;

	/**
	 * Pagination scroll id. If provided, then its value can be used to get next
	 * set of result.
	 */
	private String scrollId;

	/**
	 * List of records in hit list.
	 */
	private transient List<Document> recordsList = Collections.EMPTY_LIST;

}
