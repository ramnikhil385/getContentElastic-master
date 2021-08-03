/**
 * 
 */
package com.dms.doc360.rest.getcontent.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.Ostermiller.util.CircularByteBuffer;
import com.dms.doc360.rest.getcontent.exception.ApplicationNotFoundException;
import com.dms.doc360.rest.getcontent.exception.DocumentTypeNotFoundException;
import com.dms.doc360.rest.getcontent.model.Application;
import com.dms.doc360.rest.getcontent.model.ApplicationPreference;
import com.dms.doc360.rest.getcontent.model.DocumentClass;
import com.dms.doc360.rest.getcontent.services.Doc360TypesService;
import com.documentum.fc.client.internal.ISysObjectInternal;
import com.documentum.fc.common.DfException;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;

/**
 * Utility bean for this application.
 * 
 * @author Tarun Verma
 *
 */
@Slf4j
@Component
public class Doc360Util implements Doc360Constants {

	// thread-safe instance of date formatter - yyyy-MM-dd'T'HH:mm:ss'.'SSS'Z'
	private final FastDateFormat ISO_DATE_FORMATTER = FastDateFormat.getInstance(ISO_DATE_FORMAT,
			TimeZone.getTimeZone(ZoneOffset.UTC));

	// thread-safe instance of date formatter - MM/dd/yyyy
	private final FastDateFormat MMDDYYYY_DATE_FORMATTER = FastDateFormat.getInstance(DATE_FORMAT_MMDDYYYY,
			TimeZone.getTimeZone(ZoneOffset.UTC));

	// thread-safe instance of date formatter - yyyy-MM-dd
	private final FastDateFormat YYYYMMDD_DATE_FORMATTER = FastDateFormat.getInstance(DATE_FORMAT_YYYYMMDD,
			TimeZone.getTimeZone(ZoneOffset.UTC));

	// thread-safe instance of date formatter - yyyyMMdd'T'HHmmssSSS
	private final FastDateFormat YYYYMMDDHHMISS_DATE_FORMATTER = FastDateFormat
			.getInstance(DATE_FORMAT_YYYYMMDDHH24MISS, TimeZone.getTimeZone(ZoneOffset.UTC));

	private String scheme;
	private String serverName;
	private int serverPort = -1;

	@Autowired
	private Doc360TypesService doc360TypesService;

	/**
	 * Convert date into MM/dd/yyyy format.
	 * 
	 * @param inValue
	 * @return String
	 */
	public String convertDateToMMDDYYYY(Date inValue) {
		// check for empty string first
		if (inValue == null) {
			return EMPTY_STRING;
		}

		return MMDDYYYY_DATE_FORMATTER.format(inValue);
	}

	/**
	 * Convert date into YYYY-MM-dd format.
	 * 
	 * @param inValue
	 * @return String
	 */
	public String convertDateToYYYYMMDD(Date inValue) {
		// check for empty string first
		if (inValue == null) {
			return EMPTY_STRING;
		}

		return YYYYMMDD_DATE_FORMATTER.format(inValue);
	}

	/**
	 * Convert date in yyyy-MM-dd'T'HH:mm:ss.SSSZ ISO String format.
	 * 
	 * @param inValue
	 * @return Date
	 */
	public Date convertISOToDate(String inValue) {
		// check for empty string first
		if (StringUtils.isBlank(inValue)) {
			return null;
		}

		try {
			return ISO_DATE_FORMATTER.parse(inValue.trim().replace(DOUBLE_QUOTES, EMPTY_STRING));
		} catch (ParseException pe) {
			log.error("Error while parsing ISO date.", pe);
			return null;
		}
	}

	/**
	 * Convert date into yyyy-MM-dd'T'HH:mm:ss.SSSZ ISO String format.
	 * 
	 * @param inValue
	 * @return String
	 */
	public String convertDateToISO(Date inValue) {
		// check for empty string first
		if (inValue == null) {
			return EMPTY_STRING;
		}

		return ISO_DATE_FORMATTER.format(inValue);
	}

