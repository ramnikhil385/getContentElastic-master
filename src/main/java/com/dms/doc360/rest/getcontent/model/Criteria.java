/**
 * 
 */
package com.dms.doc360.rest.getcontent.model;

import java.io.Serializable;
import java.util.ArrayList;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * This class will provide all the search criteria for basic search.
 * 
 * @author Tarun Verma
 *
 */
@ToString
@NoArgsConstructor
@Getter
@Setter
public class Criteria implements Serializable {
	private static final long serialVersionUID = 1L;

	private ArrayList<Clause> mustClauses;
	private ArrayList<Clause> mustNotClauses;
	private ArrayList<Clause> filterClauses;
	private ArrayList<Clause> shouldClauses;
}
