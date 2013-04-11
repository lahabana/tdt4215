package com.ntnu.tdt4215.index.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;

import com.ntnu.tdt4215.index.MultipleQueryPolicy;
import com.ntnu.tdt4215.query.QueryFactory;

public class MergingManager extends LuceneAbstractManager {

	MultipleQueryPolicy mergePolicy;
	public MergingManager(Directory dir, Analyzer analyzer2, QueryFactory qpf, MultipleQueryPolicy mergePol) {
		super(dir, analyzer2, qpf);
		mergePolicy = mergePol;
	}

	public Vector<Document> getResults(int nbHits, String queryStr)
			throws IOException, ParseException {
	    IndexSearcher searcher = new IndexSearcher(getReader());	
	    ArrayList<String> queries = mergePolicy.splitQuery(queryStr);
	    for (String query : queries) {
	    	TopScoreDocCollector collector = TopScoreDocCollector.create(nbHits, true);
	    	searcher.search(queryFactory.parse(QueryParser.escape(query)), collector);
	    	ScoreDoc[] hits = collector.topDocs().scoreDocs;
	    	for(int i=0;i<hits.length;++i) {
	    		mergePolicy.map(searcher.doc(hits[i].doc));
	    	}
	    }
	    return mergePolicy.reduce(nbHits);
	}

}
