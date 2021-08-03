/**
 * 
 */
package com.dms.doc360.rest.getcontent.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * This class will be used to get search request from user.
 * 
 * @author Tarun Verma
 *
 */
@ToString
@NoArgsConstructor
@Getter
@Setter
public class SearchRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * Min value is 1 if provided. Optional
	 */
	private int startRow;

	/**
	 * Max value is 10,000. If not provided, then 50 records are returned.
	 * Optional
	 */
	private int totalRecords;

	/**
	 * Pagination scroll id. If provided, then startRow and totalRecords values
	 * are ignored.
	 */
	private String scrollId;

	/**
	 * List of attributes to be selected. Optional. If not provided, then
	 * default list of attributes and returned.
	 */
	private List<String> selectAttributes;

	/**
	 * Basic index name for the search.
	 */
	private String indexName;

	/**
	 * Optional search criteria for locating records.
	 */
	private Criteria criteria;

	/**
	 * Optional sort order for the search result. If not provided, then default
	 * sorting order is used.
	 */
	private List<SortKeyOrder> sortKeys = new ArrayList<SortKeyOrder>();

	/**
	 * Optional client Transaction Id for logging purpose to track individual
	 * find operation.
	 */
	private String clientTransactionId;

}
