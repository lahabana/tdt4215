package com.ntnu.tdt4215.index.manager;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.no.NorwegianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

import com.ntnu.tdt4215.parser.IndexingFSM;
import com.ntnu.tdt4215.parser.NLHWebsiteCrawlerFSM;
import com.ntnu.tdt4215.query.QueryFactory;
import com.ntnu.tdt4215.query.SimpleQueryFactory;

/**
 * A simple manager that just has a NLHChapter index
 * @author charlymolter
 *
 */
public class NLHOnlyMultipleIndexManager extends MultipleIndexManager {

	Directory dir;
	Analyzer analyzer;
	QueryFactory queryFactory;
	private static final Version VERSION = Version.LUCENE_40;
	private static final File FILE = new File("NLHindex");
	private static final String[] FOLDERS = {"Download/G/", "Download/L/", "Download/T/"};

	public NLHOnlyMultipleIndexManager() throws IOException {
		super();
		analyzer = new NorwegianAnalyzer(VERSION);
		dir = new SimpleFSDirectory(FILE);
		queryFactory = new SimpleQueryFactory();
		queryFactory.setAnalyzer(analyzer);
	    queryFactory.setVersion(VERSION);
		SimpleManager idx = new SimpleManager(dir, analyzer, queryFactory);
		addIndex("NLHIndex", idx);
	}
	
	public Vector<Document> getResults(int maxElt, String queryStr) throws IOException, ParseException {
		return getIndex("NLHIndex").getResults(maxElt, queryStr);
	}

	@Override
	public void addAll(IndexingFSM fsm) throws IOException {
		getIndex("NLHIndex").addAll(fsm);
	}

	@Override
	public void clean() throws IOException {
		if (FILE.exists() && FILE.isDirectory()) {
			if (FILE.canWrite()) {
				FileUtils.deleteDirectory(FILE);
			} else {
				throw new IOException("Can't delete the directory:" + FILE.getAbsolutePath());
			}
		} else if(!FILE.isDirectory()) {
			throw new IOException("Can't delete:" + FILE.getAbsolutePath() + " it is not a directory");
		}
	}

	@Override
	public void indexAll() throws IOException {
		NLHWebsiteCrawlerFSM fsm = new NLHWebsiteCrawlerFSM(FOLDERS);
		addAll("NLHIndex", fsm);
	}
}
