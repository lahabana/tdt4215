package com.ntnu.tdt4215.index.manager;

import java.io.IOException;
import java.util.Vector;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import com.ntnu.tdt4215.query.QueryFactory;

/**
 * A directory manager just makes it easier to deal with Lucene's directory
 * It just encapsulates readers, writers...
 * @param <T>
 */
public class SimpleManager extends LuceneAbstractManager {
	public SimpleManager(Directory dir, Analyzer analyzer2, QueryFactory qpf) {
		super(dir, analyzer2, qpf);
	}

	public Vector<Document> getResults(int nbHits, String querystr) throws IOException, ParseException {
		if (currentReader == null) {
			currentReader = DirectoryReader.open(index);
		}
	    IndexSearcher searcher = new IndexSearcher(currentReader);	
	    TopScoreDocCollector collector = TopScoreDocCollector.create(nbHits, true);
	    searcher.search(queryFactory.parse(QueryParser.escape(querystr)), collector);
	    ScoreDoc[] hits = collector.topDocs().scoreDocs;
	    Vector<Document> matches = new Vector<Document>();
	    for(int i=0;i<hits.length;++i) {
	        matches.add(searcher.doc(hits[i].doc));
	    }
	    return matches;
	}	
}
