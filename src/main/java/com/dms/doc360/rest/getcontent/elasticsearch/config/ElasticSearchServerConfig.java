/**
 * 
 */
package com.dms.doc360.rest.getcontent.elasticsearch.config;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Config domain class to store Elastic Search server info.
 * 
 * @author Tarun Verma
 *
 */
@ToString
@NoArgsConstructor
@Getter
@Setter
public class ElasticSearchServerConfig {

	@NotBlank
	private String username;

	@NotBlank
	private String pwd;

	@NotBlank
	private String superUsername;

	@NotBlank
	private String host;

	@NotBlank
	private String port;

	@NotBlank
	private String protocol;

	@NotBlank
	private int maxRetryTimeout;

	@NotBlank
	private int connectTimeout;

	@NotBlank
	private int socketTimeout;

	@NotBlank
	private int connectionRequestTimeout;

}
