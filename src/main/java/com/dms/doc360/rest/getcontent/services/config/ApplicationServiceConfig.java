/**
 * 
 */
package com.dms.doc360.rest.getcontent.services.config;

import javax.validation.constraints.NotBlank;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This will configure the external services.
 * 
 * @author Tarun Verma
 *
 */
@Configuration
@ConfigurationProperties(prefix = "find.rest.api")
@NoArgsConstructor
@Getter
@Setter
public class ApplicationServiceConfig {

	@NotBlank
	private String urlEndpoint;
	
	@NotBlank
	private int socketConnectionTimeout;

	@NotBlank
	private int socketReadTimeout;

	@NotBlank
	private int poolingMaxTotalConnections;

	@NotBlank
	private int poolingDefaultMaxPerRoute;

	/**
	 * Prepare the new RequestConfig instance.
	 * 
	 * @return RequestConfig
	 */
	@Bean
	public RequestConfig requestConfig() {
		RequestConfig result = RequestConfig.custom()

				// refers to the timeout when requesting a connection from the
				// connection manager
				.setConnectionRequestTimeout(Long.valueOf(socketConnectionTimeout).intValue())

				// refers to the timeout until a connection is established
				.setConnectTimeout(Long.valueOf(socketConnectionTimeout).intValue())

				// refers to the timeout for waiting for data
				.setSocketTimeout(Long.valueOf(socketReadTimeout).intValue()).build();
		return result;
	}

	/**
	 * Prepare the new HttpClient instance for given connection manager, and
	 * request config instance.
	 * 
	 * @param requestConfig
	 * @return CloseableHttpClient
	 */
	public CloseableHttpClient httpClient(RequestConfig requestConfig) {
		CloseableHttpClient result = HttpClientBuilder.create().useSystemProperties()
				.setDefaultRequestConfig(requestConfig).setMaxConnPerRoute(poolingDefaultMaxPerRoute)
				.setMaxConnTotal(poolingMaxTotalConnections).build();
		return result;
	}

	/**
	 * Prepare the Rest Template for REST Find API calls.
	 * 
	 * @return RestTemplate
	 */
	@Bean
	public RestTemplate findRestTemplate() {
		return new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient(requestConfig())));
	}

}
