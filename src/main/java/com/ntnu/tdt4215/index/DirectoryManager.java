package com.ntnu.tdt4215.index;

import java.io.IOException;
import java.util.Vector;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

import com.ntnu.tdt4215.document.IndexableDocument;
import com.ntnu.tdt4215.parser.IndexingFSM;
import com.ntnu.tdt4215.query.QueryFactory;

/**
 * A directory manager just makes it easier to deal with Lucene's directory
 * It just encapsulates readers, writers...
 * @param <T>
 */
public class DirectoryManager<T extends IndexableDocument> {
	Directory index;
	StandardAnalyzer analyzer;
	IndexWriter currentWriter = null;
	IndexReader currentReader = null;
	QueryFactory queryFactory = null;
	private static final Version VERSION = Version.LUCENE_40;
	
	/**
	 * Build a manager by Inversion of Control
	 * @param dir
	 * @param analyzer
	 */
	public DirectoryManager(Directory dir, StandardAnalyzer analyzer, QueryFactory qpf) {
	    index = dir;
	    this.analyzer = analyzer;
	    qpf.setAnalyzer(analyzer);
	    qpf.setVersion(VERSION);
	    queryFactory = qpf;
	}
	
	/**
	 * Add one document to the document (opens a writer if none are already open)
	 * @param doc
	 * @throws IOException
	 */
	public void addDoc(Document doc) throws IOException {
		if (currentWriter == null) {
			IndexWriterConfig config = new IndexWriterConfig(VERSION, analyzer);
		    currentWriter = new IndexWriter(index, config);
		}
		currentWriter.addDocument(doc);
	}
	
	/**
	 * Add all the documents in the finite state machine and closes the writer.
	 * @param <T>
	 * @param fsm
	 * @throws IOException
	 */
	public void addAll(IndexingFSM<T> fsm) throws IOException {
		fsm.initialize();
		while(fsm.hasNext()) {
			Document d = fsm.nextDoc();
			this.addDoc(d);
		}
		fsm.finish();
		this.closeWriter();
	}
	
	/**
	 * Close the writer
	 * @throws IOException
	 */
	public void closeWriter() throws IOException {
		if (currentWriter != null) {
			currentWriter.close();
			currentWriter = null;
		}
	}
	
	/**
	 * Returns a vector of documents matching a query 
	 * the query builder used is the one returned by getQueryParser
	 * @param nbHits nb of document returned
	 * @param querystr query string
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public Vector<Document> getResults(int nbHits, String querystr) throws IOException, ParseException {
		if (currentReader == null) {
			currentReader = DirectoryReader.open(index);
		}
	    IndexSearcher searcher = new IndexSearcher(currentReader);	
	    TopScoreDocCollector collector = TopScoreDocCollector.create(nbHits, true);
	    searcher.search(queryFactory.parse(querystr), collector);
	    ScoreDoc[] hits = collector.topDocs().scoreDocs;
	    Vector<Document> matches = new Vector<Document>();
	    for(int i=0;i<hits.length;++i) {
	        matches.add(searcher.doc(hits[i].doc));
	    }
	    return matches;
	}

	/**
	 * Close the reader (necessary if you add new documents)
	 * @throws IOException
	 */
	public void closeReader() throws IOException {
		currentReader.close();
		currentReader = null;
	}
		
}
