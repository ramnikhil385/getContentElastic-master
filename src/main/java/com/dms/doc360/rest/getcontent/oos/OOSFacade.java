package com.dms.doc360.rest.getcontent.oos;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.dms.doc360.rest.getcontent.model.Document;
import com.dms.doc360.rest.getcontent.oos.config.OOSConfig;
import com.dms.doc360.rest.getcontent.utils.Doc360Constants;
import com.dms.doc360.rest.getcontent.utils.Doc360Util;

import lombok.extern.slf4j.Slf4j;

/**
 * This class is used to work with Object Storage on Amazon S3
 * 
 * @author cloudSDK, Sudheer Rangaboina, Tarun Verma
 *
 */
@Slf4j
@Service(value = "oosFacade")
public class OOSFacade implements OOS {

	/**
	 * Define different type of clients
	 * 
	 * @author Tarun Verma
	 *
	 */
	public enum S3Client {
		CONTAINER, VAULT_PRIMARY, VAULT_SECONDARY
	};

	@Autowired
	private AmazonS3 s3ContainerClient;

	@Autowired
	private AmazonS3 s3VaultPrimaryClient;

	@Autowired
	private AmazonS3 s3VaultSecondaryClient;

	@Autowired
	private OOSConfig oosConfig;

	@Autowired
	private Doc360Util doc360Util;

