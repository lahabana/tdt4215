package com.ntnu.tdt4215.query;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;

/**
 * A factory to create queries (used to search for example)
 */
public interface QueryFactory {

	/**
	 * Create a new querybuilder and return the query parsed by this parser
	 * @param querystr
	 * @return
	 * @throws ParseException
	 */
	public Query parse(String querystr) throws ParseException;

	/**
	 * Set the analyzer for the queryBuilder
	 * @param analyzer
	 */
	public void setAnalyzer(Analyzer analyzer);
	
	/**
	 * Set the lucene version
	 * @param version
	 */
	public void setVersion(Version version);

}
