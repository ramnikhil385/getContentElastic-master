package com.dms.doc360.rest.getcontent.oos;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.dms.doc360.rest.getcontent.model.Document;
import com.dms.doc360.rest.getcontent.oos.OOSFacade.S3Client;

/**
 * This interface implements uploading, getting and deleting objects in Optum
 * Object Store
 * 
 * @author cloudSDK, Sudheer Rangaboina, Tarun Verma
 *
 */
public interface OOS {

	/**
	 * Store the object in the OOS store
	 * 
	 * @param clientType
	 * @param bucketName
	 * @param objectName
	 * @param pathName
	 */
	void putObject(S3Client clientType, String bucketName, String objectName, String pathName);

	/**
	 * Get the object from storage using bucket and name.
	 * 
	 * @param clientType
	 * @param bucketName
	 * @param objectName
	 * @param totalPageCount
	 * @param offset
	 * @param length
	 * @return ContentStream
	 * @throws IOException
	 */
	Document getObject(S3Client clientType, String bucketName, String objectName, String totalPageCount,
			Optional<Integer> startPage, Optional<Integer> endPage) throws IOException;

	/**
	 * Get the object from storage using the url path.
	 * 
	 * @param urlPath
	 * @param totalPageCount
	 * @param offset
	 * @param length
	 * @return ContentStream
	 * @throws IOException
	 */
	Document getObject(String urlPath, String totalPageCount, Optional<Integer> startPage, Optional<Integer> endPage)
			throws IOException;

	/**
	 * Delete the object from storage.
	 * 
	 * @param clientType
	 * @param bucketName
	 * @param objectName
	 */
	void deleteObject(S3Client clientType, String bucketName, String objectName);

	/**
	 * List all the object from a bucket.
	 * 
	 * @param clientType
	 * @param bucketName
	 * @return List<S3ObjectSummary>
	 */
	List<S3ObjectSummary> listObjects(S3Client clientType, String bucketName);

	/**
	 * List all the buckets.
	 * 
	 * @param clientType
	 * @return List<Bucket>
	 */
	List<Bucket> listBuckets(S3Client clientType);
}
