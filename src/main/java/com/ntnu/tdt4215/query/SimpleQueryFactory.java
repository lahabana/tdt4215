package com.ntnu.tdt4215.query;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;

public class SimpleQueryFactory implements QueryFactory {

	Analyzer analyzer = null;
	
	public SimpleQueryFactory(Analyzer analyzer) {
		setAnalyzer(analyzer);
	}

	public Query parse(String querystr) throws ParseException {
		QueryParser qp = new QueryParser(VERSION, "content", analyzer);
		return qp.parse(querystr);
	}

	public void setAnalyzer(Analyzer analyzer) {
		this.analyzer = analyzer;
	}

	public Analyzer getAnalyzer() {
		return analyzer;
	}

	public void prepare(IndexReader reader) {
		return;
	}
	
}
