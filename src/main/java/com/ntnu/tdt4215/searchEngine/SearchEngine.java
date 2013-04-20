package com.ntnu.tdt4215.searchEngine;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Hashtable;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.queryparser.classic.ParseException;

import com.ntnu.tdt4215.index.ScoredDocument;
import com.ntnu.tdt4215.index.manager.IndexManager;
import com.ntnu.tdt4215.parser.IndexingFSM;

/**
 * A class to encapsulate multiple Directory managers
 * it will have to put the results of all the indexes together in the method
 * getResults()
 * @author charlymolter
 *
 */
abstract public class SearchEngine {
	Hashtable<String, IndexManager> indexes = new Hashtable<String, IndexManager>();
	
	/**
	 * Add an new index to the directory manager
	 * @param key
	 * @param idx
	 */
	public void addIndex(String key, IndexManager idx) {
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
	 * Deletes all the already existing indexes
	 * @throws IOException
	 */
	abstract public void clean() throws IOException;
	
	/**
	 * Returns the results of the queryString the collection can return more results that maxResults
	 * @param maxResults
	 * @param queryString
	 * @return
	 * @throws ParseException 
	 * @throws IOException 
	 */
	abstract public Collection<ScoredDocument> getResults(int maxResults, String queryString) throws IOException, ParseException;

	/**
	 * Index all the documents to the index
	 * @throws IOException
	 */
	abstract public void indexAll() throws IOException;

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

	/**
	 * delete the index present at file
	 * @param file
	 * @throws IOException
	 */
	protected void deleteDirectory(File file) throws IOException {
		if (file.exists() && file.isDirectory()) {
			if (file.canWrite()) {
				FileUtils.deleteDirectory(file);
			} else {
				throw new IOException("Can't delete the directory:" + file.getAbsolutePath());
			}
		} else if(file.exists() && !file.isDirectory()) {
			throw new IOException("Can't delete:" + file.getAbsolutePath() + " it is not a directory");
		}
	}
}