	/**
	 * Get current date in MM/dd/yyyy format.
	 * 
	 * @return String
	 */
	public String getCurrentDateInMMDDYYYYString() {
		return convertDateToMMDDYYYY(new Date());
	}

	/**
	 * Get current date in ISO String format.
	 * 
	 * @return String
	 */
	public String getCurrentDateInISOString() {
		return convertDateToISO(new Date());
	}

	/**
	 * Get current date in Date Time format without separator.
	 * 
	 * @return String
	 */
	public String getCurrentDateInDateTimeString() {
		return YYYYMMDDHHMISS_DATE_FORMATTER.format(new Date());
	}

	/**
	 * Generate the Nonce value.
	 * 
	 * @return String
	 */
	public String generateNonce() {
		String dateTimeString = Long.toString(new Date().getTime());
		byte[] nonceByte = dateTimeString.getBytes();
		return Base64.encodeBase64String(nonceByte);
	}

	/**
	 * Build the password digest using nonce, created date string and app id
	 * password.
	 * 
	 * @param nonce
	 * @param created
	 * @param password
	 * @return String
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public String buildPasswordDigest(String nonce, String created, String password)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest sha2 = MessageDigest.getInstance(ALGORITHM_SHA_256);
		sha2.update(nonce.getBytes(CHARSET_UTF_8));
		sha2.update(created.getBytes(CHARSET_UTF_8));
		sha2.update(password.getBytes(CHARSET_UTF_8));
		String passwordDigest = new String(Base64.encodeBase64(sha2.digest()));
		// log.debug("Password Digest with SHA2: {}", passwordDigest);
		sha2.reset();

		return passwordDigest;
	}

	/**
	 * Set the headers to disable cache.
	 * 
	 * @param headers
	 * @param fileName
	 */
	public void setHeadersToDisableCache(HttpHeaders headers, String fileName) {
		headers.setContentDispositionFormData(fileName, fileName);
		headers.setCacheControl(HTTP_HEADER_CACHE_CONTROL);
		headers.setExpires(0);
		headers.setPragma(HTTP_HEADER_PRAGMA);
		headers.setIfModifiedSince(0);
	}

	/**
	 * Set the headers to disable cache.
	 * 
	 * @param headers
	 * @param fileName
	 */
	public void setHeadersToDisableCache(HttpServletResponse response, String fileName) {
		StringBuilder builder = new StringBuilder("form-data; name=\"");
		builder.append(fileName).append('\"');
		builder.append("; filename=\"");
		builder.append(fileName).append('\"');
		response.setHeader(CONTENT_DISPOSITION, builder.toString());
		response.setHeader(EXPIRES, HTTP_HEADER_DATE_ZERO_OFFSET_VALUE);
		response.setHeader(CACHE_CONTROL, HTTP_HEADER_CACHE_CONTROL);
		response.setHeader(PRAGMA, HTTP_HEADER_PRAGMA);
		response.setHeader(IF_MODIFIED_SINCE, HTTP_HEADER_DATE_ZERO_OFFSET_VALUE);
	}

