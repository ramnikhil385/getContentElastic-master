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
 * Configuration class for File Server info.
 * 
 * @author Tarun Verma
 *
 */
@ToString
@NoArgsConstructor
@Getter
@Setter
public class FileServerConfig {
	@NotBlank
	private String basicAuthUserid;

	@NotBlank
	private String basicAuthPwd;

	@NotBlank
	private int connectTimeOut;

	@NotBlank
	private int readTimeOut;

}
