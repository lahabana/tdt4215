package com.ntnu.tdt4215.document;

import org.apache.lucene.document.Document;

/**
 * A class that holds a Lucene Document and its score
 *
 */
public class ScoredDocument implements Comparable<ScoredDocument> {
	protected Document doc;
	protected float score;
	public static int maxOccurence = 1;
	private int occurence = 1;
	
	public ScoredDocument(Document d, float sc) {
		doc = d;
		score = sc;
	}
	
	public float getScore() {
		return score;
	}
	
	public float getNormalizedScore() {
		return score/maxOccurence;
	}
	
	public static void resetMaxOccurence() {
		maxOccurence = 1;
	}
	
	public Document getDocument() {
		return doc;
	}
	
	public String getField(String id) {
		return doc.get(id);
	}
	
	public void setScore(float d) {
		score = d;
	}
	
	public void addScore(float d) {
		occurence++;
		if (occurence > maxOccurence) {
			maxOccurence = occurence;
		}
		score += d;
	}

	public int compareTo(ScoredDocument o) {
		float scoreT = getNormalizedScore();
		float scoreO = o.getNormalizedScore();
		return (scoreT == scoreO) ? 0 : (scoreT > scoreO ? 1 : -1);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof ScoredDocument) {
			return doc.get("id") == ((ScoredDocument) o).doc.get("id");
		}
		return false;
	}
	
	@Override
	public String toString() {
		String id = doc.get("id");
		return (id == null ? doc.get("title") : doc.get("id")) + ":" + score;
	}
}
