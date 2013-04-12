package com.ntnu.tdt4215.query;

import org.apache.lucene.analysis.no.NorwegianAnalyzer;

public class NorwegianQueryFactory extends ContentQueryFactory {
	
	public NorwegianQueryFactory() {
		analyzer = new NorwegianAnalyzer(VERSION);
	}
}
