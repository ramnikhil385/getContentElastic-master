/**
 * 
 */
package com.dms.doc360.rest.getcontent.elasticsearch.config;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.net.ssl.SSLContext;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.dms.doc360.rest.getcontent.utils.Doc360Constants;

/**
 * Configuration class to initialize all ES related components.
 * 
 * @author Tarun Verma
 *
 */
@Configuration
public class ElasticSearchConfig {

	private RestClient farmRestClient;
	private RestClient boneyardRestClient;
	private RestClient rioRestClient;
	private RestClient rioAltTwoRestClient;
	private RestClient rioAltThreeRestClient;
	private RestHighLevelClient farmRestHighLevelClient;
	private RestHighLevelClient boneyardRestHighLevelClient;
	private RestHighLevelClient rioRestHighLevelClient;
	private RestHighLevelClient rioAltTwoRestHighLevelClient;
	private RestHighLevelClient rioAltThreeRestHighLevelClient;

	@Autowired
	private SSLContext sslContext;

	/**
	 * Provide the FARM elastic search server config
	 * 
	 * @return ElasticSearchServerConfig
	 */
	@Bean
	@ConfigurationProperties(prefix = "farm.elastic.search")
	public ElasticSearchServerConfig farmSearchServerConfig() {
		return new ElasticSearchServerConfig();
	}

	/**
	 * Provide the BONEYARD elastic search server config
	 * 
	 * @return ElasticSearchServerConfig
	 */
	@Bean
	@ConfigurationProperties(prefix = "boneyard.elastic.search")
	public ElasticSearchServerConfig boneyardSearchServerConfig() {
		return new ElasticSearchServerConfig();
	}

	/**
	 * Provide the RIO elastic search server config
	 * 
	 * @return ElasticSearchServerConfig
	 */
	@Bean
	@ConfigurationProperties(prefix = "rio.elastic.search")
	public ElasticSearchServerConfig rioSearchServerConfig() {
		return new ElasticSearchServerConfig();
	}

	/**
	 * Provide the RIO Alt Two elastic search server config
	 * 
	 * @return ElasticSearchServerConfig
	 */
	@Bean
	@ConfigurationProperties(prefix = "rio.alt.two.elastic.search")
	public ElasticSearchServerConfig rioAltTwoSearchServerConfig() {
		return new ElasticSearchServerConfig();
	}

	/**
	 * Provide the RIO Alt Three elastic search server config
	 * 
	 * @return ElasticSearchServerConfig
	 */
	@Bean
	@ConfigurationProperties(prefix = "rio.alt.three.elastic.search")
	public ElasticSearchServerConfig rioAltThreeSearchServerConfig() {
		return new ElasticSearchServerConfig();
	}

	/**
	 * Provide the FARM file server config
	 * 
	 * @return FileServerConfig
	 */
	@Bean
	@ConfigurationProperties(prefix = "farm.file.server")
	public FileServerConfig farmFileServerConfigConfig() {
		return new FileServerConfig();
	}

	/**
	 * Provide the RIO file server config
	 * 
	 * @return FileServerConfig
	 */
	@Bean
	@ConfigurationProperties(prefix = "rio.file.server")
	public FileServerConfig rioFileServerConfigConfig() {
		return new FileServerConfig();
	}

	/**
	 * Provide the BONEYARD file server config
	 * 
	 * @return FileServerConfig
	 */
	@Bean
	@ConfigurationProperties(prefix = "boneyard.file.server")
	public FileServerConfig boneyardFileServerConfigConfig() {
		return new FileServerConfig();
	}

	/**
	 * Initialize the components and beans once application is started.
	 */
	@PostConstruct
	public void init() {
		buildFarmRestClient();
		buildBoneyardRestClient();
		buildRioRestClient();
		buildRioAltTwoRestClient();
		buildRioAltThreeRestClient();
	}

