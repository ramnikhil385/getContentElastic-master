/**
 * 
 */
package com.dms.doc360.rest.getcontent.services;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.document.DocumentField;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.dms.doc360.rest.getcontent.elasticsearch.config.FileServerConfig;
import com.dms.doc360.rest.getcontent.exception.Doc360ApplicationException;
import com.dms.doc360.rest.getcontent.exception.DocumentContentException;
import com.dms.doc360.rest.getcontent.exception.DocumentSearchException;
import com.dms.doc360.rest.getcontent.exception.UniqueDocumentNotFoundException;
import com.dms.doc360.rest.getcontent.model.Application;
import com.dms.doc360.rest.getcontent.model.Document;
import com.dms.doc360.rest.getcontent.model.DocumentClass;
import com.dms.doc360.rest.getcontent.model.DocumentClassPreference;
import com.dms.doc360.rest.getcontent.model.GetContentSearchRequest;
import com.dms.doc360.rest.getcontent.model.RestError;
import com.dms.doc360.rest.getcontent.model.SearchResult;
import com.dms.doc360.rest.getcontent.oos.OOSFacade;
import com.dms.doc360.rest.getcontent.oos.config.OOSConfig;
import com.dms.doc360.rest.getcontent.security.ApplicationClaims;
import com.dms.doc360.rest.getcontent.security.config.ApplicationSecurityConfig;
import com.dms.doc360.rest.getcontent.services.config.ApplicationServiceConfig;
import com.dms.doc360.rest.getcontent.utils.ContentTypeMapping;
import com.dms.doc360.rest.getcontent.utils.Doc360Constants;
import com.dms.doc360.rest.getcontent.utils.Doc360Util;
import com.dms.doc360.rest.getcontent.utils.TimeWatch;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * This class is responsible to get content for the given global doc id of the
 * class.
 * 
 * @author Tarun Verma
 *
 */
@Slf4j
@Service
public class Doc360ContentService implements Doc360Constants {

	@Autowired
	private Doc360Util doc360Util;

	@Autowired
	private OOSConfig oosConfig;

	@Autowired
	private RestClient farmRestClient;

	@Autowired
	private RestClient rioRestClient;

	@Autowired
	private RestClient rioAltTwoRestClient;

	@Autowired
	private RestClient rioAltThreeRestClient;

	@Autowired
	private RestClient boneyardRestClient;

	@Autowired
	private RestHighLevelClient rioRestHighLevelClient;

	@Autowired
	private RestHighLevelClient rioAltTwoRestHighLevelClient;

	@Autowired
	private RestHighLevelClient rioAltThreeRestHighLevelClient;

	@Autowired
	private RestHighLevelClient farmRestHighLevelClient;

	@Autowired
	private RestHighLevelClient boneyardRestHighLevelClient;

	@Autowired
	private SSLContext sslContext;

	@Autowired
	private FileServerConfig rioFileServerConfigConfig;

	@Autowired
	private FileServerConfig farmFileServerConfigConfig;

	@Autowired
	private FileServerConfig boneyardFileServerConfigConfig;

	// AWS Object Storage Client
	@Autowired
	private OOSFacade oosFacade;

	@Autowired
	private ContentTypeMapping contentTypeMapping;

	@Autowired
	private RestTemplate findRestTemplate;

	@Autowired
	private ApplicationSecurityConfig applicationSecurityConfig;

	@Autowired
	private ApplicationServiceConfig applicationServiceConfig;

