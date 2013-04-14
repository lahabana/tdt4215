package com.ntnu.tdt4215.searchEngine;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
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
	private static final File FILE = new File("NLHindex");
	private static final String[] FOLDERS = {"Download/G/", "Download/L/", "Download/T/"};

	public NLH() throws IOException {
		super();
		Directory dir = new SimpleFSDirectory(FILE);
		QueryFactory queryFactory = new NorwegianQueryFactory();
		SimpleManager idx = new SimpleManager(dir, queryFactory);
		addIndex("NLHIndex", idx);
	}
	
	public Collection<ScoredDocument> getResults(int maxElt, String queryStr) throws IOException, ParseException {
		return getIndex("NLHIndex").getResults(maxElt, queryStr);
	}

	@Override
	public void clean() throws IOException {
		if (FILE.exists() && FILE.isDirectory()) {
			if (FILE.canWrite()) {
				FileUtils.deleteDirectory(FILE);
			} else {
				throw new IOException("Can't delete the directory:" + FILE.getAbsolutePath());
			}
		} else if(FILE.exists() && !FILE.isDirectory()) {
			throw new IOException("Can't delete:" + FILE.getAbsolutePath() + " it is not a directory");
		}
	}

	@Override
	public void indexAll() throws IOException {
		NLHWebsiteCrawlerFSM fsm = new NLHWebsiteCrawlerFSM(FOLDERS);
		addAll("NLHIndex", fsm);
	}
}
