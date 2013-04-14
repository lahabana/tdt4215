package com.ntnu.tdt4215.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;

import com.ntnu.tdt4215.document.ScoredDocument;

/**
 * A MultipleQueryPolicy that will split the query in separated sentences
 * and then returns the result according to the chapters that where the most frequent
 * (This code should be optimized)
 *
 */
public class SentenceQueryPolicy implements MultipleQueryPolicy {
	Hashtable<String, ScoredDocument> docs = new Hashtable<String, ScoredDocument>();

	public ArrayList<String> splitQuery(String query) {
		String[] sentences = query.split("[\\.\\!\\?]");
		ArrayList<String> arr = new ArrayList<String>();
		for (int i = 0; i < sentences.length; i++) {
			sentences[i] = sentences[i].trim();
			if (sentences[i].length() > 0) {
				arr.add(sentences[i]);
			}
		}
		return arr;
	}

	public void map(ScoredDocument doc) {
		String id = doc.getField("id");
		if (docs.get(id) != null) {
			ScoredDocument d2 = docs.get(id);
			d2.setScore(doc.getScore() + d2.getScore());
		} else {
			docs.put(id, doc);
		}
	}

	public Collection<ScoredDocument> reduce(int nbHits) {
		HashSet<ScoredDocument> res = new HashSet<ScoredDocument>(docs.size());
		Enumeration<ScoredDocument> enumer = docs.elements();
		while (enumer.hasMoreElements()) {
			res.add(enumer.nextElement());
		}
		docs = new Hashtable<String, ScoredDocument>();
		return res;
	}

}