	/**
	 * Get the document content for the given type name, document id and
	 * optional page range.
	 * 
	 * @param typeName
	 * @param objectId
	 * @param sourceSystem
	 * @param startPage
	 * @param endPage
	 * @param applicationClaims
	 * @param response
	 * @return Document with populated InputStream
	 */
	public Document getDocumentContent(String typeName, String objectId, Optional<String> sourceSystemOptional,
			Optional<Integer> startPage, Optional<Integer> endPage, ApplicationClaims applicationClaims,
			HttpServletResponse response) {
		String urlPath = null;
		String contentSize = null;
		String contentType = null;
		String pageCount = null;
		TimeWatch timeWatch = TimeWatch.start();
		// get the doc class name and application id from call context
		// and add the documentPdfUAFlag in the response header based on class
		// preferences
		Application appDataBean = doc360Util.getAppDataBean(applicationClaims.getAppId());
		String docClassName = typeName;
		DocumentClass docClass = doc360Util.getRDocCls(docClassName);
		String sourceSystem = sourceSystemOptional.isPresent() ? sourceSystemOptional.get() : docClass.getSourceCode();
		response.addHeader(RESPONSE_HEADER_DOCUMENT_PDF_UA_FLAG,
				Boolean.toString(docClass.getPreference().isEnablePdfUAFlag()));

		// capture the global doc id and specific index name from object id if
		// separated by underscore
		String globalDocId = StringUtils.substringBeforeLast(objectId, PIPE_SEPARATOR);
		String indexName = StringUtils.substringAfterLast(objectId, PIPE_SEPARATOR);
		boolean isAvailableSpecificIndexName = true;
		if (StringUtils.isBlank(indexName)) {
			indexName = typeName;
			isAvailableSpecificIndexName = false;
		}
		try {

			// Get the urlpath, content size and page count value from ES
			// based on situation.
			// use GET API if specific index name is available otherwise
			// use REQUEST API to search all index to find specific record
			if (isAvailableSpecificIndexName) {
				log.info("Getting document details based on specific index name: {}", indexName);
				// perform the elastic search to get the needed values
				Map<String, String> esResponseByDocID = getDocumentFromElasticSearch(applicationClaims, appDataBean,
						sourceSystem, typeName, indexName, globalDocId, ES_CONTENT_SIZE_IDENTIFIER,
						ES_URL_PATH_IDENTIFIER, ES_PAGE_COUNT_IDENTIFIER, ES_CONTENT_TYPE_IDENTIFIER);

				// if no record found then throw error
				if (esResponseByDocID == null || esResponseByDocID.size() == 0) {
					log.error("No document found in ES for request (repositoryId: " + indexName + " globalDocId: "
							+ globalDocId);
					throw new DocumentContentException("No document found in ES for request (repositoryId: " + indexName
							+ " globalDocId: " + globalDocId);
				}

				// get the contentSize, urlpath and page count
				urlPath = esResponseByDocID.get(ES_URL_PATH_IDENTIFIER);
				contentSize = esResponseByDocID.get(ES_CONTENT_SIZE_IDENTIFIER);
				pageCount = esResponseByDocID.get(ES_PAGE_COUNT_IDENTIFIER);
				contentType = esResponseByDocID.get(ES_CONTENT_TYPE_IDENTIFIER);

			} else {
				log.info("Getting document details by searching all indexes for doc class: {}", indexName);
				// prepare the criteria map based on object id
				HashMap<String, String> termMap = new HashMap<String, String>(1);
				termMap.put(ES_GLOBAL_DOC_ID_IDENTIFIER, globalDocId);

				// perform the elastic search to get the needed values
				List<Map<String, String>> esResponseByDocID = callElasticSearch(applicationClaims, appDataBean,
						sourceSystem, indexName, globalDocId, 1, termMap, ES_CONTENT_SIZE_IDENTIFIER,
						ES_URL_PATH_IDENTIFIER, ES_PAGE_COUNT_IDENTIFIER, ES_CONTENT_TYPE_IDENTIFIER);

				// if no record found then throw error
				if (CollectionUtils.isEmpty(esResponseByDocID)) {
					log.error("No document found in ES for request (repositoryId: " + indexName + " globalDocId: "
							+ globalDocId);
					throw new DocumentContentException("No document found in ES for request (repositoryId: " + indexName
							+ " globalDocId: " + globalDocId);
				}

				// get the contentSize, urlpath and page count
				urlPath = esResponseByDocID.get(0).get(ES_URL_PATH_IDENTIFIER);
				contentSize = esResponseByDocID.get(0).get(ES_CONTENT_SIZE_IDENTIFIER);
				pageCount = esResponseByDocID.get(0).get(ES_PAGE_COUNT_IDENTIFIER);
				contentType = esResponseByDocID.get(0).get(ES_CONTENT_TYPE_IDENTIFIER);
			}
			// use the lower case value
			if (contentType != null) {
				contentType = contentType.toLowerCase();
			}
			log.info("Url Path: {}, Content Size: {}, Content Type: {}, Page Count: {}", urlPath, contentSize,
					contentType, pageCount);

			// if urlpath is populated then get the content from FS directly
			if (StringUtils.isNotBlank(urlPath)) {
				// get the document based on urlpath type; OOS or FS
				return (isOOSPath(urlPath)) ? oosFacade.getObject(urlPath, pageCount, startPage, endPage)
						: getContent(docClass.getPreference(), sourceSystem, typeName, globalDocId, urlPath,
								contentSize, contentType, pageCount, startPage, endPage);
			} else {
				throw new DocumentContentException(
						"Unable to get the content. The urlpath is not available for the repositoryId: " + indexName
								+ "; object id: " + globalDocId);
			}
		} catch (ResponseException re) {
			log.error("Response exception in ES query operation inside getContentStream for the given repositoryId:"
					+ indexName + "; object id: " + globalDocId + "; urlpath: " + urlPath, re);
			throw new Doc360ApplicationException(
					"Response exception in ES query operation inside getContentStream for the given repositoryId:"
							+ indexName + "; object id: " + globalDocId + "; urlpath: " + urlPath,
					re);
		} catch (Doc360ApplicationException ex) {
			log.error("Error while getting the content for the given repositoryId: " + indexName + "; object id: "
					+ globalDocId + "; urlpath: " + urlPath, ex);
			throw ex;
		} catch (Exception ex) {
			log.error("Error while getting the content for the given repositoryId: " + indexName + "; object id: "
					+ globalDocId + "; urlpath: " + urlPath, ex);
			throw new Doc360ApplicationException("Error while getting the content for the given repositoryId: "
					+ indexName + "; object id: " + globalDocId + "; urlpath: " + urlPath, ex);
		} finally {
			log.info("{} Time taken for getContentStream operation repositoryId: {} objectId: {} is {} seconds ",
					getSourceSystemFullName(sourceSystem), indexName, globalDocId, timeWatch.time());
		}
	}

