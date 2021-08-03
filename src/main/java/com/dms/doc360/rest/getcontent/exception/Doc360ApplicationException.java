/**
 * Created on: Jun 1, 2015
 */
package com.dms.doc360.rest.getcontent.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception class to be used in DFC Adapter layer. This would be a wrapper for
 * non-specified exceptions.
 * 
 * @author Tarun Verma
 *
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Error generated in DFC adapter layer.")
public class Doc360ApplicationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * @param arg0
	 */
	public Doc360ApplicationException(String arg0) {
		super(arg0);

	}

	/**
	 * @param arg0
	 */
	public Doc360ApplicationException(Throwable arg0) {
		super(arg0);

	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public Doc360ApplicationException(String arg0, Throwable arg1) {
		super(arg0, arg1);

	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public Doc360ApplicationException(String arg0, Throwable arg1, boolean arg2,
			boolean arg3) {
		super(arg0, arg1, arg2, arg3);

	}

}
