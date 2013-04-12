package com.ntnu.tdt4215.query;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;

public class SimpleQueryFactory implements QueryFactory {

	Analyzer analyzer = null;
	Version version = null;
	
	public SimpleQueryFactory(Analyzer analyzer, Version version) {
		setAnalyzer(analyzer);
		setVersion(version);
	}

	public Query parse(String querystr) throws ParseException {
		QueryParser qp = new QueryParser(version, "content", analyzer);
		return qp.parse(querystr);
	}

	public void setAnalyzer(Analyzer analyzer) {
		this.analyzer = analyzer;
	}
	
	public void setVersion(Version version) {
		this.version = version;
	}

	public Analyzer getAnalyzer() {
		return analyzer;
	}
	
}
