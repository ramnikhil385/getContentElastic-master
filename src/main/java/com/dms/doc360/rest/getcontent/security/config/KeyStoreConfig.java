/**
 * 
 */
package com.dms.doc360.rest.getcontent.security.config;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;
import javax.validation.constraints.NotBlank;

import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Configuration for Keystore file to build SSL Context
 * 
 * @author Tarun Verma
 *
 */
@Configuration
@ConfigurationProperties(prefix = "keystore.file")
@NoArgsConstructor
@Getter
@Setter
public class KeyStoreConfig {

	@NotBlank
	private String path;

	@NotBlank
	private String pwd;

	/**
	 * Provide the SSLContext for this application.
	 * 
	 * @return SSLContext
	 */
	@Bean
	public SSLContext sslContext() {
		try {
			return new SSLContextBuilder().loadTrustMaterial(
					Thread.currentThread().getContextClassLoader().getResource(path), pwd.toCharArray()).build();
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException | CertificateException
				| IOException e) {
			throw new RuntimeException("Error occurred while loading keystore", e);
		}
	}
}
