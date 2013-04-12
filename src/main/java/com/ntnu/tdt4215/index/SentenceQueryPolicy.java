package com.ntnu.tdt4215.index;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.PriorityQueue;
import java.util.Vector;

import org.apache.lucene.document.Document;

/**
 * A MultipleQueryPolicy that will split the query in separated sentences
 * and then returns the result according to the chapters that where the most frequent
 * (This code should be optimized)
 *
 */
public class SentenceQueryPolicy implements MultipleQueryPolicy {
	Hashtable<String, Integer> results = new Hashtable<String, Integer>();
	Hashtable<String, Document> docs = new Hashtable<String, Document>();

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

	public void map(Document doc) {
		String id = doc.get("id");
		if (results.get(id) != null) {
			results.put(id, results.get(id) + 1);
		} else {
			results.put(id, 1);
			docs.put(id, doc);
		}
	}

	public Vector<Document> reduce(int nbHits) {
		Enumeration<String> keys = results.keys();
		PriorityQueue<Elt> pq = new PriorityQueue<Elt>();
		while (keys.hasMoreElements()) {
			String id = keys.nextElement();
			results.get(id);
			pq.add(new Elt(id, results.get(id)));
		}
		Vector<Document> res = new Vector<Document>();
		for (int i = 0; i < nbHits && !pq.isEmpty(); i++) {
			String id = pq.remove().getId();
			res.add(docs.get(id));
		}
		results = new Hashtable<String, Integer>();
		docs = new Hashtable<String, Document>();
		return res;
	}

}

class Elt implements Comparable<Elt> {
	String id;
	int nb;
	
	public Elt(String id, int nb) {
		this.id = id;
		this.nb = nb;
	}
	
	public String getId() {
		return id;
	}

	public int compareTo(Elt o) {
		return (o.nb == this.nb) ? 0 :
	    	(o.nb > this.nb ? -1 : 1);
	}
	
}