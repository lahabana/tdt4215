package com.ntnu.tdt4215.document;

import java.util.Collection;

import org.apache.lucene.document.Field;

/**
 * A document that holds the ICD10 entries that are the most relevant to the chapter
 * and creates a lucene document with each NLH chapter in a single field and repeated
 * depending on its score
 */
public class NLHIcd10Inline extends AbstractNLHIcd10 {
	
	public NLHIcd10Inline(String title, Collection<ScoredDocument> content) {
		super(title, content);
	}
	
	protected void setContent(Collection<ScoredDocument> content) {
		String res = "";
		for (ScoredDocument d: content) {
			float score = d.getScore();
			String id = d.getField("id");
			for (int i = 0; i < score; i++) {
				res += id + " ";
			}
		}
		Field f = new Field("content", res, ftContent);
		document.add(f);
		this.content = res;
	}
}