	/**
	 * Get the remote user's IP address.
	 * 
	 * @return String
	 */
	public String getRequestRemoteAddr() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getRequest();
		return request.getRemoteAddr();
	}

	/**
	 * Get the scheme name, e.g. http or https.
	 * 
	 * @return String
	 */
	public String getScheme() {
		if (scheme == null) {
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
					.getRequest();
			scheme = request.getScheme();
		}
		return scheme;
	}

	/**
	 * Get the server name.
	 * 
	 * @return String
	 */
	public String getServerName() {
		if (serverName == null) {
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
					.getRequest();
			serverName = request.getServerName();
		}
		return serverName;
	}

	/**
	 * Get the server port.
	 * 
	 * @return int
	 */
	public int getServerPort() {
		if (serverPort == -1) {
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
					.getRequest();
			serverPort = request.getServerPort();
		}
		return serverPort;
	}

	/**
	 * Utility method to copy input stream into byte array.
	 * 
	 * @param in
	 * @return byte[]
	 * @throws IOException
	 */
	private byte[] copyStream(InputStream in) throws IOException {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			StreamUtils.copy(in, baos);
			return baos.toByteArray();
		}
	}

	/**
	 * Get the index suffix for any doc class based on source system.
	 * 
	 * @param docCls
	 * @param sourceSystem
	 * @return String
	 */
	public String getIndexSuffix(DocumentClass docCls, String sourceSystem) {
		// get the index suffix based on source system
		String indexSuffix = null;
		switch (sourceSystem) {
		case Doc360Constants.SOURCE_SYSTEM_FARM:
			indexSuffix = docCls.getPreference().getFarmIndexSuffix();
			break;
		case Doc360Constants.SOURCE_SYSTEM_RIO:
			indexSuffix = docCls.getPreference().getRioIndexSuffix();
			break;
		case Doc360Constants.SOURCE_SYSTEM_BONEYARD:
			indexSuffix = docCls.getPreference().getBoneyardIndexSuffix();
			break;
		}

		// get the value configured otherwise return blank string
		return StringUtils.isBlank(indexSuffix) ? Doc360Constants.EMPTY_STRING : indexSuffix;
	}

	/**
	 * Get the content stream for a specific page range of the PDF document, or
	 * all pages of all types of documents.
	 * 
	 * @param sourceSystem
	 * @param contentInputStream
	 * @param fileName
	 * @param contentLength
	 * @param mimeType
	 * @param totalPages
	 * @param startPageNumber
	 * @param endPageNumber
	 * @return InputStream
	 * @throws IOException
	 */
	public InputStream getContentForPageRange(String sourceSystem, InputStream contentInputStream, String fileName,
			long contentLength, String mimeType, int totalPages, int startPageNumber, int endPageNumber)
			throws IOException {
		// get page range content only for PDF now
		// and only if page range doesn't indicate the full document
		if (Doc360Constants.MIME_TYPE_PDF.equalsIgnoreCase(mimeType)) {
			if ((endPageNumber - startPageNumber + 1) != totalPages) {
				// create circular byte buffer to create connected input and
				// output stream
				// where output stream can be used to generate content, and
				// input stream could be returned back to the client which can
				// stream the content back
				CircularByteBuffer cbb = new CircularByteBuffer(1024 * 1024);

				// submit new thread for content extraction
				ThreadPoolTaskExecutor taskExecutor = ApplicationContextProvider.getApplicationContext()
						.getBean(ThreadPoolTaskExecutor.class);
				taskExecutor.submit(() -> {
					// created buffered stream for output generation
					try (OutputStream os = cbb.getOutputStream();
							BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(os)) {
						// create pdf reader for the content
						PdfReader reader = new PdfReader(new BufferedInputStream(contentInputStream));
						// Instantiate iText document object.
						com.itextpdf.text.Document pdfDocument = new com.itextpdf.text.Document();
						// Use the output stream to generate the pdf
						// content
						// for required page range
						PdfCopy copy = new PdfCopy(pdfDocument, bufferedOutputStream);
						pdfDocument.open();
						// get only the desired pages
						for (int pageCounter = startPageNumber; pageCounter <= endPageNumber; pageCounter++) {
							log.debug("Adding page " + pageCounter + " to stream...");
							copy.addPage(copy.getImportedPage(reader, pageCounter));
						}

						// release the reader
						copy.freeReader(reader);
						log.debug("About to close document object...");
						// Writes the trailer for the PDF.
						pdfDocument.close();
						log.debug("PDF document extracted!");
						// close the copy
						copy.close();
					} catch (IOException | DocumentException ioe) {
						throw new RuntimeException(
								"Error while getting the content for specific page range. Source System : "
										+ sourceSystem + "; Start Page: " + startPageNumber + "; End Page: "
										+ endPageNumber,
								ioe);
					}
				});

				// submit the content extraction in new thread
				// return the content stream
				return cbb.getInputStream();
			} else {
				// Provide all the pages of the document
				// return the content stream for all pages
				return contentInputStream;
			}
		}

		// if it's a text document
		if (Doc360Constants.MIME_TYPE_TEXT.equalsIgnoreCase(mimeType)) {

			// means it's a Text document
			// read the content first
			String textDocumentContent = new String(StreamUtils.copyToByteArray(contentInputStream));
			boolean windowsEndOfLineMatches = textDocumentContent.contains(IOUtils.LINE_SEPARATOR_WINDOWS);
			// if content doesn't have End of Line characters for Windows
			// then replace Unix EOL with Windows ones
			if (!windowsEndOfLineMatches) {
				log.debug("Text document contains Windows end of line characters: {}", windowsEndOfLineMatches);
				textDocumentContent = StringUtils.replace(textDocumentContent, IOUtils.LINE_SEPARATOR_UNIX,
						IOUtils.LINE_SEPARATOR_WINDOWS);
			}

			// determine the pages count
			int textPagesCount = StringUtils.countMatches(textDocumentContent, Doc360Constants.CHARACTER_FORM_FEED) + 1;
			log.info("For file {} and source system {}, total pages: {}", fileName, sourceSystem, textPagesCount);
			// if only partial pages are needed, and start/end page values are
			// under total pages count
			if ((endPageNumber - startPageNumber + 1) != textPagesCount && startPageNumber <= textPagesCount
					&& endPageNumber <= textPagesCount) {
				log.debug("Getting page ranges for text documents; page range {} to {}", startPageNumber,
						endPageNumber);

				// determine the search index in the document based on page
				// range
				int startSearchIndex = ((startPageNumber - 1) == 0) ? 0
						: (StringUtils.ordinalIndexOf(textDocumentContent, Doc360Constants.PAGE_BREAK_SEARCH_STRING,
								startPageNumber - 1)) + 1;

				String pageRangeContent =
						// if end page was equal to total pages, we just need to
						// locate the start index for start page
						((endPageNumber == textPagesCount)
								? StringUtils.substring(textDocumentContent, startSearchIndex) :
								// determine the content for page range
								StringUtils.substring(textDocumentContent, startSearchIndex, StringUtils.ordinalIndexOf(
										textDocumentContent, Doc360Constants.PAGE_BREAK_SEARCH_STRING, endPageNumber)));
				// return the content stream for specific page range
				return new ByteArrayInputStream(pageRangeContent.getBytes());
			} else {
				// return the content stream for all pages
				return new ByteArrayInputStream(textDocumentContent.getBytes());
			}
		} else {
			// return the content stream as it is
			return contentInputStream;
		}
	}

	/**
	 * Utility method recursively traverse zip file and get content file
	 * 
	 * @param internalObj
	 * @return
	 * @throws IOException
	 * @throws DfException
	 */
	public InputStream getContentStreamFromZip(ISysObjectInternal internalObj) throws IOException, DfException {
		ZipInputStream zis = new ZipInputStream(internalObj.getStream(null, 0, null, false));
		ZipEntry ze = zis.getNextEntry();
		while (ze != null && ze.getName().endsWith(FILE_EXTENSION_ZIP)) {
			zis = new ZipInputStream(new ByteArrayInputStream(copyStream(zis)));
			ze = zis.getNextEntry();
		}
		// In case input stream is original content file, So return fresh input
		// stream
		if (ze == null) {
			return internalObj.getStream(null, 0, null, false);
		}

		// return the zip input stream, which is set to the entry
		// to provide content.
		return zis;
	}

	/**
	 * Get the Document type id by its value.
	 * 
	 * @param docType
	 * @return int Legacy document type from its api
	 */
	public int getDocumentTypeIdByName(String docType) {

		switch (docType) {
		case "TEXT":
			return 1;
		case "HTML":
			return 2;
		case "PDF":
			return 3;
		default:
			return 1;
		}
	}

	/**
	 * Get the start page number based on offset value
	 * 
	 * @param totalPages
	 *            Total pages count for document
	 * @param startPageNumber
	 *            resolved starting page number; must be >= 1
	 * @param length
	 *            Total number of pages requested by user; must be less than <=
	 *            total pages
	 * @return int Must be greater than or equal to 1
	 */
	public int getEndPageNumber(int totalPages, int startPageNumber, Optional<Integer> endPage) {
		// ensure start page number is correct
		startPageNumber = (startPageNumber < Doc360Constants.DEFAULT_PAGE_NO) ? Doc360Constants.DEFAULT_PAGE_NO
				: startPageNumber;
		// calculate end page number
		int endPageNumber = (endPage.isPresent()) ? endPage.get() : (startPageNumber + totalPages - 1);

		// ensure page range doesn't exceed than total pages
		if (endPageNumber > totalPages) {
			endPageNumber = totalPages;
		}
		// return the last page number index
		return endPageNumber;
	}

	/**
	 * Get the file extension based on doc type value of legacy env.
	 * 
	 * @param docType
	 * @return String
	 */
	public String getFileExtension(int docType) {
		return (docType == 3) ? FILE_EXTENSION_PDF : FILE_EXTENSION_TXT;
	}

	/**
	 * Get the MIME type based on the doc type.
	 * 
	 * @param docType
	 * @return String
	 */
	public String getMimeType(int docType) {
		return (docType == 3) ? MIME_TYPE_PDF : MIME_TYPE_TEXT;
	}

	/**
	 * Return the repository id value with suffix setup for that source system,
	 * if already not present.
	 * 
	 * @param repositoryId
	 * @return String
	 */
	public String getRepositoryIdWithSuffix(String sourceSystem, String repositoryId) {
		if (StringUtils.isNotBlank(repositoryId) && !repositoryId.trim().endsWith(Doc360Constants.WILDCARD)) {
			// trim the value
			String indexName = repositoryId.trim();
			// if passed value doesn't end with wildcard, then add it and return
			// resolve the doc class name using repository id
			// add the type with suffix setup for the source system
			indexName += getIndexSuffix(getRDocCls(indexName), sourceSystem);
			return indexName;
		}
		return repositoryId;
	}

	/**
	 * Get the start page number based on offset value
	 * 
	 * @param offset
	 * @return int Must be greater than or equal to 1
	 */
	public int getStartPageNumber(Optional<Integer> startPage) {

		int pageNo = (startPage.isPresent()) ? startPage.get() : Doc360Constants.DEFAULT_PAGE_NO;
		if (pageNo < Doc360Constants.DEFAULT_PAGE_NO) {
			pageNo = Doc360Constants.DEFAULT_PAGE_NO;
		}
		return pageNo;
	}

	/**
	 * Get the total number of pages count.
	 * 
	 * @param totalPageCount
	 * @return int
	 */
	public int getTotalPages(String totalPageCount) {
		int pageNo = Doc360Constants.DEFAULT_PAGE_NO;
		try {
			pageNo = Integer.parseInt(totalPageCount);
		} catch (Exception e) {
			log.warn("Error while converting the total pages; will use default page size as 1");
		}
		if (pageNo < Doc360Constants.DEFAULT_PAGE_NO) {
			pageNo = Doc360Constants.DEFAULT_PAGE_NO;
		}
		return pageNo;
	}

	/**
	 * Utility method recursively traverse zip file and get final zip input
	 * stream.
	 * 
	 * @param inputStream
	 * @return ZipInputStream The stream is set to get the content for document
	 * @throws IOException
	 */
	public ZipInputStream getZipInputStream(InputStream inputStream) throws IOException {
		ZipInputStream zis = new ZipInputStream(inputStream);
		ZipEntry ze = zis.getNextEntry();
		while (ze != null && ze.getName().endsWith(FILE_EXTENSION_ZIP)) {
			zis = new ZipInputStream(new ByteArrayInputStream(copyStream(zis)));
			ze = zis.getNextEntry();
		}

		// In case input stream is original content file, return null
		if (ze == null) {
			return null;
		}

		// return the zip input stream, which is set to the entry
		// to provide content.
		return zis;
	}
	
	/**
	 * Utility method to get first zip input stream.
	 * 
	 * @param inputStream
	 * @return InputStream The stream is set to get the content for document
	 * @throws IOException
	 */
	public InputStream getFirstZipInputStream(InputStream inputStream) throws IOException {
		ZipInputStream zis = new ZipInputStream(inputStream);
		ZipEntry ze = zis.getNextEntry();
		if (ze != null && StringUtils.endsWithIgnoreCase(ze.getName(), FILE_EXTENSION_ZIP)) {
			return new ByteArrayInputStream(copyStream(zis));
		} else {
			return null;
		}
	}

	/**
	 * Check whether source should be enabled for any doc class based on source
	 * system.
	 * 
	 * @param docCls
	 * @param sourceSystem
	 * @return boolean
	 */
	public boolean isSourceEnabledInSearchRequest(DocumentClass docCls, String sourceSystem) {
		// check whether source filtering should be enabled based on source
		// system
		switch (sourceSystem) {
		case Doc360Constants.SOURCE_SYSTEM_FARM:
			return !docCls.getPreference().isFarmSearchDocvalues();
		case Doc360Constants.SOURCE_SYSTEM_RIO:
			return !docCls.getPreference().isRioSearchDocvalues();
		case Doc360Constants.SOURCE_SYSTEM_BONEYARD:
			return !docCls.getPreference().isBoneyardSearchDocvalues();
		default:
			return true;
		}

	}

	/**
	 * Check whether source should be enabled for any doc class based on source
	 * system.
	 * 
	 * @param docClassName
	 * @param sourceSystem
	 * @return boolean
	 */
	public boolean isSourceEnabledInSearchRequest(String docClassName, String sourceSystem) {
		// check whether source filtering should be enabled based on source
		// system
		return isSourceEnabledInSearchRequest(getRDocCls(docClassName), sourceSystem);
	}

	/**
	 * Get the application data bean for the given id.
	 * 
	 * @param appId
	 * @return Application
	 */
	public Application getAppDataBean(String appId) {

		Application appDataBean = doc360TypesService.findByApplicationId(appId);
		if (appDataBean == null) {
			log.error(String.format("Invalid AppId found. please check configuration for appId: %s", appId));
			throw new ApplicationNotFoundException(
					String.format("Internal Error: Error while fetching appId preference for appID: %s", appId));
		} else {
			return appDataBean;
		}
	}

	/**
	 * Get the application preference for the given id.
	 * 
	 * @param appId
	 * @return AppIdPreference
	 */
	public ApplicationPreference getAppIdPreference(String appId) {

		Application appDataBean = getAppDataBean(appId);
		if (appDataBean == null) {
			// log.error(String.format("Invalid AppId found. please check
			// configuration for appId: %s", appId));
			throw new ApplicationNotFoundException(
					String.format("Internal Error: Error while fetching appId preference for appID: %s", appId));
		} else {
			ApplicationPreference appIdPreference = appDataBean.getPreferences();
			if (appIdPreference == null) {
				// log.error(
				// String.format("appIdPreference is empty. please check
				// configuration for appId: %s", appId));
				throw new ApplicationNotFoundException(
						String.format("Internal Error: Error while fetching appId preference for appID: %s", appId));
			}
			return appIdPreference;
		}
	}

	/**
	 * Get RDocCls object based on passed doc class name
	 * 
	 * @param docClassName
	 * @return RDocCls
	 */
	public DocumentClass getRDocCls(String docClassName) {
		DocumentClass docCls = doc360TypesService.findByDocClassName(docClassName);
		if (docCls != null) {
			return docCls;
		} else {
			throw new DocumentTypeNotFoundException(String.format("%s docclass is invalid", docClassName));
		}
	}

}
