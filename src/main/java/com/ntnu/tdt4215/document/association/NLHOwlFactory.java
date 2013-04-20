package com.ntnu.tdt4215.document.association;

import java.util.Collection;

import com.ntnu.tdt4215.index.ScoredDocument;
import com.ntnu.tdt4215.index.queryFactory.QueryFactory;

public interface NLHOwlFactory {

	/**
	 * Create the correct document
	 * @param title
	 * @param content
	 * @return
	 */
	public AbstractNLHOwl create(String title, Collection<ScoredDocument> content);
	
	/**
	 * Returns the queryFactory adapted to the documents this factory creates
	 * @return
	 */
	public QueryFactory getQueryFactory();
}
