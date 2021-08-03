package com.dms.doc360.rest.getcontent.caching;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * Provide methods to clear specific cache, or all caches.
 * 
 * @author Deepak Bhatt
 *
 */
@Service
@Slf4j
public class CachingService {

	@Autowired
	CacheManager cacheManager;

	/**
	 * Clear cache based on cache name
	 * 
	 * @param cacheName
	 */
	public void evictAllCacheValues(String cacheName) {
		log.info("Clear cache based on cache name {}", cacheName);
		cacheManager.getCache(cacheName).clear();
	}

	/**
	 * Clear all the cache in this application.
	 */
	@Scheduled(cron = "0 0 * * *")
	public void evictAllCaches() {
		log.info("Clear all cache");
		cacheManager.getCacheNames().stream().forEach(cacheName -> cacheManager.getCache(cacheName).clear());
	}

}