	/**
	 * Generic method to get list of response keys for the given repository and
	 * passed criteria.
	 *
	 * @param applicationClaims
	 * @param sourceSystem
	 * @param repositoryId
	 * @param objectId
	 * @param resultSize
	 * @param termMap
	 * @param responseKeys
	 * @return List<Map<String, String>>
	 * @throws IOException
	 *
	 * @author Tarun Verma
	 */
	private List<Map<String, String>> callElasticSearch(ApplicationClaims applicationClaims, Application appDataBean,
			String sourceSystem, String repositoryId, String objectId, int resultSize, Map<String, String> termMap,
			String... responseKeys) throws IOException {
		log.info("ES Query for repository {}, object id {} and criteria map {}", repositoryId, objectId, termMap);
		// prepare the security header for ES API
		RequestOptions requestOptions = getRequestOptionsWithSecurityHeader(applicationClaims, appDataBean);

		// use boolean query with filter and term clauses for criteria
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		termMap.entrySet().stream()
				.forEach(entry -> boolQueryBuilder.filter(QueryBuilders.termQuery(entry.getKey(), entry.getValue())));

		// use high level rest client to get the search hits.
		SearchRequest searchRequest = null;
		boolean enableSourceInSearchRequest = doc360Util.isSourceEnabledInSearchRequest(repositoryId, sourceSystem);
		if (enableSourceInSearchRequest) {
			// use source fields for result hits
			searchRequest = new SearchRequest(
					new String[] { doc360Util.getRepositoryIdWithSuffix(sourceSystem, repositoryId) },
					SearchSourceBuilder.searchSource().size(resultSize).fetchSource(responseKeys, null)
							.query(boolQueryBuilder).terminateAfter(resultSize)).types(Doc360Constants.ES_TYPE_DOCUMENT)
									.preference(Doc360Constants.ES_PREFERENCE_PRIMARY_FIRST);
		} else {
			// we need to use doc_values in this case here
			searchRequest = new SearchRequest(
					new String[] { doc360Util.getRepositoryIdWithSuffix(sourceSystem, repositoryId) },
					SearchSourceBuilder.searchSource().size(resultSize).fetchSource(false).query(boolQueryBuilder)
							.terminateAfter(resultSize)).types(Doc360Constants.ES_TYPE_DOCUMENT)
									.preference(Doc360Constants.ES_PREFERENCE_PRIMARY_FIRST);
			// set up the doc value fields
			for (String docValueFieldName : responseKeys) {
				searchRequest.source().docValueField(docValueFieldName, Doc360Constants.ES_DOCVALUES_DEFAULT_FORMAT);
			}
		}

		log.info("Search Request prepared for ES: {}", searchRequest);
		// perform the search and extract the response
		SearchResponse searchResponse = getRestHighLevelClient(sourceSystem, repositoryId).search(searchRequest,
				requestOptions);
		log.info("Completed the search for the records in time: {}", searchResponse.getTook());

		// check for execution failures
		// terminate after has been added for get content as we know only one
		// record
		// is available for this query
		// if (BooleanUtils.isTrue(searchResponse.isTerminatedEarly())) {
		// throw new DocumentContentException("The ES search has been terminated
		// early.
		// RepositoryId: " + repositoryId
		// + " globalDocId: " + objectId);
		// }
		if (searchResponse.isTimedOut()) {
			throw new DocumentContentException(
					"The ES search has timed out. RepositoryId: " + repositoryId + " globalDocId: " + objectId);
		}

		// parse the search result
		List<Map<String, String>> responseList = new ArrayList<Map<String, String>>();
		Map<String, String> responseMap = null;
		SearchHits hits = searchResponse.getHits();
		// make sure some result came back
		if (hits != null && hits.getTotalHits() > 0 && hits.getHits() != null) {
			// iterate through hits
			for (SearchHit hit : hits.getHits()) {
				String index = hit.getIndex();
				String type = hit.getType();
				String id = hit.getId();
				float score = hit.getScore();
				log.debug("Record index: {}; type: {}; id: {}; score: {}", index, type, id, score);

				// use source fields for result hits
				if (enableSourceInSearchRequest) {
					// get the fields map and capture what is needed
					Map<String, Object> sourceAsMap = hit.getSourceAsMap();
					// make sure _source was populated
					if (sourceAsMap != null) {
						// prepare map for key/value pair
						responseMap = new HashMap<String, String>();
						// get all the values for the asked keys
						for (String key : responseKeys) {
							responseMap.put(key, StringUtils
									.trimToEmpty(Objects.toString(sourceAsMap.get(key), Doc360Constants.EMPTY_STRING)));
						}
						responseList.add(responseMap);
					}
				} else {
					// get the fields map and capture what is needed
					Map<String, DocumentField> fieldsAsMap = hit.getFields();
					// make sure _source was populated
					if (fieldsAsMap != null) {
						// prepare map for key/value pair
						responseMap = new HashMap<String, String>();
						// get all the values for the asked keys
						for (String key : responseKeys) {
							responseMap.put(key, StringUtils.trimToEmpty(
									Objects.toString(fieldsAsMap.get(key).getValue(), Doc360Constants.EMPTY_STRING)));
						}
						responseList.add(responseMap);
					}
				}
			}
		}

		return responseList;
	}

