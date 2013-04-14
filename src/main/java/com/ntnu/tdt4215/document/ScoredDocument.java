package com.ntnu.tdt4215.document;

import org.apache.lucene.document.Document;

/**
 * A class that holds a Lucene Document and its score
 *
 */
public class ScoredDocument implements Comparable<ScoredDocument> {
	protected Document doc;
	protected float score;
	
	public ScoredDocument(Document d, float sc) {
		doc = d;
		score = sc;
	}
	
	public float getScore() {
		return score;
	}
	
	public Document getDocument() {
		return doc;
	}
	
	public String getField(String id) {
		return doc.get(id);
	}

	public int compareTo(ScoredDocument o) {
		return (score == o.score) ? 0 : (score > o.score ? 1 : -1);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof ScoredDocument) {
			return doc.get("id") == ((ScoredDocument) o).doc.get("id");
		}
		return false;
	}

	public void setScore(float d) {
		score = d;
	}
}
