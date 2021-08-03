/**
 * Created on: Jun 18, 2015
 */
package com.dms.doc360.rest.getcontent.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This class is used to throw any error related document search errors.
 * 
 * @author Tarun Verma
 *
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Unable to search documents.")
public class DocumentSearchException extends Doc360ApplicationException {

	private static final long serialVersionUID = 1L;

	/**
	 * @param arg0
	 */
	public DocumentSearchException(String arg0) {
		super(arg0);

	}

	/**
	 * @param arg0
	 */
	public DocumentSearchException(Throwable arg0) {
		super(arg0);

	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public DocumentSearchException(String arg0, Throwable arg1) {
		super(arg0, arg1);

	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public DocumentSearchException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);

	}

}