	/**
	 * Get the RequestOptions with headers for Elastic Search security using the
	 * Actor value passed in soap header, or user name configured in DB table.
	 *
	 * @return RequestOptions
	 */
	private RequestOptions getRequestOptionsWithSecurityHeader(ApplicationClaims applicationClaims,
			Application appDataBean) {
		// get the end user id and application id from call context
		String endUserId = applicationClaims.getActor();

		// if there was app user name defined in db table for given app id,
		// then override that value for end user id,
		// otherwise user the Actor value passed in security header
		if (StringUtils.isNotBlank(appDataBean.getApplicationUserId())) {
			endUserId = appDataBean.getApplicationUserId();
		}

		RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
		builder.addHeader(ES_SECURITY_RUNAS_USER_HEADER, endUserId);
		RequestOptions requestOptions = builder.build();

		log.info("Passing request header for ES security: {}", requestOptions);
		return requestOptions;
	}

	/**
	 * Get source system's full name
	 * 
	 * @return String
	 */
	private String getSourceSystemFullName(String sourceSystem) {
		switch (sourceSystem) {
		case RIO:
			return SOURCE_SYSTEM_RIO_FULL_NAME;
		case BONEYARD:
			return SOURCE_SYSTEM_BONEYARD_FULL_NAME;
		case FARM:
			return SOURCE_SYSTEM_FARM_FULL_NAME;
		}
		return null;
	}

	/**
	 * Checks whether passed urlPath is indicating OOS path or not.
	 * 
	 * @param urlPath
	 * @return true if url is for OOS, otherwise false.
	 */
	private boolean isOOSPath(String urlPath) {
		return (StringUtils.startsWithIgnoreCase(urlPath, oosConfig.getContainerEndpoint())
				|| StringUtils.startsWithIgnoreCase(urlPath, oosConfig.getVaultEndpoint()));

	}

	/**
	 * Get the Rest client for specific service and doc class.
	 *
	 * @return RestClient
	 */
	private RestClient getRestClient(String sourceSystem, String docClassName) {
		switch (sourceSystem) {
		case RIO:
			return getRioRestClient(sourceSystem, docClassName);
		case BONEYARD:
			return getBoneyardRestClient(sourceSystem, docClassName);
		case FARM:
			return getFarmRestClient(sourceSystem, docClassName);
		default:
			throw new Doc360ApplicationException("Invalid source system provided: " + sourceSystem);
		}
	}

	/**
	 * Get the Rest client for specific service and doc class for Boneyard.
	 *
	 * @return RestClient
	 */
	private RestClient getBoneyardRestClient(String sourceSystem, String docClassName) {
		return boneyardRestClient;
	}

