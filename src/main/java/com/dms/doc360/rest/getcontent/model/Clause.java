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
 * This class will provide the various clauses possible for criteria.
 * 
 * @author Tarun Verma
 *
 */
@ToString
@NoArgsConstructor
@Getter
@Setter
public class Clause implements Serializable {
	private static final long serialVersionUID = 1L;

	// indicates what type of clause this is
	// equal, range, prefix, wildcard, exists, nested
	private String type;
	private String name;
	private String value;
	private Range range;
	private Criteria criteria;
}
