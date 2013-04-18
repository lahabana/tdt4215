package com.ntnu.tdt4215.document.factory;

import java.util.Collection;

import com.ntnu.tdt4215.document.AbstractNLHIcd10;
import com.ntnu.tdt4215.document.NLHOwlInline;
import com.ntnu.tdt4215.document.ScoredDocument;
import com.ntnu.tdt4215.query.QueryFactory;
import com.ntnu.tdt4215.query.WhiteSpaceQueryFactory;

public class NLHOwlInlineFactory implements NLHOwlFactory {
	QueryFactory qpf = new WhiteSpaceQueryFactory();
	
	
	public AbstractNLHIcd10 create(String title, Collection<ScoredDocument> content) {
		return new NLHOwlInline(title, content);
	}

	public QueryFactory getQueryFactory() {
		return qpf;
	}

}
