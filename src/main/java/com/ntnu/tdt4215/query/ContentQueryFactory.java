package com.ntnu.tdt4215.query;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;

/**
 * An abstract queryFactory that has as a default field content
 * @author charlymolter
 *
 */
abstract public class ContentQueryFactory implements QueryFactory {
	Analyzer analyzer;
	
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
	
	public void prepare(IndexReader reader) {
		return;
	}
}
