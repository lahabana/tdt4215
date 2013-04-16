package com.ntnu.tdt4215.document.factory;

import java.util.Collection;

import com.ntnu.tdt4215.document.AbstractNLHIcd10;
import com.ntnu.tdt4215.document.ScoredDocument;

public interface NLHIcd10Factory {

	public AbstractNLHIcd10 create(String title, Collection<ScoredDocument> content);
}
