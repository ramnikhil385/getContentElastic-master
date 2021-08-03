/**
 * Jun 5, 2015
 */
package com.dms.doc360.rest.getcontent.services.mapper;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This class is used to report mapping related errors.
 * 
 * @author Tarun Verma
 *
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Unable to map attributes.")
public class MappingException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 */
	public MappingException() {
	}

	/**
	 * @param message
	 */
	public MappingException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public MappingException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MappingException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public MappingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
