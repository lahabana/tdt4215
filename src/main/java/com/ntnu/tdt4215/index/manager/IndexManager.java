package com.ntnu.tdt4215.index.manager;

import java.io.IOException;
import java.util.Collection;

import com.ntnu.tdt4215.document.IndexableDocument;
import com.ntnu.tdt4215.index.ScoredDocument;
import com.ntnu.tdt4215.parser.IndexingFSM;

public interface IndexManager {
	
	/**
	 * Add one document to the document (opens a writer if none are already open)
	 * @param doc
	 * @throws IOException
	 */
	public void addDoc(IndexableDocument doc) throws IOException;
	
	/**
	 * Add all the documents in the finite state machine and closes the writer.
	 * @param fsm
	 * @throws IOException
	 */
	public void addAll(IndexingFSM fsm) throws IOException;
	
	/**
	 * Returns a vector of documents matching a query 
	 * the query builder used is the one returned by getQueryParser
	 * @param nbHits nb of document returned
	 * @param querystr query string
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public Collection<ScoredDocument> getResults(int nbHits, String querystr) throws IOException;
	
	/**
	 * Get all documents recently added retrievable
	 * @throws IOException
	 */
	public void commit() throws IOException;
}
