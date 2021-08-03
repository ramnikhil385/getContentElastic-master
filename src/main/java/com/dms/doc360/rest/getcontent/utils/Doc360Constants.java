package com.dms.doc360.rest.getcontent.utils;

/**
 * Constant interfaces for this project.
 * 
 * @author Tarun Verma
 *
 */
public interface Doc360Constants {

	public static final String SINGLE_SPACE = " ";
	public static final String DOT = ".";
	public static final String COMMA = ",";
	public static final String TILDA = "~";
	public static final String HYPHEN = "-";
	public static final String UNDERSCORE = "_";
	public static final String DOUBLE_QUOTES = "\"";
	public static final String SINGLE_QUOTE = "'";
	public static final String PIPE = "|";
	public static final String EQUAL = "=";
	public static final String SEMICOLON = ";";
	public static final String COLON = ":";
	public static final String PERCENTAGE = "%";
	public static final String WILDCARD = "*";
	public static final String ZERO = "0";
	public static final String FORWARD_SLASH = "/";
	public static final String END_OF_LINE = System.getProperty("line.separator");
	public static final int BUFFER_LENGTH = 1024;
	public static final String PDF_EXTENSION = ".pdf";
	public static final String TEXT_EXTENSION = ".txt";
	public static final String CSV_EXTENSION = ".csv";
	public static final String BONEYARD = "B";
	public static final String FARM = "F";
	public static final String RIO = "R";
	public static final String DATERANGE = "DateRange";
	public static final String DATE = "Date";
	public static final String DOCUMENT_NOT_FOUND_ERROR = "Error occurred. Unable to retrieve document, please contact the helpdesk.";
	public static final String HEADER_QUERY_SCROLL_ID = "query_scroll_id";
	public static final String NODE_QUERY_SCROLL_ID = "query_scroll_id";
	public static final String SEARCH_ERROR = "An error has occurred during your search.  Please log out, log back in and try your search again";
	public static final String HTTP = "http";
	public static final String HTTPS = "https";
	public static final String LOCALHOST = "localhost";
	public static final String HOST_OPTUM = ".uhc.com";
	public static final String FIELD_GLOBAL_DOC_ID = "u_gbl_doc_id";
	public static final String DEFAULT_CONTENT_SIZE = "1";
	public static final int DEFAULT_PAGE_NO = 1;
	public static final String TIME_ZONE_GMT = "GMT";
	public static final String INDICATOR_YES = "Y";
	public static final String INDICATOR_NO = "N";
	public static final Character CHARACTER_FORM_FEED = new Character('\u000c');
	public static final String PAGE_BREAK_SEARCH_STRING = new String(new char[] { CHARACTER_FORM_FEED.charValue() });
	// cache control
	public static final String HTTP_HEADER_CACHE_CONTROL = "no-cache, no-store, must-revalidate, post-check=0, pre-check=0";
	public static final String HTTP_HEADER_PRAGMA = "no-cache";
	public static final String HTTP_HEADER_DATE_ZERO_OFFSET_VALUE = "Mon, 26 Jul 1997 05:00:00 GMT";
	public static final String DONE = "Done";

	// http headers/cookies etc
	public static final String COOKIE_SESSION_ID = "JSESSIONID";
	public static final String X_XSRF_TOKEN = "X-XSRF-TOKEN";
	public static final String XSRF_TOKEN = "XSRF-TOKEN";
	public static final String CONTENT_DISPOSITION = "Content-Disposition";
	public static final String EXPIRES = "Expires";
	public static final String IF_MODIFIED_SINCE = "If-Modified-Since";
	public static final String PRAGMA = "Pragma";
	public static final String CACHE_CONTROL = "Cache-Control";
	public static final String BASIC_AUTHORIZATION_HEADER_PREFIX = "Basic ";
	public static final String HEADER_APPLICATION_IDENTIFIER = "ApplicationEntity-Identifier";

