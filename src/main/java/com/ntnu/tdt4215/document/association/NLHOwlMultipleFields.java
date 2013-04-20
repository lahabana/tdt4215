package com.ntnu.tdt4215.document.association;

import java.util.Collection;

import org.apache.lucene.document.Field;

import com.ntnu.tdt4215.index.ScoredDocument;

/**
 * A document that holds the ICD10 entries that are the most relevant to the chapter
 * and creates a lucene document with each NLH chapter as a different field
 */
public class NLHOwlMultipleFields extends AbstractNLHIcd10 {
	public static boolean withBoost = false;
	
	public NLHOwlMultipleFields(String title, Collection<ScoredDocument> content) {
		super(title, content);
	}

	protected void setContent(Collection<ScoredDocument> content) {
		String res = "";
		int i = 0;
		for (ScoredDocument d: content) {
			res += d.getField("id") + " "; 
			Field f = new Field("content" + i, d.getField("id"), ftContent);
			if (withBoost) {
				f.setBoost(d.getScore());
			}
			document.add(f);
			i++;
		}
		this.content = res;
	}
}
