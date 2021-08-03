/**
 * 
 */
package com.dms.doc360.rest.getcontent.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.dms.doc360.rest.getcontent.caching.CachingService;
import com.dms.doc360.rest.getcontent.utils.Doc360Constants;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * REST API methods for Application.
 * 
 * @author Tarun Verma
 *
 */
@RestController
@RequestMapping(path = "/api/${rest.api.version}", produces = { MediaType.APPLICATION_JSON_VALUE })
public class ApplicationRestController implements Doc360Constants {

	@Value("${application.version}")
	private String applicationVersion;

	@Autowired
	private RequestMappingHandlerMapping requestMappingHandlerMapping;

	@Autowired
	private CachingService cachingService;

	/**
	 * Get application version
	 * 
	 * @return {@link ResponseObject}
	 */
	@ApiOperation(value = "Get application version", tags = { "Version" })
	@ApiImplicitParams({
			@ApiImplicitParam(name = "JWT", value = "Authorization token", required = true, dataType = "string", paramType = "header") })
	@GetMapping("/app/version")
	public String getVersion() {
		return this.applicationVersion;
	}

	/**
	 * Get all the end points for this api.
	 * 
	 * @return Object
	 */
	@ApiOperation(value = "Get all endpoints in this application", tags = { "Endpoint" })
	@ApiImplicitParams({
			@ApiImplicitParam(name = "JWT", value = "Authorization token", required = true, dataType = "string", paramType = "header") })
	@GetMapping("/endpoints")
	public @ResponseBody Object showEndpointsAction() {
		return requestMappingHandlerMapping.getHandlerMethods().keySet().stream()
				.map(t -> (t.getMethodsCondition().getMethods().size() == 0 ? "GET"
						: t.getMethodsCondition().getMethods().toArray()[0]) + " "
						+ t.getPatternsCondition().getPatterns().toArray()[0])
				.toArray();
	}

	/**
	 * Clear all the application caches.
	 * 
	 * Method = GET
	 */
	@ApiOperation(value = "Clear all caches", tags = { "Clear Cache" })
	@ApiImplicitParams({
			@ApiImplicitParam(name = "JWT", value = "Authorization token", required = true, dataType = "string", paramType = "header") })
	@GetMapping("/caches/all/clear")
	public String clearAllCaches() {
		cachingService.evictAllCaches();
		return DONE;
	}

	/**
	 * Clear specific cache.
	 * 
	 * Method = GET
	 * 
	 * @param cacheName
	 */
	@ApiOperation(value = "Clear specific cache", tags = { "Clear Cache" })
	@ApiImplicitParams({
			@ApiImplicitParam(name = "JWT", value = "Authorization token", required = true, dataType = "string", paramType = "header") })
	@GetMapping("/caches/{cacheName}/clear")
	public String clearAllCaches(@PathVariable("cacheName") String cacheName) {
		cachingService.evictAllCacheValues(cacheName);
		return DONE;
	}

}
