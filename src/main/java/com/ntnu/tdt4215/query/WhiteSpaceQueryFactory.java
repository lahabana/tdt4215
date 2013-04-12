package com.ntnu.tdt4215.query;


import org.apache.lucene.analysis.core.WhitespaceAnalyzer;

public class WhiteSpaceQueryFactory extends ContentQueryFactory {
	
	public WhiteSpaceQueryFactory() {
		analyzer = new WhitespaceAnalyzer(VERSION);
	}
}