	public static final String EMPTY = "";
	public static final String ISO_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'.'SSS'Z'";
	public static final String DATE_FORMAT_YYYYMMDD = "yyyy-MM-dd";
	public static final String DATE_FORMAT_MMDDYYYY = "MM/dd/yyyy";
	public static final String DATE_FORMAT_YYYYMMDDHH24MISS = "yyyyMMdd'T'HHmmssSSS";
	public static final String EMPTY_STRING = "";
	public static final String BINARY_CONTENT_TYPE = "application/octet-stream";

	// spring security roles
	public static final String ROLE_USER = "ROLE_USER";
	public static final String ROLE_ADMIN = "ROLE_ADMIN";

	// Request type
	public static final String REQUEST_TYPE_HEAD = "HEAD";
	public static final String REQUEST_TYPE_GET = "GET";

	// logging constants
	public static final String MDC_USER_ID = "userID";
	public static final String MDC_USER_IP_ADDRESS = "userIPAddress";
	public static final String MDC_APPLICATION_IDENTIFIER = "applicationIdentifier";

	// Security constants
	public static final String CHARSET_UTF_8 = "UTF-8";
	public static final String ALGORITHM_SHA_256 = "SHA-256";

	public static final String JWT_TOKEN_ISSUER = "DOC360_API_CLIENT";
	public static final String JWT_CLAIM_APPID = "appId";
	public static final String JWT_CLAIM_USERNAME = "username";
	public static final String JWT_CLAIM_CRED_DIGEST = "passworddigest";
	public static final String JWT_CLAIM_NONCE = "nonce";
	public static final String JWT_CLAIM_CREATED = "created";
	public static final String JWT_CLAIM_ACTOR = "actor";
	public static final String JWT_CLAIM_DOMAIN = "domain";
	public static final String JWT_CLAIM_EDSS_USERNAME = "edssUserName";
	public static final String JWT_CLAIM_EDSS_USERCRED = "edssUserCred";
	public static final String REQUEST_ATTRIBUTE_DOC360_JWT_APPLICATION_CLAIMS = "DOC360_JWT_APPLICATION_CLAIMS";

	public static final String BEAN_ID_APPLICATION_SECURITY_CONFIG = "applicationSecurityConfig";
	public static final String BEAN_ID_JWT_VALIDATOR = "jwtValidator";
	// BEAN ID CONSTANTS
	public static final String RIO_ALT_HOST_NUMBER_2 = "2";
	public static final String RIO_ALT_HOST_NUMBER_3 = "3";
	public static final String BEAN_ID_SSL_CONTEXT = "sslContext";
	public static final String BEAN_ID_SU_MANAGER = "suManager";
	public static final String BEAN_ID_ES_REST_CLIENT = "esRestClient";
	public static final String BEAN_ID_BONEYARD_REST_CLIENT = "boneyardRestClient";
	public static final String BEAN_ID_FARM_REST_CLIENT = "farmRestClient";
	public static final String BEAN_ID_RIO_REST_CLIENT = "rioRestClient";
	public static final String BEAN_ID_RIO_ALT_TWO_REST_CLIENT = "rioAltTwoRestClient";
	public static final String BEAN_ID_RIO_ALT_THREE_REST_CLIENT = "rioAltThreeRestClient";
	public static final String BEAN_ID_BONEYARD_REST_HIGH_LEVEL_CLIENT = "boneyardRestHighLevelClient";
	public static final String BEAN_ID_FARM_REST_HIGH_LEVEL_CLIENT = "farmRestHighLevelClient";
	public static final String BEAN_ID_RIO_REST_HIGH_LEVEL_CLIENT = "rioRestHighLevelClient";
	public static final String BEAN_ID_RIO_ALT_TWO_REST_HIGH_LEVEL_CLIENT = "rioAltTwoRestHighLevelClient";
	public static final String BEAN_ID_RIO_ALT_THREE_REST_HIGH_LEVEL_CLIENT = "rioAltThreeRestHighLevelClient";
	public static final String BEAN_ID_OOS_FACADE = "oosFacade";
	public static final String BEAN_ID_EDMS_BDS_SEARCH_SCROLL_CACHE = "edmsBdsSearchScrollCache";
	public static final String BEAN_ID_EDMS_ELASTIC_SEARCH_SCROLL_CACHE = "edmsElasticSearchScrollCache";
	public static final String BEAN_ID_TASK_EXECUTOR = "taskExecutor";

