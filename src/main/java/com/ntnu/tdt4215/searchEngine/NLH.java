package com.ntnu.tdt4215.searchEngine;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;

import com.ntnu.tdt4215.document.ScoredDocument;
import com.ntnu.tdt4215.index.manager.SimpleManager;
import com.ntnu.tdt4215.parser.NLHWebsiteCrawlerFSM;
import com.ntnu.tdt4215.query.NorwegianQueryFactory;
import com.ntnu.tdt4215.query.QueryFactory;

/**
 * A simple manager that just has a NLHChapter index
 * @author charlymolter
 *
 */
public class NLH extends SearchEngine {
	private static final File FILE = new File("indexes/NLHindex");
	private static final String[] FOLDERS = {"documents/NLH/G/", "documents/NLH/L/", "documents/NLH/T/"};

	public NLH() throws IOException {
		super();
		Directory dir = new SimpleFSDirectory(FILE);
		QueryFactory queryFactory = new NorwegianQueryFactory();
		SimpleManager idx = new SimpleManager(dir, queryFactory);
		addIndex("NLHIndex", idx);
	}
	
	public Collection<ScoredDocument> getResults(int maxElt, String queryStr) throws IOException, ParseException {
		List<ScoredDocument> res = (List<ScoredDocument>) getIndex("NLHIndex").getResults(maxElt, queryStr);
		Collections.sort(res);
		return res;
	}

	@Override
	public void clean() throws IOException {
		deleteDirectory(FILE);
	}

	@Override
	public void indexAll() throws IOException {
		NLHWebsiteCrawlerFSM fsm = new NLHWebsiteCrawlerFSM(FOLDERS);
		addAll("NLHIndex", fsm);
	}
}
