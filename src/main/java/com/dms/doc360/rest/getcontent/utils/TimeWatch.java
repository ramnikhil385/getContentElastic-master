package com.dms.doc360.rest.getcontent.utils;

import java.time.Instant;

/**
 * @author sraganab
 *
 */
public class TimeWatch {

	double starts;

	private TimeWatch() {
		starts = Instant.now().toEpochMilli();
	}

	public static TimeWatch start() {
		return new TimeWatch();
	}

	public TimeWatch reset() {
		starts = Instant.now().toEpochMilli();
		return this;
	}

	public double time() {
		double ends = Instant.now().toEpochMilli();
		return (ends - starts) / 1000;
	}

}