	/**
	 * Get the Rest client for specific service and doc class for Farm.
	 *
	 * @return RestClient
	 */
	private RestClient getFarmRestClient(String sourceSystem, String docClassName) {
		return farmRestClient;
	}

	/**
	 * Get the Rest client for specific service and doc class for Rio.
	 *
	 * @return RestClient
	 */
	private RestClient getRioRestClient(String sourceSystem, String docClassName) {
		// if no doc class is provided, then use the default client
		if (StringUtils.isBlank(docClassName)) {
			return rioRestClient;
		} else {
			// resolve the doc class instance, and get the preferences property
			// value
			DocumentClass docCls = doc360Util.getRDocCls(docClassName);
			String rioAltHost = docCls.getPreference().getRioAltHost();
			// if rioAltHost is provided, then gather the client value
			if (StringUtils.isNotBlank(rioAltHost)) {
				RestClient docClassClient = null;
				switch (rioAltHost) {
				case RIO_ALT_HOST_NUMBER_2:
					docClassClient = rioAltTwoRestClient;
					break;
				case RIO_ALT_HOST_NUMBER_3:
					docClassClient = rioAltThreeRestClient;
					break;
				}
				if (docClassClient != null) {
					return docClassClient;
				} else {
					throw new Doc360ApplicationException(
							"Unable to get Rest Client! Invalid Rio Alt Host value provided for doc class: "
									+ docClassName);
				}
			} else {
				// return the default client value
				return rioRestClient;
			}
		}
	}

	/**
	 * Get the Rest high level client for specific service and doc class.
	 *
	 * @return RestHighLevelClient
	 */
	private RestHighLevelClient getRestHighLevelClient(String sourceSystem, String docClassName) {
		switch (sourceSystem) {
		case RIO:
			return getRioRestHighLevelClient(sourceSystem, docClassName);
		case BONEYARD:
			return getBoneyardRestHighLevelClient(sourceSystem, docClassName);
		case FARM:
			return getFarmRestHighLevelClient(sourceSystem, docClassName);
		default:
			throw new Doc360ApplicationException("Invalid source system provided: " + sourceSystem);
		}
	}

	/**
	 * Get the Rest high level client for specific service and doc class.
	 *
	 * @return RestHighLevelClient
	 */
	private FileServerConfig getFileServerConfig(String sourceSystem) {
		switch (sourceSystem) {
		case RIO:
			return rioFileServerConfigConfig;
		case BONEYARD:
			return boneyardFileServerConfigConfig;
		case FARM:
			return farmFileServerConfigConfig;
		default:
			throw new Doc360ApplicationException("Invalid source system provided: " + sourceSystem);
		}
	}

	/**
	 * Get the Rest High Level client for specific service and doc class for
	 * Boneyard.
	 *
	 * @return RestClient
	 */
	private RestHighLevelClient getBoneyardRestHighLevelClient(String sourceSystem, String docClassName) {
		return boneyardRestHighLevelClient;
	}

	/**
	 * Get the Rest High Level client for specific service and doc class for
	 * Farm.
	 *
	 * @return RestClient
	 */
	private RestHighLevelClient getFarmRestHighLevelClient(String sourceSystem, String docClassName) {
		return farmRestHighLevelClient;
	}

	/**
	 * Get the Rest high level client for specific service and doc class for
	 * Rio.
	 *
	 * @return RestHighLevelClient
	 */
	private RestHighLevelClient getRioRestHighLevelClient(String sourceSystem, String docClassName) {
		// if no doc class is provided, then use the default high level client
		if (StringUtils.isBlank(docClassName)) {
			// log.debug("Doc class is blank. Rio Rest Hight Level Client:
			// {}", rioRestHighLevelClient);
			return rioRestHighLevelClient;
		} else {
			// resolve the doc class instance, and get the preferences property
			// value
			DocumentClass docCls = doc360Util.getRDocCls(docClassName);
			String rioAltHost = docCls.getPreference().getRioAltHost();
			// if rioAltHost is provided, then gather the client value
			if (StringUtils.isNotBlank(rioAltHost)) {
				RestHighLevelClient docClassClient = null;
				switch (rioAltHost) {
				case RIO_ALT_HOST_NUMBER_2:
					docClassClient = rioAltTwoRestHighLevelClient;
					break;
				case RIO_ALT_HOST_NUMBER_3:
					docClassClient = rioAltThreeRestHighLevelClient;
					break;
				}
				if (docClassClient != null) {
					// log.debug("For Doc class {}, found the alt host {}.
					// Rio Rest Hight Level Client: {}",
					// docClassName, rioAltHost, docClassClient);
					return docClassClient;
				} else {
					throw new Doc360ApplicationException(
							"Unable to get Rest High Level Client! Invalid Rio Alt Host value provided for doc class: "
									+ docClassName);
				}
			} else {
				// return the default client value
				// log.debug("For Doc class {}, can't find the alt host. Rio
				// Rest Hight Level Client: {}", docClassName,
				// rioRestHighLevelClient);
				return rioRestHighLevelClient;
			}
		}
	}

