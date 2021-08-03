package com.dms.doc360.rest.getcontent.dfc;

import java.io.Serializable;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Class to keep documentum repository and credentials information.
 * 
 * @author Sudheer Rangaboina
 *
 */
@Configuration
@ConfigurationProperties(prefix = "farm.documentum.repository")
@ToString
@NoArgsConstructor
@Getter
@Setter
public class RepositoryCredentials implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private String superUser;
	private String pwd;
}
