/**
 * 
 */
package com.dms.doc360.rest.getcontent.utils.security;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.dms.doc360.rest.getcontent.utils.Doc360Constants;

import lombok.extern.slf4j.Slf4j;

/**
 * This class will be used for Base64 based encryption and decryption.
 * 
 * @author Tarun Verma
 */
@Component
@Slf4j
public class Base64Util implements Doc360Constants {

	/**
	 * Encrypt the passed string using Base64 encoding.
	 * 
	 * @param input
	 * @return String Encrypted value. Emptry string in case there were any
	 *         errors.
	 */
	public String encrypt(String input) {
		try {
			if (StringUtils.isNotBlank(input)) {
				String encryptedValue = Base64.getEncoder().encodeToString(input.getBytes(StandardCharsets.UTF_8));
				// log.debug("Base64 encoding from {} to {}.", input,
				// encryptedValue);
				return encryptedValue;
			}
		} catch (Exception th) {
			log.error("Unable to encode value using Base64. Input: " + input, th);
		}
		return EMPTY_STRING;
	}

	/**
	 * Encrypt the passed string using Base64 encoding.
	 * 
	 * @param input
	 * @return String Encrypted value. Emptry string in case there were any
	 *         errors.
	 */
	public String decrypt(String input) {
		try {
			if (StringUtils.isNotBlank(input)) {
				String decryptedValue = new String(Base64.getDecoder().decode(input), StandardCharsets.UTF_8);
				log.debug("Base64 decoded from {} to {}.", input, decryptedValue);
				return decryptedValue;
			}
		} catch (Exception th) {
			log.error("Unable to decode value using Base64. Input: " + input, th);
		}
		return EMPTY_STRING;
	}

}
