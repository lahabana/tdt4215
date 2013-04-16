package com.ntnu.tdt4215.document.factory;

import java.util.Collection;

import com.ntnu.tdt4215.document.AbstractNLHIcd10;
import com.ntnu.tdt4215.document.NLHIcd10Inline;
import com.ntnu.tdt4215.document.ScoredDocument;
import com.ntnu.tdt4215.query.QueryFactory;
import com.ntnu.tdt4215.query.WhiteSpaceQueryFactory;

public class NLHIcd10InlineFactory implements NLHIcd10Factory {
	QueryFactory qpf = new WhiteSpaceQueryFactory();
	
	
	public AbstractNLHIcd10 create(String title, Collection<ScoredDocument> content) {
		return new NLHIcd10Inline(title, content);
	}

	public QueryFactory getQueryFactory() {
		return qpf;
	}

}
