package com.ntnu.tdt4215.document.association;

import java.util.Collection;

import com.ntnu.tdt4215.index.ScoredDocument;
import com.ntnu.tdt4215.index.queryFactory.MultifieldQueryFactory;
import com.ntnu.tdt4215.index.queryFactory.QueryFactory;

public class NLHOwlMultipleFieldsFactory implements NLHOwlFactory {
	QueryFactory qpf = new MultifieldQueryFactory();
	
	public AbstractNLHIcd10 create(String title, Collection<ScoredDocument> content) {
		return new NLHOwlMultipleFields(title, content);
	}

	public QueryFactory getQueryFactory() {
		return qpf;
	}

}
