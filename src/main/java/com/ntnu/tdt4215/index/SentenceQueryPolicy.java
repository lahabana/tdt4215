package com.ntnu.tdt4215.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.PriorityQueue;

import com.ntnu.tdt4215.document.ScoredDocument;

/**
 * A MultipleQueryPolicy that will split the query in separated sentences
 * and then returns the result according to the chapters that where the most frequent
 * (This code should be optimized)
 *
 */
public class SentenceQueryPolicy implements MultipleQueryPolicy {
	Hashtable<String, Float> results = new Hashtable<String, Float>();
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
		if (results.get(id) != null) {
			results.put(id, results.get(id) + doc.getScore());
		} else {
			results.put(id, doc.getScore());
			docs.put(id, doc);
		}
	}

	public Collection<ScoredDocument> reduce(int nbHits) {
		Enumeration<String> keys = results.keys();
		BoundedPriorityQueue <ScoredDocument> pq = new BoundedPriorityQueue<ScoredDocument>(nbHits);
		while (keys.hasMoreElements()) {
			String id = keys.nextElement();
			pq.add(new ScoredDocument(docs.get(id).getDocument(), results.get(id)));
		}
		results = new Hashtable<String, Float>();
		docs = new Hashtable<String, ScoredDocument>();
		return pq;
	}

}

class BoundedPriorityQueue<T> extends PriorityQueue<T> {
	private static final long serialVersionUID = 1L;
	private int maxItems;
    public BoundedPriorityQueue(int maxItems){
        this.maxItems = maxItems;
    }

    @Override
    public boolean add(T e) {
        boolean success = super.add(e);
        if (!success) {
            return false;
        } else if (this.size() > maxItems) {
        	this.remove(this.toArray()[this.size()-1]);
        }
        return true;
    }
}