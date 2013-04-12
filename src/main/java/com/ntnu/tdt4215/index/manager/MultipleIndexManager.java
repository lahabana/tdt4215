package com.ntnu.tdt4215.index.manager;

import java.io.IOException;
import java.util.Hashtable;


import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;

import com.ntnu.tdt4215.parser.IndexingFSM;

/**
 * A class to encapsulate multiple Directory managers
 * it will have to put the results of all the indexes together in the method
 * getResults()
 * @author charlymolter
 *
 */
abstract public class MultipleIndexManager implements IndexManager {
	Hashtable<String, IndexManager> indexes = new Hashtable<String, IndexManager>();
	
	/**
	 * Add an new index to the directory manager
	 * @param key
	 * @param idx
	 */
	public void addIndex(String key, SimpleManager idx) {
		indexes.put(key, idx);
	}

	/**
	 * Get the directory manager
	 * @param key
	 * @return
	 */
	public IndexManager getIndex(String key) {
		return indexes.get(key);
	}

	/**
	 * Add all the elements in the fsm to the index identified by "key"
	 * @param key
	 * @param fsm
	 * @throws IOException
	 */
	public void addAll(String key, IndexingFSM fsm) throws IOException {
		IndexManager dm = indexes.get(key);
		if (dm == null) {
			throw new IllegalArgumentException();
		}
		dm.addAll(fsm);
	}

	/**
	 * Deletes all the already existing indexes
	 * @throws IOException
	 */
	abstract public void clean() throws IOException;

	/**
	 * Index all the documents to the index
	 * @throws IOException
	 */
	abstract public void indexAll() throws IOException;
	
	public void closeWriter() throws IOException {
		for (String key : indexes.keySet()) {
			indexes.get(key).closeWriter();
		}
	}

	public void closeReader() throws IOException {
		for (String key : indexes.keySet()) {
			indexes.get(key).closeReader();
		}
	}
	
	public IndexReader getReader() throws IOException {
		throw new UnsupportedOperationException();
	}

	public IndexWriter getWriter() throws IOException {
		throw new UnsupportedOperationException();
	}
	
	public void addAll(IndexingFSM fsm) throws IOException {
		throw new UnsupportedOperationException();
	}
}