	/**
	 * Generic method to get list of response keys for the given repository and
	 * object id using GET API.
	 *
	 * @param repositoryId
	 * @param indexName
	 * @param objectId
	 * @param responseKeys
	 * @return Map<String, String>
	 * @throws IOException
	 *
	 * @author Tarun Verma
	 */
	private Map<String, String> getDocumentFromElasticSearch(ApplicationClaims applicationClaims,
			Application appDataBean, String sourceSystem, String repositoryId, String indexName, String objectId,
			String... responseKeys) throws IOException {
		log.info("ES GET API for repository {} and object id {}", indexName, objectId);
		// prepare the security header for ES API
		// find the application configuration details for given app id
		RequestOptions requestOptions = getRequestOptionsWithSecurityHeader(applicationClaims, appDataBean);

		// use high level rest client to get the search hits.
		GetRequest getRequest = new GetRequest(indexName, ES_TYPE_DOCUMENT, objectId)
				.preference(ES_PREFERENCE_PRIMARY_FIRST);
		// use source fields provided
		FetchSourceContext fetchSourceContext = new FetchSourceContext(true, responseKeys, Strings.EMPTY_ARRAY);
		getRequest.fetchSourceContext(fetchSourceContext);

		log.info("Get Request prepared for ES: {}", getRequest);
		// perform the search and extract the response
		GetResponse getResponse = null;
		try {
			getResponse = getRestHighLevelClient(sourceSystem, repositoryId).get(getRequest, requestOptions);
		} catch (ElasticsearchException ese) {
			if (ese.status() == RestStatus.NOT_FOUND) {
				throw new DocumentContentException("The requested document cannot be found. RepositoryId: " + indexName
						+ " globalDocId: " + objectId);
			}
			if (ese.status() == RestStatus.CONFLICT) {
				throw new DocumentContentException(
						"The requested document has a different version than requested. RepositoryId: " + indexName
								+ " globalDocId: " + objectId);
			}
			throw new DocumentContentException("Unable to get the document using GET API. RepositoryId: " + indexName
					+ " globalDocId: " + objectId, ese);
		}
		log.info("GET API call finished for document. RepositoryId: {}, GlobalDocId: {}", indexName, objectId);

		// check whether document was retrieved or not
		if (!getResponse.isExists()) {
			throw new DocumentContentException(
					"The requested document doesn't exist. RepositoryId: " + indexName + " globalDocId: " + objectId);
		}

		// parse the search result
		Map<String, String> responseMap = null;
		String index = getResponse.getIndex();
		String type = getResponse.getType();
		String id = getResponse.getId();
		log.debug("Record index: {}; type: {}; id: {}", index, type, id);

		// use source fields for result hits
		// get the fields map and capture what is needed
		Map<String, Object> sourceAsMap = getResponse.getSourceAsMap();
		// make sure _source was populated
		if (sourceAsMap != null) {
			// prepare map for key/value pair
			responseMap = new HashMap<String, String>();
			// get all the values for the asked keys
			for (String key : responseKeys) {
				responseMap.put(key, StringUtils.trimToEmpty(Objects.toString(sourceAsMap.get(key), EMPTY_STRING)));
			}
		}

		return responseMap;
	}