	// AWS S3 CONSTANTS
	public static final String AWS_S3_DEFAULT_REGION = "us-east-1";

	public static final String ES_TYPE_DOCUMENT = "Document";
	public static final String ES_QUERY_DELIMETER = "?";
	public static final String ES_GLOBAL_DOC_ID_IDENTIFIER = "u_gbl_doc_id";
	public static final String ES_REPO_NAME_IDENTIFIER = "repository_name";
	public static final String ES_OBJECT_ID_IDENTIFIER = "r_object_id";
	public static final String ES_CONTENT_SIZE_IDENTIFIER = "r_content_size";
	public static final String ES_COMPOUND_DOC = "u_compound_doc";
	public static final String ES_URL_PATH_IDENTIFIER = "urlpath";
	public static final String ES_PAGE_COUNT_IDENTIFIER = "r_page_cnt";
	public static final String ES_CONTENT_TYPE_IDENTIFIER = "a_content_type";
	public static final String ES_ORIG_CREATION_DATE_IDENTIFIER = "u_orig_creation_date";
	public static final String ES_SECURITY_RUNAS_USER_HEADER = "es-security-runas-user";
	public static final String ES_DOC_TYPE_DEFAULT_SUFFIX = "_2";
	public static final String ES_SCROLL_ID = "_scroll_id";
	public static final String ES_MATCH_QUERY_CLAUSE = "matchQuery";
	public static final String ES_PREFERENCE_PRIMARY_FIRST = "_primary_first";
	public static final String ES_DOCVALUES_DEFAULT_FORMAT = "use_field_mapping";
	public static final String ES_METADATA_ATTRIBUTE_PREFIX = "u_";
	public static final String ES_BODY = "body";
	public static final String ES_ATTRIBUTE_TYPE_TEXT = "Text";
	public static final String ES_INDEX_NAME_MARM = "u_arm_rpt";
	public static final String ES_MARM_RUN_DT_NAME = "u_run_dt";
	public static final long ES_SCROLL_TIME_OUT_IN_MINUTES = 15L;

	// FILE EXTENSIONS
	String FILE_EXTENSION_PDF = ".pdf";
	String RESPONSE_HEADER_DOCUMENT_PDF_UA_FLAG = "documentPdfUAFlag";
	String PIPE_SEPARATOR = "|";
	String BASIC_AUTH = "Basic ";
	String HTTPS_PROTOCOL = "https";
	public static final String FILE_EXTENSION_TXT = ".txt";
	public static final String FILE_EXTENSION_ZIP = ".zip";
	
	// CONTENT TYPES
	public static final String CONTENT_TYPE_ZIP = "zip";
	public static final String CONTENT_TYPE_ZIPX = "zipx";

	// MIME CONSTANTS
	public static final String MIME_TYPE_PDF = "application/pdf";
	public static final String MIME_TYPE_TEXT = "text/plain";

	// LOGGING CONSTANTS
	public static final String MDC_HOST_NAME = "hostName";

	// SECURITY CONSTANTS
	public static final String BOUNCY_CASTLE_PROVIDER_NAME = "BC";

	public static final String SOURCE_SYSTEM_BONEYARD = "B";
	public static final String SOURCE_SYSTEM_LEGACY_EDSS = "E";
	public static final String SOURCE_SYSTEM_FARM = "F";
	public static final String SOURCE_SYSTEM_RIO = "R";
	public static final String SOURCE_SYSTEM_BONEYARD_FULL_NAME = "BONEYARD";
	public static final String SOURCE_SYSTEM_LEGACY_EDSS_FULL_NAME = "LEGACY";
	public static final String SOURCE_SYSTEM_FARM_FULL_NAME = "FARM";
	public static final String SOURCE_SYSTEM_RIO_FULL_NAME = "RIO";
	public static final String REQUEST_HEADER_CLIENT_TRANSACTION_ID = "clientTransactionId";
}
