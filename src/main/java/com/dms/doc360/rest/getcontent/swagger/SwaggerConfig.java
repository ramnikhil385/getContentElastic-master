package com.dms.doc360.rest.getcontent.swagger;

import static springfox.documentation.schema.AlternateTypeRules.newRule;

import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.fasterxml.classmate.TypeResolver;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Prepare the Swagger configuration for this api.
 * 
 * @author Tarun Verma
 *
 */
public class SwaggerConfig {

	@Value("${application.version}")
	private String version;

	/**
	 * Create the Swagger details for this api.
	 * 
	 * @return Docket
	 */
//	@SuppressWarnings("deprecation")
//	@Bean
//	@Autowired
//	public Docket api(TypeResolver typeResolver) {
//		return new Docket(DocumentationType.SWAGGER_2)
////				.apiInfo(new ApiInfo("Doc360 REST API Get Content",
////						"Doc360 REST API for Get Content related functionality", this.version, null, null, null, null))
////				.select()
////				.apis(RequestHandlerSelectors.basePackage("com.optum.dms.doc360.rest.getcontent"))
////				.paths(PathSelectors.any())
////				.build()
////				.genericModelSubstitutes(Callable.class)
////				.alternateTypeRules(newRule(
////						typeResolver.resolve(Callable.class,
////								typeResolver.resolve(ResponseEntity.class, StreamingResponseBody.class)),
////						typeResolver.resolve(StreamingResponseBody.class)));
//	}
}