	/**
	 * Get the content for the passed url path using HttpConnection.
	 * 
	 * @param preference
	 * @param repositoryId
	 * @param objectId
	 * @param urlPath
	 * @param contentSize
	 * @param totalPageCount
	 * @param startPage
	 * @param endPage
	 * @param fileServerUserid
	 * @param fileServerPwd
	 * @param fileServerConnectTimeout
	 * @param fileServerReadTimeout
	 * @return Document
	 * @throws IOException
	 */
	private Document getContent(DocumentClassPreference docClsPreference, String sourceSystem, String repositoryId,
			String objectId, String urlPath, String contentSize, String contentType, String totalPageCount,
			Optional<Integer> startPage, Optional<Integer> endPage) throws IOException {
		log.info("Going to get content from url using HttpConnection: {}", urlPath);
		log.info("Doc class: {}, Object Id: {}, Total Pages: {}, Offset: {}, Pages Count: {}, Content Type : {}",
				repositoryId, objectId, totalPageCount, startPage, endPage, contentType);
		Document document = new Document();
		FileServerConfig fileServerConfig = getFileServerConfig(sourceSystem);
		String basicAuthPlain = fileServerConfig.getBasicAuthUserid() + COLON + fileServerConfig.getBasicAuthPwd();
		String basicAuth = BASIC_AUTH + new String(Base64.getEncoder().encode(basicAuthPlain.getBytes()));
		// log.debug("Setting up basic auth: {}", basicAuth);

		log.debug("Going to setup the http url....");
		// set up the HttpURLConnection with the passed url,
		// and pass the Basic Auth on that url
		// define the server connection settings as well to avoid
		// indefinite connection time in case FS is down etc.
		URL url = new URL(urlPath);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setRequestProperty(HttpHeaders.AUTHORIZATION, basicAuth);
		urlConnection.setConnectTimeout(fileServerConfig.getConnectTimeOut());
		urlConnection.setReadTimeout(fileServerConfig.getReadTimeOut());
		urlConnection.setDefaultUseCaches(false);
		log.debug("Setup the basic auth and timeouts values, and now getting the response code...");

		// for https url, setup the SSL context and apply on url connection
		if (urlPath.toLowerCase(Locale.ENGLISH).startsWith(HTTPS_PROTOCOL)
				&& urlConnection instanceof HttpsURLConnection) {
			// set up the SSL context socket factory on url
			((HttpsURLConnection) urlConnection).setSSLSocketFactory(sslContext.getSocketFactory());
			log.debug("Setup the SSL socket factory on http url connection...");
		}

		// Check whether connection was successful or not.
		int responseStatus = urlConnection.getResponseCode();
		if (responseStatus == HttpURLConnection.HTTP_OK) {
			log.debug("Recieved OK status code from url.");
			// get the output type for this doc class
			// int type = doc360Util
			// .getDocumentTypeIdByName(doc360Util.getRDocCls(repositoryId).getPreference().getDocOutputType());
			// generate the file name for output
			// objectId = objectId + doc360Util.getFileExtension(type);
			objectId = objectId + DOT + contentTypeMapping.getFileExtension(contentType);

			// get the input stream for the urlpath
			InputStream contentInputStream = null;
			if (docClsPreference.isEnableContentInZip() || StringUtils.equalsIgnoreCase(contentType, CONTENT_TYPE_ZIP)
					|| StringUtils.equalsIgnoreCase(contentType, CONTENT_TYPE_ZIPX)) {
				// if content is stored as .zip.zip, then get the next
				// compressed file
				if (StringUtils.endsWithIgnoreCase(urlPath, FILE_EXTENSION_ZIP + FILE_EXTENSION_ZIP)) {
					log.debug("Getting first zip file inside the content zip file....");
					// get the zip input stream of first zip file
					contentInputStream = doc360Util
							.getFirstZipInputStream(new BufferedInputStream(urlConnection.getInputStream()));
					if (contentInputStream == null) {
						throw new RuntimeException(
								"Unable to get the content from zip file. Either it's corrupted, or it's invalid zip file.");
					}
				} else {
					// get the content stream from urlpath directly
					contentInputStream = new BufferedInputStream(urlConnection.getInputStream());
				}
			} else
			// if content is compressed, then get the document's stream after
			// decompression
			if (StringUtils.endsWithIgnoreCase(urlPath, FILE_EXTENSION_ZIP)) {
				// get the zip input stream set to get the document content
				contentInputStream = doc360Util
						.getZipInputStream(new BufferedInputStream(urlConnection.getInputStream()));
				if (contentInputStream == null) {
					throw new RuntimeException(
							"Unable to get the content from zip file. Either it's corrupted, or it's invalid zip file.");
				}
			} else {
				// get the content stream from urlpath directly
				contentInputStream = new BufferedInputStream(urlConnection.getInputStream());
			}

			// use the content size retrieved in search result
			// if missing, then use the default value as 1.
			contentSize = (StringUtils.isBlank(contentSize) ? DEFAULT_CONTENT_SIZE : contentSize);
			long contentSizeLong = Long.parseLong(contentSize);
			log.info("Receiving the content back from url; File: {}, Size: {}", objectId, contentSize);

			// calculate the start and end page number based on total pages,
			// offset and length values
			int totalPages = doc360Util.getTotalPages(totalPageCount);
			int startPageNumber = doc360Util.getStartPageNumber(startPage);
			int endPageNumber = doc360Util.getEndPageNumber(totalPages, startPageNumber, endPage);
			log.info("Getting content for pages; Total Pages: {}, Start Page#: {}, End Page#: {}", totalPages,
					startPageNumber, endPageNumber);

			// get the mime type for the document
			// String mimeType = doc360Util.getMimeType(type);
			String mimeType = contentTypeMapping.getMimeType(contentType);
			document.setMimeType(mimeType);
			document.setContentSize(contentSizeLong);
			document.setObjectName(objectId);
			// return the content stream based on page range or whole document
			document.setContentStream(doc360Util.getContentForPageRange(sourceSystem, contentInputStream, objectId,
					contentSizeLong, mimeType, totalPages, startPageNumber, endPageNumber));
			return document;
		} else {
			log.error("Got unsuccessful error code in return while accessing URL {} : {}", urlPath, responseStatus);
			throw new DocumentContentException("Error occurred while connecting with the URL: " + urlPath);
		}
	}

