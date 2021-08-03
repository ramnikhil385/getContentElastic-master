package com.dms.doc360.rest.getcontent.services.mapper;

/**
 * Mapper interface.
 * 
 * @param <I>
 * @param <O>
 */
public interface Mapper<I, O> {
	public O map(I input);
}
