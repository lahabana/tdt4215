package com.ntnu.tdt4215.document.association;

import java.util.Collection;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;

import com.ntnu.tdt4215.index.ScoredDocument;
import com.ntnu.tdt4215.index.queryFactory.QueryFactory;
import com.ntnu.tdt4215.index.queryFactory.SimpleQueryFactory;

public class NLHOwlInlineFactory implements NLHOwlFactory {
	QueryFactory qpf = new SimpleQueryFactory(new WhitespaceAnalyzer(QueryFactory.VERSION));
	
	public AbstractNLHIcd10 create(String title, Collection<ScoredDocument> content) {
		return new NLHOwlInline(title, content);
	}

	public QueryFactory getQueryFactory() {
		return qpf;
	}

}