	/**
	 * Get the document after finding the unique document based on given
	 * criteria. Return the error message if more than one document is found for
	 * criteria.
	 * 
	 * @param typeName
	 * @param getContentSearchRequest
	 * @param sourceSystemOptional
	 * @param startPage
	 * @param endPage
	 * @param applicationClaims
	 * @param request
	 * @param response
	 * @return Document
	 */
	public Document getDocumentContentAfterFind(String typeName, GetContentSearchRequest getContentSearchRequest,
			Optional<String> sourceSystemOptional, Optional<Integer> startPage, Optional<Integer> endPage,
			ApplicationClaims applicationClaims, final HttpServletRequest httpRequest,
			final HttpServletResponse httpResponse) {
		// build the url end point for Find operation
		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
				.fromUriString(applicationServiceConfig.getUrlEndpoint()).encode();
		if (sourceSystemOptional.isPresent()) {
			uriComponentsBuilder.queryParam("sourceSystem", sourceSystemOptional.get());
		}

		URI targetUrl = uriComponentsBuilder.buildAndExpand(typeName).toUri();
		log.info("REST Find URL: {}", targetUrl);

		// prepare the search request for Find
		com.dms.doc360.rest.getcontent.model.SearchRequest findSearchRequest = new com.dms.doc360.rest.getcontent.model.SearchRequest();
		// make sure to set index name same type name
		findSearchRequest.setIndexName(typeName);
		findSearchRequest.setClientTransactionId(getContentSearchRequest.getClientTransactionId());
		findSearchRequest.setCriteria(getContentSearchRequest.getCriteria());
		// find max 2 records
		findSearchRequest.setTotalRecords(2);

		// prepare the request entity for POST operation
		RequestEntity<com.dms.doc360.rest.getcontent.model.SearchRequest> requestEntity = RequestEntity
				.post(targetUrl).accept(MediaType.APPLICATION_JSON)
				.header(applicationSecurityConfig.getTokenHeaderName(),
						httpRequest.getHeader(applicationSecurityConfig.getTokenHeaderName()))
				.body(findSearchRequest);

		// perform the Find operation
		ResponseEntity<SearchResult> findResponse = null;
		try {
			findResponse = findRestTemplate.exchange(requestEntity, SearchResult.class);
		} catch (RestClientResponseException rcre) {
			// handle the find operation failures
			String errorPayload = rcre.getResponseBodyAsString();
			// log.debug("Error Payload: {}", errorPayload);
			// parse the json structure, and get the error message
			// returned by Find service
			if (StringUtils.isNotBlank(errorPayload)) {
				try {
					RestError restError = new ObjectMapper().readValue(errorPayload, RestError.class);
					throw new DocumentSearchException(restError.getMessage());
				} catch (IOException e) {
					log.error("Unable to get RestError from Find service.", e);
				}
			}
			// if no payload or unable to parse message, then return this
			// exception as it is
			throw rcre;
		}

		SearchResult findSearchResultData = (SearchResult) findResponse.getBody();
		log.debug("Found search response: {}", findSearchResultData);

		// check for no documents found status
		if (findSearchResultData == null || findSearchResultData.getTotalRecords() == 0) {
			return null;
		}

		// if unique document is found, then return that.
		if (findSearchResultData.getTotalRecords() == 1
				&& !CollectionUtils.isEmpty(findSearchResultData.getRecordsList())) {
			// get the found document first, and gather object id
			Document findResultDocument = findSearchResultData.getRecordsList().get(0);
			// get the content of the document
			return getDocumentContent(typeName, findResultDocument.getObjectId(), sourceSystemOptional, startPage,
					endPage, applicationClaims, httpResponse);
		}
		// if more than one document found, then throw the error
		throw new UniqueDocumentNotFoundException(
				"More than one document found for Type: " + typeName + "; Criteria: " + getContentSearchRequest);
	}
}
