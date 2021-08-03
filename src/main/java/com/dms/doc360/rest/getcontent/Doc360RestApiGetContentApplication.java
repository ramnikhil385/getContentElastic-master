package com.dms.doc360.rest.getcontent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Boot starter for this application.
 * 
 * @author Tarun Verma
 *
 */
@SpringBootApplication
@EnableEncryptableProperties
@EnableAsync
@EnableCaching
@EnableConfigurationProperties
public class Doc360RestApiGetContentApplication {

	public static void main(String[] args) {
		SpringApplication.run(Doc360RestApiGetContentApplication.class, args);
	}

}
