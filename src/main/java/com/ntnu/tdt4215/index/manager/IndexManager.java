package com.ntnu.tdt4215.index.manager;

import java.io.IOException;
import java.util.Vector;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;

import com.ntnu.tdt4215.parser.IndexingFSM;

public interface IndexManager {
	
	
	/**
	 * Add all the documents in the finite state machine and closes the writer.
	 * @param <T>
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
	public Vector<Document> getResults(int nbHits, String querystr) throws IOException, ParseException;
	
	/**
	 * Close the writer
	 * @throws IOException
	 */
	public void closeWriter() throws IOException;
	
	/**
	 * Close the reader (necessary if you add new documents)
	 * @throws IOException
	 */
	public void closeReader() throws IOException;
}
