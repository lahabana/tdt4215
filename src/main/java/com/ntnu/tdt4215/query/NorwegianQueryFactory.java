package com.ntnu.tdt4215.query;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.no.NorwegianAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;

public class NorwegianQueryFactory implements QueryFactory {
	Analyzer analyzer = null;
	
	public NorwegianQueryFactory() {
		analyzer = new NorwegianAnalyzer(VERSION);
	}
	
	public Query parse(String querystr) throws ParseException {
		QueryParser qp = new QueryParser(VERSION, "content", analyzer);
		return qp.parse(querystr);
	}

	public void setAnalyzer(Analyzer analyzer) {
		throw new UnsupportedOperationException();
	}

	public Analyzer getAnalyzer() {
		return analyzer;
	}
}
