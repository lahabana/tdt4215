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
import com.ntnu.tdt4215.index.MultiplequeryPolicy.MultipleQueryPolicy;
import com.ntnu.tdt4215.index.queryFactory.QueryFactory;

public class MergingManager extends LuceneAbstractManager {

	MultipleQueryPolicy mergePolicy = null;
	public MergingManager(Directory dir, QueryFactory qpf) {
		super(dir, qpf);
	}

	public void setQueryPolicy(MultipleQueryPolicy mergePol) {
		mergePolicy = mergePol;
	}

	public Collection<ScoredDocument> getResults(int nbHits, String queryStr)
			throws IOException {
		if (mergePolicy == null) {
			throw new IOException("No merge policy set");
		}
		ScoredDocument.resetMaxOccurence();
	    IndexSearcher searcher = new IndexSearcher(getReader());	
	    ArrayList<String> queries = mergePolicy.splitQuery(queryStr);
	    for (String query : queries) {
	    	try {
		    	TopScoreDocCollector collector = TopScoreDocCollector.create(nbHits, true);
					searcher.search(queryFactory.parse(QueryParser.escape(query)), collector);
		    	ScoreDoc[] hits = collector.topDocs().scoreDocs;
		    	for (int i=0; i<hits.length; ++i) {
		    		mergePolicy.map(searcher.doc(hits[i].doc), hits[i].score);
		    	}
	    	} catch (ParseException e) {
				System.err.println("Couldn't parse the query:" + query);
			}
	    }
	    Collection<ScoredDocument> res = mergePolicy.reduce(nbHits);
	    ScoredDocument.resetMaxOccurence();
	    return res;
	}

}
