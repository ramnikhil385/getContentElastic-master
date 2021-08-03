/**
 * Created on: Feb 13, 2017
 */
package com.dms.doc360.rest.getcontent.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This class is used to indicate invalid value related errors.
 * 
 * @author Tarun Verma
 *
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Invalid values were provided.")
public class InvalidValueException extends Doc360ApplicationException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor with message
	 * 
	 * @param message
	 */
	public InvalidValueException(String message) {
		super(message);

	}

	/**
	 * Constructor with cause.
	 * 
	 * @param cause
	 */
	public InvalidValueException(Throwable cause) {
		super(cause);

	}

	/**
	 * Constructor with message and cause.
	 * 
	 * @param message
	 * @param cause
	 */
	public InvalidValueException(String message, Throwable cause) {
		super(message, cause);

	}

	/**
	 * Constructor with message, cause and suppress related information.
	 * 
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public InvalidValueException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);

	}

}