	/**
	 * Before application is destroyed, release the rest client resources.
	 * 
	 * @throws IOException
	 */
	@PreDestroy
	public void destroy() throws IOException {
		this.farmRestHighLevelClient.close();
		this.boneyardRestHighLevelClient.close();
		this.rioRestHighLevelClient.close();
		this.rioAltTwoRestHighLevelClient.close();
		this.rioAltThreeRestHighLevelClient.close();
		// this.farmRestClient.close();
		// this.boneyardRestClient.close();
		// this.rioRestClient.close();
		// this.rioAltTwoRestClient.close();
		// this.rioAltThreeRestClient.close();
	}

	/**
	 * Prepare the Farm Rest client using configuration details.
	 * 
	 */
	private void buildFarmRestClient() {
		ElasticSearchServerConfig farmSearchServerConfig = farmSearchServerConfig();
		// SET AUTHENTICATION
		final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY,
				new UsernamePasswordCredentials(farmSearchServerConfig.getUsername(), farmSearchServerConfig.getPwd()));

		// prepare the rest client for Farm
		RestClientBuilder builder = RestClient
				.builder(new HttpHost(farmSearchServerConfig.getHost(),
						Integer.valueOf(farmSearchServerConfig.getPort()), farmSearchServerConfig.getProtocol()))
				.setMaxRetryTimeoutMillis(farmSearchServerConfig.getMaxRetryTimeout())
				.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {

					@Override
					public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
						if (Doc360Constants.HTTPS.equals(farmSearchServerConfig.getProtocol())) {
							return httpClientBuilder.setSSLContext(sslContext)
									.setDefaultCredentialsProvider(credentialsProvider);
						} else {
							return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
						}
					}
				}).setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {

					@Override
					public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
						return requestConfigBuilder.setConnectTimeout(farmSearchServerConfig.getConnectTimeout())
								.setSocketTimeout(farmSearchServerConfig.getSocketTimeout())
								.setConnectionRequestTimeout(farmSearchServerConfig.getConnectionRequestTimeout());
					}
				});
		this.farmRestHighLevelClient = new RestHighLevelClient(builder);
		this.farmRestClient = this.farmRestHighLevelClient.getLowLevelClient();
		// this.farmRestClient = builder.build();
		// this.farmRestHighLevelClient = new
		// RestHighLevelClient(this.farmRestClient);
	}

	/**
	 * Build the Rio rest client based on configuration details.
	 */
	private void buildRioRestClient() {
		ElasticSearchServerConfig rioSearchServerConfig = rioSearchServerConfig();
		// SET AUTHENTICATION
		final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY,
				new UsernamePasswordCredentials(rioSearchServerConfig.getUsername(), rioSearchServerConfig.getPwd()));

		// prepare the rest client for Farm
		RestClientBuilder builder = RestClient
				.builder(new HttpHost(rioSearchServerConfig.getHost(), Integer.valueOf(rioSearchServerConfig.getPort()),
						rioSearchServerConfig.getProtocol()))
				.setMaxRetryTimeoutMillis(rioSearchServerConfig.getMaxRetryTimeout())
				.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {

					@Override
					public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
						if (Doc360Constants.HTTPS.equals(rioSearchServerConfig.getProtocol())) {
							return httpClientBuilder.setSSLContext(sslContext)
									.setDefaultCredentialsProvider(credentialsProvider);
						} else {
							return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
						}
					}
				}).setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {

					@Override
					public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
						return requestConfigBuilder.setConnectTimeout(rioSearchServerConfig.getConnectTimeout())
								.setSocketTimeout(rioSearchServerConfig.getSocketTimeout())
								.setConnectionRequestTimeout(rioSearchServerConfig.getConnectionRequestTimeout());
					}
				});
		this.rioRestHighLevelClient = new RestHighLevelClient(builder);
		this.rioRestClient = this.rioRestHighLevelClient.getLowLevelClient();
		// this.rioRestClient = builder.build();
		// this.rioRestHighLevelClient = new
		// RestHighLevelClient(this.rioRestClient);
	}

	/**
	 * Build the Rio Alt Two rest client based on configuration details.
	 */
	private void buildRioAltTwoRestClient() {
		ElasticSearchServerConfig rioAltTwoSearchServerConfig = rioAltTwoSearchServerConfig();
		// SET AUTHENTICATION
		final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(
				rioAltTwoSearchServerConfig.getUsername(), rioAltTwoSearchServerConfig.getPwd()));

		// prepare the rest client for Farm
		RestClientBuilder builder = RestClient.builder(new HttpHost(rioAltTwoSearchServerConfig.getHost(),
				Integer.valueOf(rioAltTwoSearchServerConfig.getPort()), rioAltTwoSearchServerConfig.getProtocol()))
				.setMaxRetryTimeoutMillis(rioAltTwoSearchServerConfig.getMaxRetryTimeout())
				.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {

					@Override
					public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
						if (Doc360Constants.HTTPS.equals(rioAltTwoSearchServerConfig.getProtocol())) {
							return httpClientBuilder.setSSLContext(sslContext)
									.setDefaultCredentialsProvider(credentialsProvider);
						} else {
							return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
						}
					}
				}).setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {

					@Override
					public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
						return requestConfigBuilder.setConnectTimeout(rioAltTwoSearchServerConfig.getConnectTimeout())
								.setSocketTimeout(rioAltTwoSearchServerConfig.getSocketTimeout())
								.setConnectionRequestTimeout(rioAltTwoSearchServerConfig.getConnectionRequestTimeout());
					}
				});
		this.rioAltTwoRestHighLevelClient = new RestHighLevelClient(builder);
		this.rioAltTwoRestClient = this.rioAltTwoRestHighLevelClient.getLowLevelClient();
		// this.rioAltTwoRestClient = builder.build();
		// this.rioAltTwoRestHighLevelClient = new
		// RestHighLevelClient(this.rioAltTwoRestClient);
	}

	/**
	 * Build the Boneyard rest client based on configuration details.
	 */
	private void buildBoneyardRestClient() {
		ElasticSearchServerConfig boneyardSearchServerConfig = boneyardSearchServerConfig();
		// SET AUTHENTICATION
		final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(
				boneyardSearchServerConfig.getUsername(), boneyardSearchServerConfig.getPwd()));

		// prepare the rest client for Farm
		RestClientBuilder builder = RestClient.builder(new HttpHost(boneyardSearchServerConfig.getHost(),
				Integer.valueOf(boneyardSearchServerConfig.getPort()), boneyardSearchServerConfig.getProtocol()))
				.setMaxRetryTimeoutMillis(boneyardSearchServerConfig.getMaxRetryTimeout())
				.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {

					@Override
					public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
						if (Doc360Constants.HTTPS.equals(boneyardSearchServerConfig.getProtocol())) {
							return httpClientBuilder.setSSLContext(sslContext)
									.setDefaultCredentialsProvider(credentialsProvider);
						} else {
							return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
						}
					}
				}).setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {

					@Override
					public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
						return requestConfigBuilder.setConnectTimeout(boneyardSearchServerConfig.getConnectTimeout())
								.setSocketTimeout(boneyardSearchServerConfig.getSocketTimeout())
								.setConnectionRequestTimeout(boneyardSearchServerConfig.getConnectionRequestTimeout());
					}
				});
		this.boneyardRestHighLevelClient = new RestHighLevelClient(builder);
		this.boneyardRestClient = this.boneyardRestHighLevelClient.getLowLevelClient();
		// this.boneyardRestClient = builder.build();
		// this.boneyardRestHighLevelClient = new
		// RestHighLevelClient(this.boneyardRestClient);
	}

	/**
	 * Provide the Farm Rest client bean.
	 * 
	 * @return RestClient
	 */
	@Bean
	public RestClient farmRestClient() {
		return this.farmRestClient;
	}

	/**
	 * Provide the Rio Alt Two Rest client bean.
	 * 
	 * @return RestClient
	 */
	@Bean
	public RestClient rioAltTwoRestClient() {
		return this.rioAltTwoRestClient;
	}

	/**
	 * Provide the Rio Alt Three Rest client bean.
	 * 
	 * @return RestClient
	 */
	@Bean
	public RestClient rioAltThreeRestClient() {
		return this.rioAltThreeRestClient;
	}

	/**
	 * Provide the Rio Rest client bean.
	 * 
	 * @return RestClient
	 */
	@Bean
	public RestClient rioRestClient() {
		return this.rioRestClient;
	}

	/**
	 * Provide the Boneyard Rest client bean.
	 * 
	 * @return RestClient
	 */
	@Bean
	public RestClient boneyardRestClient() {
		return this.boneyardRestClient;
	}

	/**
	 * Provide the Farm Rest high level client bean.
	 * 
	 * @return RestHighLevelClient
	 */
	@Bean
	public RestHighLevelClient farmRestHighLevelClient() {
		return this.farmRestHighLevelClient;
	}

	/**
	 * Provide the Rio Rest high level client bean.
	 * 
	 * @return RestHighLevelClient
	 */
	@Bean
	public RestHighLevelClient rioRestHighLevelClient() {
		return this.rioRestHighLevelClient;
	}

	/**
	 * Provide the Rio Alt Two Rest high level client bean.
	 * 
	 * @return RestHighLevelClient
	 */
	@Bean
	public RestHighLevelClient rioAltTwoRestHighLevelClient() {
		return this.rioAltTwoRestHighLevelClient;
	}

	/**
	 * Provide the Rio Alt Thrree Rest high level client bean.
	 * 
	 * @return RestHighLevelClient
	 */
	@Bean
	public RestHighLevelClient rioAltThreeRestHighLevelClient() {
		return this.rioAltThreeRestHighLevelClient;
	}

	/**
	 * Provide the Boneyard Rest high level client bean.
	 * 
	 * @return RestHighLevelClient
	 */
	@Bean
	public RestHighLevelClient boneyardRestHighLevelClient() {
		return this.boneyardRestHighLevelClient;
	}

	/**
	 * Build the Rio Alt Three rest client based on configuration details.
	 */
	private void buildRioAltThreeRestClient() {
		ElasticSearchServerConfig rioAltThreeSearchServerConfig = rioAltThreeSearchServerConfig();
		// SET AUTHENTICATION
		final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(
				rioAltThreeSearchServerConfig.getUsername(), rioAltThreeSearchServerConfig.getPwd()));

		// prepare the rest client for Farm
		RestClientBuilder builder = RestClient.builder(new HttpHost(rioAltThreeSearchServerConfig.getHost(),
				Integer.valueOf(rioAltThreeSearchServerConfig.getPort()), rioAltThreeSearchServerConfig.getProtocol()))
				.setMaxRetryTimeoutMillis(rioAltThreeSearchServerConfig.getMaxRetryTimeout())
				.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {

					@Override
					public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
						if (Doc360Constants.HTTPS.equals(rioAltThreeSearchServerConfig.getProtocol())) {
							return httpClientBuilder.setSSLContext(sslContext)
									.setDefaultCredentialsProvider(credentialsProvider);
						} else {
							return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
						}
					}
				}).setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {

					@Override
					public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
						return requestConfigBuilder.setConnectTimeout(rioAltThreeSearchServerConfig.getConnectTimeout())
								.setSocketTimeout(rioAltThreeSearchServerConfig.getSocketTimeout())
								.setConnectionRequestTimeout(
										rioAltThreeSearchServerConfig.getConnectionRequestTimeout());
					}
				});
		this.rioAltThreeRestHighLevelClient = new RestHighLevelClient(builder);
		this.rioAltThreeRestClient = this.rioAltThreeRestHighLevelClient.getLowLevelClient();
		// this.rioAltThreeRestClient = builder.build();
		// this.rioAltThreeRestHighLevelClient = new
		// RestHighLevelClient(this.rioAltThreeRestClient);
	}
}
