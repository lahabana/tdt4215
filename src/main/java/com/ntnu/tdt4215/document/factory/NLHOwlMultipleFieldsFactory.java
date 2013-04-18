package com.ntnu.tdt4215.document.factory;

import java.util.Collection;

import com.ntnu.tdt4215.document.AbstractNLHIcd10;
import com.ntnu.tdt4215.document.NLHOwlMultipleFields;
import com.ntnu.tdt4215.document.ScoredDocument;
import com.ntnu.tdt4215.query.MultifieldQueryFactory;
import com.ntnu.tdt4215.query.QueryFactory;

public class NLHOwlMultipleFieldsFactory implements NLHOwlFactory {
	QueryFactory qpf = new MultifieldQueryFactory();
	
	public AbstractNLHIcd10 create(String title, Collection<ScoredDocument> content) {
		return new NLHOwlMultipleFields(title, content);
	}

	public QueryFactory getQueryFactory() {
		return qpf;
	}

}
