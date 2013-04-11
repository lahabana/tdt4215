package com.ntnu.tdt4215.index;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;

import com.ntnu.tdt4215.parser.IndexingFSM;
import com.ntnu.tdt4215.query.QueryFactory;

/**
 * A class to encapsulate multiple Directory managers
 * it will have to put the results of all the indexes together in the method
 * getResults()
 * @author charlymolter
 *
 */
abstract public class MultipleIndexManager {
	Hashtable<String, DirectoryManager> indexes = new Hashtable<String, DirectoryManager>(); 
	Directory index;
	Analyzer analyzer;
	QueryFactory queryFactory;
	
	public MultipleIndexManager(Directory index2, Analyzer analyzer2, QueryFactory qpf) {
		index = index2;
		analyzer = analyzer2;
		queryFactory = qpf;
	}
	
	/**
	 * Add an new index to the directory manager
	 * @param key
	 * @param idx
	 */
	public void addIndex(String key, DirectoryManager idx) {
		indexes.put(key, idx);
	}

	/**
	 * Get the directory manager
	 * @param key
	 * @return
	 */
	public DirectoryManager getIndex(String key) {
		return indexes.get(key);
	}

	/**
	 * Results the results of the query
	 * @param maxElt
	 * @param queryStr
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	abstract public Vector<Document> getResults(int maxElt, String queryStr) throws IOException, ParseException;

	/**
	 * Add all the elements of the fsm to the index indentified by key
	 * @param key
	 * @param fsm
	 * @throws IOException
	 */
	public void addAll(String key, IndexingFSM fsm) throws IOException {
		DirectoryManager dm = indexes.get(key);
		if (dm == null) {
			throw new IllegalArgumentException();
		}
		dm.addAll(fsm);
	}
}