	/**
	 * Store the object in the given bucket.
	 * 
	 * @see com.optum.edms.doc360.apibridge.ooss.OOS#putObject(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public void putObject(S3Client clientType, String bucketName, String objectName, String pathName) {
		/*
		 * Commenting for sonar as this method is not being called
		 * getS3Client(clientType).putObject(bucketName, objectName, new
		 * File(pathName));
		 */
	}

	/**
	 * Read the object from the given bucket.
	 * 
	 * @see com.optum.edms.doc360.apibridge.ooss.OOS#getObject(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public Document getObject(S3Client clientType, String bucketName, String objectName, String totalPageCount,
			Optional<Integer> startPage, Optional<Integer> endPage) throws IOException {

		try {
			log.debug("Getting content from object storage. Client Type: {}, Bucket: {}, Object: {}", clientType,
					bucketName, objectName);
			Document document = new Document();
			// prepare the request
			GetObjectRequest request = new GetObjectRequest(bucketName, objectName);

			// get the object from storage
			S3Object s3Object = getS3Client(clientType).getObject(request);

			// gather the metadata information
			ObjectMetadata objectMetaData = s3Object.getObjectMetadata();

			// get the file name
			String fileName = s3Object.getKey();
			if (fileName.contains(Doc360Constants.FORWARD_SLASH)) {
				fileName = StringUtils.substringAfterLast(fileName, "/");
			}

			long contentLength = objectMetaData.getContentLength();
			String mimeType = objectMetaData.getContentType();
			log.info("Document content returned from s3Client. File: {}; Content Length: {}; MIME: {}", fileName,
					contentLength, mimeType);

			// calculate the start and end page number based on total pages,
			// offset and length values
			int totalPages = doc360Util.getTotalPages(totalPageCount);
			int startPageNumber = doc360Util.getStartPageNumber(startPage);
			int endPageNumber = doc360Util.getEndPageNumber(totalPages, startPageNumber, endPage);
			log.info("Getting content for pages; Total Pages: {}, Start Page#: {}, End Page#: {}", totalPages,
					startPageNumber, endPageNumber);
			// return the content stream based on page range or whole document
			document.setContentSize(contentLength);
			document.setMimeType(mimeType);
			document.setObjectName(fileName);
			document.setContentStream(doc360Util.getContentForPageRange("Boneyard/OOS", s3Object.getObjectContent(),
					fileName, contentLength, mimeType, totalPages, startPageNumber, endPageNumber));
			return document;
		} catch (Exception e) {
			log.error("Exception occurred while getting the content from Object Storage. Client Type: " + clientType
					+ "; Bucket: " + bucketName + "; Object: " + objectName, e);
			throw new IOException("Exception occurred while getting the content from Object Storage. Client Type: "
					+ clientType + "; Bucket: " + bucketName + "; Object: " + objectName, e);
		}
	}

	/**
	 * Read the object from the given bucket.
	 * 
	 * @see com.optum.edms.doc360.apibridge.ooss.OOS#getObject(java.lang.String)
	 */
	@Override
	public Document getObject(String urlPath, String totalPageCount, Optional<Integer> startPage,
			Optional<Integer> endPage) throws IOException {
		// url path must be provided
		if (StringUtils.isBlank(urlPath)) {
			throw new RuntimeException("No urlpath found to retrive object content data. urlPath: " + urlPath);
		}

		// get the sections separated by /
		String[] arr = urlPath.split(Doc360Constants.FORWARD_SLASH);
		// it must be compatible with Container or Vault mode path
		if (arr == null || arr.length < 5) {
			throw new RuntimeException("Invalid url path: " + urlPath);
		}

		// if end point indicates Container Mode
		// e.g.
		//
		// https://s3api-core.uhc.com/u_rr_ent_jnl/4C797427-FF1C-42EF-AE67-583F7E8751B3.txt
		if (StringUtils.startsWithIgnoreCase(urlPath, oosConfig.getContainerEndpoint())) {
			return getObject(OOSFacade.S3Client.CONTAINER, arr[3], arr[4], totalPageCount, startPage, endPage);
		}
		// if end point indicates Vault Mode
		if (StringUtils.startsWithIgnoreCase(urlPath, oosConfig.getVaultEndpoint()) && arr.length == 7) {
			// get the object from Primary Bucket
			// e.g.
			//
			// https://ooss.uhc.com/edms-boneyard-prd/Archive/u_keyed_claim/FFFF9040-79AB-4968-AD5D-0C02EF9D1636.txt
			if (StringUtils.equals(arr[3], oosConfig.getVaultPrimaryBucketName())) {
				return getObject(OOSFacade.S3Client.VAULT_PRIMARY, arr[3],
						arr[4] + Doc360Constants.FORWARD_SLASH + arr[5] + Doc360Constants.FORWARD_SLASH + arr[6],
						totalPageCount, startPage, endPage);
			}

			// get the object from Secondary Bucket
			// e.g.
			//
			// https://ooss.uhc.com/edms-boneyard-prd-noindex/Archive/u_keyed_claim/FFFF9040-79AB-4968-AD5D-0C02EF9D1636.txt
			if (StringUtils.equals(arr[3], oosConfig.getVaultSecondaryBucketName())) {
				return getObject(OOSFacade.S3Client.VAULT_SECONDARY, arr[3],
						arr[4] + Doc360Constants.FORWARD_SLASH + arr[5] + Doc360Constants.FORWARD_SLASH + arr[6],
						totalPageCount, startPage, endPage);
			}
		}
		// if unable to meet any criteria, then it's unknown url path format
		throw new RuntimeException("Unknown url path for object storage. Value: " + urlPath);
	}

	/**
	 * Delete the object from the given bucket.
	 * 
	 * @see com.optum.edms.doc360.apibridge.ooss.OOS#deleteObject(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public void deleteObject(S3Client clientType, String bucketName, String objectName) {

		getS3Client(clientType).deleteObject(bucketName, objectName);
	}

	/**
	 * List all the objects from given bucket.
	 * 
	 * @see com.optum.edms.doc360.apibridge.ooss.OOS#listObjects(java.lang.String)
	 */
	@Override
	public List<S3ObjectSummary> listObjects(S3Client clientType, String bucketName) {

		ObjectListing listing = getS3Client(clientType).listObjects(bucketName);
		return listing.getObjectSummaries();
	}

	/**
	 * List all the buckets in the object storage.
	 * 
	 * @see com.optum.edms.doc360.apibridge.ooss.OOS#listBuckets()
	 */
	@Override
	public List<Bucket> listBuckets(S3Client clientType) {
		return getS3Client(clientType).listBuckets();
	}

	/**
	 * Get the AmazonS3 client based on the type
	 * 
	 * @param clientType
	 * @return AmazonS3
	 */
	private AmazonS3 getS3Client(S3Client clientType) {
		switch (clientType) {
		case CONTAINER:
			return s3ContainerClient;
		case VAULT_PRIMARY:
			return s3VaultPrimaryClient;
		case VAULT_SECONDARY:
		default:
			return s3VaultSecondaryClient;
		}
	}
}
