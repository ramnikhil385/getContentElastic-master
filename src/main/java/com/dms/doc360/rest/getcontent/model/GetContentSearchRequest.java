/**
 * 
 */
package com.dms.doc360.rest.getcontent.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * This class will be used to get search request for locating unique document.
 * 
 * @author Tarun Verma
 *
 */
@ToString
@NoArgsConstructor
@Getter
@Setter
public class GetContentSearchRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * Search criteria for locating records.
	 */
	private Criteria criteria;

	/**
	 * Optional client Transaction Id for logging purpose to track individual
	 * find operation.
	 */
	private String clientTransactionId;

}
