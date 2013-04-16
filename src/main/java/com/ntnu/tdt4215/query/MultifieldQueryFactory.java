package com.ntnu.tdt4215.query;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

public class MultifieldQueryFactory implements QueryFactory {
	Analyzer analyzer;
	private String[] fields = null;
	
	public MultifieldQueryFactory() {
		analyzer = new WhitespaceAnalyzer(VERSION);
	}
	
	public void setFieldNames(String[] fields) {
		this.fields = fields;
	}
	
	public Query parse(String querystr) throws ParseException {
		String[] chap = querystr.split(" ");
		BooleanQuery q = new BooleanQuery();
		for (int i = 0; i < fields.length; i++) {
			BooleanQuery subquery = new BooleanQuery();
			for (int j = 0; j < chap.length; j++) {
				subquery.add(new TermQuery(new Term(fields[i], chap[j])), BooleanClause.Occur.SHOULD);
			}
			q.add(new BooleanClause(subquery, BooleanClause.Occur.SHOULD));
		}
		return q;
		
	}

	public void setAnalyzer(Analyzer analyzer) {
		throw new UnsupportedOperationException();
	}

	public Analyzer getAnalyzer() {
		return analyzer;
	}

	public void prepare(IndexReader reader) {
		extractFields(reader);
	}
	
	/**
	 * Extract the fields that exist in the index
	 * @param reader
	 */
	protected void extractFields(IndexReader reader) {
		if (fields == null) { // To guarantee we only do this once
			FieldInfos fi = MultiFields.getMergedFieldInfos(reader);
			fields = new String[fi.size()];
			int i = 0;
			for (FieldInfo f: fi) {
				fields[i] = f.name;
				i++;
			}
		}
	}
}
