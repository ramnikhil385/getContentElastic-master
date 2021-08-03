/**
 * 
 */
package com.dms.doc360.rest.getcontent.oos.config;

import javax.net.ssl.SSLContext;
import javax.validation.constraints.NotBlank;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.dms.doc360.rest.getcontent.utils.Doc360Constants;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Configuration class for Object Storage related settings.
 * 
 * @author Tarun Verma
 *
 */
@Configuration(value = "oosConfig")
@ConfigurationProperties(prefix = "object.storage")
@NoArgsConstructor
@Getter
@Setter
public class OOSConfig {

	@NotBlank
	private String containerEndpoint;

	@NotBlank
	private String containerCredentialsFile;

	@NotBlank
	private int connectTimeout;

	@NotBlank
	private int socketTimeout;

	@NotBlank
	private String vaultEndpoint;

	@NotBlank
	private String vaultPrimaryCredentialsFile;

	@NotBlank
	private String vaultSecondaryCredentialsFile;

	@NotBlank
	private String vaultPrimaryBucketName;

	@NotBlank
	private String vaultSecondaryBucketName;

	@Autowired
	private SSLContext sslContext;

	/**
	 * Provide the AWS S3 client bean for Container bucket.
	 * 
	 * @return AmazonS3
	 */
	@Bean
	public AmazonS3 s3ContainerClient() {
		// setup client configuration e.g. SSL factory, max time out etc
		ClientConfiguration clientConfiguration = new ClientConfiguration().withConnectionTimeout(connectTimeout)
				.withSocketTimeout(socketTimeout);
		clientConfiguration.getApacheHttpClientConfig()
				.setSslSocketFactory(new SSLConnectionSocketFactory(this.sslContext));

		// build S3 client using classpath credentials provider and client
		// configuration
		AmazonS3ClientBuilder s3ClientBuilder = AmazonS3ClientBuilder.standard()
				.withCredentials(new ClasspathPropertiesFileCredentialsProvider(containerCredentialsFile))
				.withPathStyleAccessEnabled(true);
		s3ClientBuilder.setClientConfiguration(clientConfiguration);
		s3ClientBuilder.setEndpointConfiguration(
				new AwsClientBuilder.EndpointConfiguration(containerEndpoint, Doc360Constants.AWS_S3_DEFAULT_REGION));
		return s3ClientBuilder.build();
	}

	/**
	 * Provide the AWS S3 client bean for Vault Primary bucket.
	 * 
	 * @return AmazonS3
	 */
	@Bean
	public AmazonS3 s3VaultPrimaryClient() {
		// setup client configuration e.g. SSL factory, max time out etc
		ClientConfiguration clientConfiguration = new ClientConfiguration().withConnectionTimeout(connectTimeout)
				.withSocketTimeout(socketTimeout);
		clientConfiguration.getApacheHttpClientConfig()
				.setSslSocketFactory(new SSLConnectionSocketFactory(this.sslContext));

		// build S3 client using classpath credentials provider and client
		// configuration
		AmazonS3ClientBuilder s3ClientBuilder = AmazonS3ClientBuilder.standard()
				.withCredentials(new ClasspathPropertiesFileCredentialsProvider(vaultPrimaryCredentialsFile))
				.withPathStyleAccessEnabled(true);
		s3ClientBuilder.setClientConfiguration(clientConfiguration);
		s3ClientBuilder.setEndpointConfiguration(
				new AwsClientBuilder.EndpointConfiguration(vaultEndpoint, Doc360Constants.AWS_S3_DEFAULT_REGION));
		return s3ClientBuilder.build();
	}

	/**
	 * Provide the AWS S3 client bean for Vault Secondary bucket.
	 * 
	 * @return AmazonS3
	 */
	@Bean
	public AmazonS3 s3VaultSecondaryClient() {
		// setup client configuration e.g. SSL factory, max time out etc
		ClientConfiguration clientConfiguration = new ClientConfiguration().withConnectionTimeout(connectTimeout)
				.withSocketTimeout(socketTimeout);
		clientConfiguration.getApacheHttpClientConfig()
				.setSslSocketFactory(new SSLConnectionSocketFactory(this.sslContext));

		// build S3 client using classpath credential provider and client
		// configuration
		AmazonS3ClientBuilder s3ClientBuilder = AmazonS3ClientBuilder.standard()
				.withCredentials(new ClasspathPropertiesFileCredentialsProvider(vaultSecondaryCredentialsFile))
				.withPathStyleAccessEnabled(true);
		s3ClientBuilder.setClientConfiguration(clientConfiguration);
		s3ClientBuilder.setEndpointConfiguration(
				new AwsClientBuilder.EndpointConfiguration(vaultEndpoint, Doc360Constants.AWS_S3_DEFAULT_REGION));
		return s3ClientBuilder.build();
	}

}
