package com.ntnu.tdt4215.index.manager;

import java.io.IOException;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

import com.ntnu.tdt4215.document.IndexableDocument;
import com.ntnu.tdt4215.index.queryFactory.QueryFactory;
import com.ntnu.tdt4215.parser.IndexingFSM;

abstract public class LuceneAbstractManager implements IndexManager {

	Directory index;
	private IndexWriter currentWriter = null;
	private IndexReader currentReader = null;
	QueryFactory queryFactory = null;
	private static final Version VERSION = Version.LUCENE_40;
	
	/**
	 * Build a manager by Inversion of Control
	 * @param dir
	 * @param analyzer2
	 */
	public LuceneAbstractManager(Directory dir, QueryFactory qpf) {
	    index = dir;
	    queryFactory = qpf;
	}
	
	/**
	 * Add one document to the document (opens a writer if none are already open)
	 * @param doc
	 * @throws IOException
	 */
	public void addDoc(IndexableDocument doc) throws IOException {
		getWriter().addDocument(doc.getDocument());
	}
	
	/**
	 * Add all the documents in the finite state machine and closes the writer.
	 * @param <T>
	 * @param fsm
	 * @throws IOException
	 */
	public void addAll(IndexingFSM fsm) throws IOException {
		fsm.initialize();
		while(fsm.hasNext()) {
			this.addDoc(fsm.next());
		}
		fsm.finish();
		this.commit();
	}
	
	/**
	 * Returns the index reader (this is especially useful for singletons)
	 * @return
	 * @throws IOException 
	 */
	public IndexReader getReader() throws IOException {
		if (currentReader == null) {
			currentReader = DirectoryReader.open(index);
		}
		return currentReader;
	}

	/**
	 * Returns the index writer (this is especially useful for singletons)
	 * @return
	 */
	public IndexWriter getWriter() throws IOException {
		if (currentWriter == null) {
			IndexWriterConfig config = new IndexWriterConfig(VERSION, queryFactory.getAnalyzer());
		    currentWriter = new IndexWriter(index, config);
		}
		return currentWriter;
	}
	
	/**
	 * Close the writer
	 * @throws IOException
	 */
	public void commit() throws IOException {
		if (currentWriter != null) {
			currentWriter.close();
			currentWriter = null;
		}
		if (currentReader != null) {
			currentReader.close();
			currentReader = null;
		}
	}
}
