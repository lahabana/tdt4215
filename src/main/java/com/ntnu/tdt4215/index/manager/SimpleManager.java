package com.ntnu.tdt4215.index.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;

import com.ntnu.tdt4215.index.ScoredDocument;
import com.ntnu.tdt4215.index.queryFactory.QueryFactory;

/**
 * A directory manager just makes it easier to deal with Lucene's directory
 * It just encapsulates readers, writers...
 * @param <T>
 */
public class SimpleManager extends LuceneAbstractManager {
	public SimpleManager(Directory dir, QueryFactory qpf) {
		super(dir, qpf);
	}

	public Collection<ScoredDocument> getResults(int nbHits, String querystr) throws IOException {
	    IndexSearcher searcher = new IndexSearcher(getReader());	
	    TopScoreDocCollector collector = TopScoreDocCollector.create(nbHits, true);
	    try {
			searcher.search(queryFactory.parse(QueryParser.escape(querystr)), collector);
		} catch (ParseException e) {
			System.err.println("Couldn't parse the query:" + querystr);
		}
	    ScoreDoc[] hits = collector.topDocs().scoreDocs;
	    ArrayList<ScoredDocument> matches = new ArrayList<ScoredDocument>();
	    for (int i=0;i<hits.length;++i) {
	        matches.add(new ScoredDocument(searcher.doc(hits[i].doc), hits[i].score));
	    }
	    return matches;
	}	
}
