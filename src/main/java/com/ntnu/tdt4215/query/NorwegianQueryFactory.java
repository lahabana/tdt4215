package com.ntnu.tdt4215.query;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.no.NorwegianAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;

public class NorwegianQueryFactory implements QueryFactory {

	Analyzer analyzer = null;
	public static Version VERSION = Version.LUCENE_40;
	
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

	public void setVersion(Version version) {
		throw new UnsupportedOperationException();
	}

}
