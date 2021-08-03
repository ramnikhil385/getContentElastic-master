/**
 * Created on: Jun 16, 2015
 */
package com.dms.doc360.rest.getcontent.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * This class is used to define the content type mapping.
 * 
 * @author Tarun Verma
 *
 */
@Slf4j
@Component
public class ContentTypeMapping implements Doc360Constants {

	@Value("${document.content.type.mapping.file}")
	private Resource mappingFile;

	private HashMap<String, AbstractMap.SimpleImmutableEntry<String, String>> contentTypeMap = null;

	/**
	 * Load the content mapping file
	 */
	@PostConstruct
	private void loadContentTypeMapping() {
		if (contentTypeMap == null) {
			contentTypeMap = new HashMap<String, AbstractMap.SimpleImmutableEntry<String, String>>();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(mappingFile.getInputStream()));) {
				log.info("Mapping File: " + mappingFile);

				String line = null;
				String[] keyValue = null;
				while ((line = reader.readLine()) != null) {
					keyValue = line.split(COMMA);

					// always enter the first occurrence content type
					if (!contentTypeMap.containsKey(keyValue[0])) {
						// key in the entry would be File Extension, and
						// value would be MIME type
						contentTypeMap.put(keyValue[0],
								new SimpleImmutableEntry<String, String>(keyValue[1], keyValue[2]));
					}
				}

				log.info("Content Type Map: " + contentTypeMap);
			} catch (IOException ioe) {
				log.error("Error during reading the content type mapping file.", ioe);
			}
		}
	}

	/**
	 * Get the MIME type based on the content type.
	 * 
	 * @param contentType
	 * @return String
	 */
	public String getMimeType(String contentType) {
		SimpleImmutableEntry<String, String> value = contentTypeMap.get(contentType);
		return (value != null) ? value.getValue() : null;
	}

	/**
	 * Get the file extension based on the content type.
	 * 
	 * @param contentType
	 * @return String
	 */
	public String getFileExtension(String contentType) {
		SimpleImmutableEntry<String, String> value = contentTypeMap.get(contentType);
		return (value != null) ? value.getKey() : null;
	}

	/**
	 * Get the content type based on the file extension.
	 * 
	 * @param fileExtension
	 * @return String
	 */
	public String getContentTypeForFileExtension(String fileExtension) {
		// scan the map
		for (Map.Entry<String, AbstractMap.SimpleImmutableEntry<String, String>> entry : contentTypeMap.entrySet()) {
			// map value is another pair, which key is file extension
			if (fileExtension.equalsIgnoreCase(entry.getValue().getKey())) {
				return entry.getKey();
			}
		}
		return null;
	}

	/**
	 * Get the MIME type based on the file extension.
	 * 
	 * @param fileExtension
	 * @return String
	 */
	public String getMimeTypeForFileExtension(String fileExtension) {
		// scan the map
		for (AbstractMap.SimpleImmutableEntry<String, String> value : contentTypeMap.values()) {
			// map value is another pair, which key is file extension
			if (fileExtension.equalsIgnoreCase(value.getKey())) {
				return value.getValue();
			}
		}
		return null;
	}
}
