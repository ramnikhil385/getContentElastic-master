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
 * This class will provide the range of values for condition.
 * 
 * @author Tarun Verma
 *
 */
@ToString
@NoArgsConstructor
@Getter
@Setter
public class Range implements Serializable {
	private static final long serialVersionUID = 1L;

	private String gt;
	private String gte;
	private String lt;
	private String lte;
}
