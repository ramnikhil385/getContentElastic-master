/**
 * 
 */
package com.dms.doc360.rest.getcontent.controller.rest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.dms.doc360.rest.getcontent.exception.DocumentContentException;
import com.dms.doc360.rest.getcontent.exception.InvalidValueException;
import com.dms.doc360.rest.getcontent.model.Document;
import com.dms.doc360.rest.getcontent.model.GetContentSearchRequest;
import com.dms.doc360.rest.getcontent.security.ApplicationClaims;
import com.dms.doc360.rest.getcontent.services.Doc360ContentService;
import com.dms.doc360.rest.getcontent.utils.Doc360Constants;
import com.dms.doc360.rest.getcontent.utils.Doc360Util;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * This will provide rest end points for Get Content related functionality.
 * 
 * @author Tarun Verma
 *
 */
@Slf4j
@RestController
@RequestMapping(path = "/api/${rest.api.version}/types/{typeName}/document", produces = {
		MediaType.APPLICATION_JSON_VALUE })
public class GetContentRestController implements Doc360Constants {

	@Autowired
	private Doc360Util doc360Util;

	@Autowired
	private Doc360ContentService doc360ContentService;

	/**
	 * Get the content of the document based on provided id.
	 * 
	 * Method = GET
	 * 
	 * @param typeName
	 * @param documentId
	 *            r_object_id of the document
	 * @param sourceSystem
	 *            Optional value of source system: E, R or B
	 * @param startPage
	 *            Optional Page Range download; value >= 1
	 * @param endPage
	 *            Optional Page Range download; value <= total pages
	 * @return ResponseEntity<StreamingResponseBody>
	 */
	@ApiOperation(value = "Get Document Content for the given id", tags = { "Get Content" })
	@ApiImplicitParams(value = {
			@ApiImplicitParam(name = "JWT", value = "Authorization token", required = true, dataType = "string", paramType = "header"),
			@ApiImplicitParam(name = "clientTransactionId", value = "Client Transaction Id", required = false, dataType = "string", paramType = "header") })
	@GetMapping("/{documentId}/content")
	public Callable<ResponseEntity<StreamingResponseBody>> getDocumentContent(@PathVariable("typeName") String typeName,
			@PathVariable("documentId") String documentId, @RequestParam("sourceSystem") Optional<String> sourceSystem,
			@RequestParam("startPage") Optional<Integer> startPage, @RequestParam("endPage") Optional<Integer> endPage,
			final HttpServletRequest request, final HttpServletResponse response) {
		return new Callable<ResponseEntity<StreamingResponseBody>>() {
			@Override
			public ResponseEntity<StreamingResponseBody> call() throws Exception {

				String clientTransactionId = request.getHeader(REQUEST_HEADER_CLIENT_TRANSACTION_ID);
				log.info(
						"Getting content of Document using REST service, Type: {}; Object Id: {}; Source System: {}; Uuid: {}; Start Page: {}; End Page: {}",
						typeName, documentId, sourceSystem, clientTransactionId, startPage, endPage);
				// check for input values
				if (StringUtils.isBlank(typeName)) {
					throw new InvalidValueException("Type name must be provided to get content.");
				}
				if (StringUtils.isBlank(documentId)) {
					throw new InvalidValueException("Document Id must be provided to get content.");
				}
				if (sourceSystem.isPresent() && StringUtils.isNotBlank(sourceSystem.get())
						&& !Doc360Constants.RIO.equals(sourceSystem.get())
						&& !Doc360Constants.BONEYARD.equals(sourceSystem.get())
						&& !Doc360Constants.FARM.equals(sourceSystem.get())) {
					throw new InvalidValueException(
							"Invalid value for Source System. Only R, F or B is allowed for this value.");
				}

				// get application claims
				ApplicationClaims applicationClaims = (ApplicationClaims) request
						.getAttribute(Doc360Constants.REQUEST_ATTRIBUTE_DOC360_JWT_APPLICATION_CLAIMS);
				if (applicationClaims == null) {
					throw new SecurityException(
							"Unauthorized access. No application claims were found in request attribute.");
				}

				// get the doc using service
				Document document = doc360ContentService.getDocumentContent(typeName, documentId, sourceSystem,
						startPage, endPage, applicationClaims, response);
				// throw the error
				if (document == null) {
					throw new DocumentContentException("Document Not Found for Type: " + typeName + "; " + documentId);
				}
				log.info("Viewing {} Document Content; Object Id: {}; Size: {}", typeName, documentId,
						document.getContentSize());

				// prepare the request header based using content type and file
				// name
				HttpHeaders headers = new HttpHeaders();
				try {
					headers.setContentType(MediaType.parseMediaType(document.getMimeType()));
				} catch (InvalidMediaTypeException imte) {
					log.error("Unknown media type on content: {}; setting to binary mime type.",
							document.getMimeType());
					headers.setContentType(MediaType.parseMediaType(BINARY_CONTENT_TYPE));
				}
				// String fileName = document.getObjectName() + DOT +
				// document.getContentFileExtension();
				String fileName = document.getObjectName();
				doc360Util.setHeadersToDisableCache(headers, fileName);

				// Stream the content back to the client.
				final InputStream inputStream = document.getContentStream();

				// return the streaming response back
				ResponseEntity<StreamingResponseBody> response = new ResponseEntity<StreamingResponseBody>(
						(outputStream) -> {
							try {
								int contentSize = FileCopyUtils.copy(inputStream, outputStream);
								log.info("Completed viewing {} Document Content; Object Id: {}; Size: {}", typeName,
										documentId, contentSize);
							} catch (IOException ioe) {
								throw new DocumentContentException(
										"Unable to get Document Content for Type: " + typeName + "; " + documentId);
							}
						}, headers, HttpStatus.OK);
				return response;
			}
		};
	}

