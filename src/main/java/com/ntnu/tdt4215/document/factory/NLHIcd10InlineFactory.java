package com.ntnu.tdt4215.document.factory;

import java.util.Collection;

import com.ntnu.tdt4215.document.AbstractNLHIcd10;
import com.ntnu.tdt4215.document.NLHIcd10Inline;
import com.ntnu.tdt4215.document.ScoredDocument;

public class NLHIcd10InlineFactory implements NLHIcd10Factory {

	public AbstractNLHIcd10 create(String title, Collection<ScoredDocument> content) {
		return new NLHIcd10Inline(title, content);
	}

}
