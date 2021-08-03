/**
 * 
 */
package com.dms.doc360.rest.getcontent.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception to indicate application not found error.
 * 
 * @author Tarun Verma
 *
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "ApplicationEntity not found.")
public class ApplicationNotFoundException extends Doc360ApplicationException {

	private static final long serialVersionUID = 1L;

	/**
	 * @param arg0
	 */
	public ApplicationNotFoundException(String arg0) {
		super(arg0);

	}

	/**
	 * @param arg0
	 */
	public ApplicationNotFoundException(Throwable arg0) {
		super(arg0);

	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public ApplicationNotFoundException(String arg0, Throwable arg1) {
		super(arg0, arg1);

	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public ApplicationNotFoundException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);

	}

}
