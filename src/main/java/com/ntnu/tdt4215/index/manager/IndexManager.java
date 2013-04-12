package com.ntnu.tdt4215.index.manager;

import java.io.IOException;
import java.util.Vector;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryparser.classic.ParseException;

import com.ntnu.tdt4215.parser.IndexingFSM;

public interface IndexManager {
	
	/**
	 * Add one document to the document (opens a writer if none are already open)
	 * @param doc
	 * @throws IOException
	 */
	public void addDoc(Document doc) throws IOException;
	
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
	public Vector<Document> getResults(int nbHits, String querystr) throws IOException, ParseException;
	
	/**
	 * Returns the index reader (this is especially useful for singletons)
	 * @return
	 * @throws IOException 
	 */
	IndexReader getReader() throws IOException;

	/**
	 * Returns the index writer (this is especially useful for singletons)
	 * @return
	 */
	IndexWriter getWriter() throws IOException;
	
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
