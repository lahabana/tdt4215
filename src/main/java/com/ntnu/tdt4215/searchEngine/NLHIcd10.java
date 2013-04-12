package com.ntnu.tdt4215.searchEngine;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;

import com.ntnu.tdt4215.index.SentenceQueryPolicy;
import com.ntnu.tdt4215.index.manager.IndexManager;
import com.ntnu.tdt4215.index.manager.MergingManager;
import com.ntnu.tdt4215.index.manager.SimpleManager;
import com.ntnu.tdt4215.parser.Icd10FSM;
import com.ntnu.tdt4215.parser.IndexingFSM;
import com.ntnu.tdt4215.parser.NLHWebsiteCrawlerFSM;
import com.ntnu.tdt4215.query.NorwegianQueryFactory;
import com.ntnu.tdt4215.query.QueryFactory;


public class NLHIcd10 extends SearchEngine {
	private QueryFactory norwegianQPF;
	private static final File INDEXNLH = new File("NLHindex");
	private static final File INDEXICD10 = new File("icd10index");
	private static final String[] FOLDERS = {"Download/G/", "Download/L/", "Download/T/"};

	public NLHIcd10() throws IOException {
		super();
		createNorwegianQPF();
		createManagers();
	}

	private void createNorwegianQPF() {
		norwegianQPF = new NorwegianQueryFactory();
	}
	
	private void createManagers() throws IOException {
		Directory dirNLH = new SimpleFSDirectory(INDEXNLH);
		IndexManager idxNLH = new SimpleManager(dirNLH, norwegianQPF);
		addIndex("NLH", idxNLH);
		Directory dirIcd10 = new SimpleFSDirectory(INDEXICD10);
		IndexManager idxIcd10 = new MergingManager(dirIcd10, norwegianQPF, new SentenceQueryPolicy());
		addIndex("icd10", idxIcd10);
	}

	public Vector<Document> getResults(int nbHits, String querystr)
			throws IOException, ParseException {
		return getIndex("NLH").getResults(nbHits, querystr);
	}

	@Override
	public void clean() throws IOException {
		deleteDirectory(INDEXNLH);
		deleteDirectory(INDEXICD10);
	}

	@Override
	public void indexAll() throws IOException {
		IndexingFSM NLHfsm = new NLHWebsiteCrawlerFSM(FOLDERS);
		addAll("NLH", NLHfsm);
		IndexingFSM icd10fsm = new Icd10FSM("icd10no.owl");
		addAll("icd10", icd10fsm);
	}
	
	/**
	 * delete the index present at file
	 * @param file
	 * @throws IOException
	 */
	private void deleteDirectory(File file) throws IOException {
		if (file.exists() && file.isDirectory()) {
			if (file.canWrite()) {
				FileUtils.deleteDirectory(file);
			} else {
				throw new IOException("Can't delete the directory:" + file.getAbsolutePath());
			}
		} else if(file.exists() && !file.isDirectory()) {
			throw new IOException("Can't delete:" + file.getAbsolutePath() + " it is not a directory");
		}
	}

}