	/**
	 * Get document content based on the unique document found on given
	 * criteria. If given criteria returns more than one document, then return
	 * error message.
	 * 
	 * Method = POST
	 * 
	 * @param typeName
	 * @param getContentSearchRequest
	 *            Find criteria to locate one document
	 * @param sourceSystem
	 *            Optional value of source system: E, R or B
	 * @param startPage
	 *            Optional Page Range download; value >= 1
	 * @param endPage
	 *            Optional Page Range download; value <= total pages
	 * @return ResponseEntity<StreamingResponseBody>
	 */
	@ApiOperation(value = "Get Document Content for the unique document for the given criteria", tags = {
			"Get Content based on criteria" })
	@ApiImplicitParams(value = {
			@ApiImplicitParam(name = "JWT", value = "Authorization token", required = true, dataType = "string", paramType = "header") })
	@PostMapping(value = "/content/read")
	public Callable<ResponseEntity<StreamingResponseBody>> getDocumentContentAfterFind(
			@PathVariable("typeName") String typeName, @RequestBody GetContentSearchRequest getContentSearchRequest,
			@RequestParam("sourceSystem") Optional<String> sourceSystem,
			@RequestParam("startPage") Optional<Integer> startPage, @RequestParam("endPage") Optional<Integer> endPage,
			final HttpServletRequest request, final HttpServletResponse response) {

		return new Callable<ResponseEntity<StreamingResponseBody>>() {
			@Override
			public ResponseEntity<StreamingResponseBody> call() throws Exception {
				log.info(
						"Performing GetContent after Find using REST service, Type: {} Source System: {} Start Page: {} End Page: {} Criteria: {}",
						typeName, sourceSystem, startPage, endPage, getContentSearchRequest);

				// check for input values
				if (StringUtils.isBlank(typeName)) {
					throw new InvalidValueException("Type name must be provided to get content.");
				}
				if (sourceSystem.isPresent() && StringUtils.isNotBlank(sourceSystem.get())
						&& !Doc360Constants.RIO.equals(sourceSystem.get())
						&& !Doc360Constants.BONEYARD.equals(sourceSystem.get())
						&& !Doc360Constants.FARM.equals(sourceSystem.get())) {
					throw new InvalidValueException(
							"Invalid value for Source System. Only R, F or B is allowed for this value.");
				}

				// get application claims
				ApplicationClaims applicationClaims = (ApplicationClaims) request
						.getAttribute(Doc360Constants.REQUEST_ATTRIBUTE_DOC360_JWT_APPLICATION_CLAIMS);
				if (applicationClaims == null) {
					throw new SecurityException(
							"Unauthorized access. No application claims were found in request attribute.");
				}

				// get the doc using service
				Document document = doc360ContentService.getDocumentContentAfterFind(typeName, getContentSearchRequest,
						sourceSystem, startPage, endPage, applicationClaims, request, response);
				// throw the error
				if (document == null) {
					throw new DocumentContentException(
							"Document Not Found for Type: " + typeName + "; Criteria: " + getContentSearchRequest);
				}
				log.info("Viewing {} Document Content; Criteria: {}; Size: {}", typeName, getContentSearchRequest,
						document.getContentSize());

				// prepare the request header based using content type and file
				// name
				HttpHeaders headers = new HttpHeaders();
				try {
					headers.setContentType(MediaType.parseMediaType(document.getMimeType()));
				} catch (InvalidMediaTypeException imte) {
					log.error("Unknown media type on content: {}; setting to binary mime type.",
							document.getMimeType());
					headers.setContentType(MediaType.parseMediaType(BINARY_CONTENT_TYPE));
				}
				// String fileName = document.getObjectName() + DOT +
				// document.getContentFileExtension();
				String fileName = document.getObjectName();
				doc360Util.setHeadersToDisableCache(headers, fileName);

				// Stream the content back to the client.
				final InputStream inputStream = document.getContentStream();

				// return the streaming response back
				ResponseEntity<StreamingResponseBody> response = new ResponseEntity<StreamingResponseBody>(
						(outputStream) -> {
							try {
								int contentSize = FileCopyUtils.copy(inputStream, outputStream);
								log.info(
										"Completed viewing {} Document Content after Find operation; Criteria: {}; Size: {}",
										typeName, getContentSearchRequest, contentSize);
							} catch (IOException ioe) {
								throw new DocumentContentException("Unable to get Document Content for Type: "
										+ typeName + "; " + getContentSearchRequest);
							}
						}, headers, HttpStatus.OK);
				return response;
			}
		};
	}

}
