/**
 * Created on: Jun 23, 2015
 */
package com.dms.doc360.rest.getcontent.controller.rest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.dms.doc360.rest.getcontent.exception.ApplicationNotFoundException;
import com.dms.doc360.rest.getcontent.exception.AttributeNotFoundException;
import com.dms.doc360.rest.getcontent.exception.Doc360ApplicationException;
import com.dms.doc360.rest.getcontent.exception.DocumentContentException;
import com.dms.doc360.rest.getcontent.exception.DocumentSearchException;
import com.dms.doc360.rest.getcontent.exception.DocumentTypeNotFoundException;
import com.dms.doc360.rest.getcontent.exception.InvalidValueException;
import com.dms.doc360.rest.getcontent.exception.UniqueDocumentNotFoundException;
import com.dms.doc360.rest.getcontent.model.RestError;
import com.dms.doc360.rest.getcontent.utils.Doc360Constants;

import lombok.extern.slf4j.Slf4j;

/**
 * This class works as global rest controller exception handler for all types of
 * exceptions.
 * 
 * @author Tarun Verma
 *
 */
@Slf4j
@ControllerAdvice
public class GlobalRestControllerExceptionHandler implements Doc360Constants {

	/**
	 * Set up the exception handler for server exceptions.
	 * 
	 * @param req
	 * @param e
	 * @return ResponseEntity<RestError>
	 */
	@ExceptionHandler({ Doc360ApplicationException.class, Throwable.class })
	public ResponseEntity<RestError> handleServerException(HttpServletRequest req, Exception e) {
		log.error("Internal Server Error.", e);
		RestError error = new RestError(HttpStatus.INTERNAL_SERVER_ERROR, getMessageString(e), req.getRequestURI());
		return new ResponseEntity<RestError>(error, error.getStatus());
	}

	/**
	 * Set up the exception handler for bad request exception.
	 * 
	 * @param req
	 * @param e
	 * @return ResponseEntity<RestError>
	 */
	@ExceptionHandler({ DocumentContentException.class, DocumentSearchException.class, InvalidValueException.class })
	public ResponseEntity<RestError> handleBadRequestException(HttpServletRequest req, Exception e) {
		log.error("Bad Request Error.", e);
		RestError error = new RestError(HttpStatus.BAD_REQUEST, getMessageString(e), req.getRequestURI());
		return new ResponseEntity<RestError>(error, error.getStatus());
	}

	/**
	 * Set up the exception handler for data not found exceptions.
	 * 
	 * @param req
	 * @param e
	 * @return ResponseEntity<RestError>
	 */
	@ExceptionHandler({ DocumentTypeNotFoundException.class, AttributeNotFoundException.class,
			ApplicationNotFoundException.class })
	public ResponseEntity<RestError> handleNotFoundException(HttpServletRequest req, Exception e) {
		log.error("Data Not Found Error.", e);
		RestError error = new RestError(HttpStatus.NOT_FOUND, getMessageString(e), req.getRequestURI());
		return new ResponseEntity<RestError>(error, error.getStatus());
	}

	/**
	 * Set up the exception handler for unique document not found exception.
	 * 
	 * @param req
	 * @param e
	 * @return ResponseEntity<RestError>
	 */
	@ExceptionHandler({ UniqueDocumentNotFoundException.class })
	public ResponseEntity<RestError> handleUniqueDocumentNotFoundException(HttpServletRequest req, Exception e) {
		log.error("Unique Document Not Found Error.", e);
		RestError error = new RestError(HttpStatus.NOT_FOUND, getMessageString(e), req.getRequestURI());
		return new ResponseEntity<RestError>(error, error.getStatus());
	}

	/**
	 * Set up the exception handler for security exceptions.
	 * 
	 * @param req
	 * @param e
	 * @return ResponseEntity<RestError>
	 */
	@ExceptionHandler({ JWTVerificationException.class, SecurityException.class })
	public ResponseEntity<RestError> handleSecurityException(HttpServletRequest req, Exception e) {
		log.error("Security Error.", e);
		RestError error = new RestError(HttpStatus.UNAUTHORIZED, getMessageString(e), req.getRequestURI());
		return new ResponseEntity<RestError>(error, error.getStatus());
	}

	/**
	 * Get the message string from any exception.
	 * 
	 * @param th
	 * @return String
	 */
	private String getMessageString(Throwable th) {
		// get the message string, include nested exception's
		// string as well.
		if (th != null) {
			StringBuilder builder = new StringBuilder(th.toString());
			if (th.getCause() != null) {
				builder.append(" Cause: ");
				builder.append(th.getCause().getMessage());
			}
			return builder.toString();
		} else {
			return EMPTY_STRING;
		}
	}

}
