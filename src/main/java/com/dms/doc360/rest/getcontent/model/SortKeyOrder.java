/**
 * Jun 4, 2015
 */
package com.dms.doc360.rest.getcontent.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * This class is used to define sort key and their order.
 * 
 * @author Tarun Verma
 *
 */
@ToString
@NoArgsConstructor
@Getter
@Setter
public class SortKeyOrder implements Serializable {
	private static final long serialVersionUID = 1L;

	private String key;
	private String order;
}
