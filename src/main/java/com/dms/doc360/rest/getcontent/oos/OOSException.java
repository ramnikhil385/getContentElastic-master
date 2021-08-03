package com.dms.doc360.rest.getcontent.oos;

/**
 * Exception for Object Storage related issues.
 * 
 * @author cloudSDK, Sudheer Rangaboina
 *
 */
public class OOSException extends RuntimeException {

	private static final long serialVersionUID = -987991492884005033L;

	/**
	 * @param message
	 */
	public OOSException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public OOSException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public OOSException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public OOSException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
