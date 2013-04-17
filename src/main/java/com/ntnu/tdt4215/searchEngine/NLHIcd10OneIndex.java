package com.ntnu.tdt4215.searchEngine;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;

import com.ntnu.tdt4215.document.NLHChapter;
import com.ntnu.tdt4215.document.ScoredDocument;
import com.ntnu.tdt4215.index.MultipleQueryPolicy;
import com.ntnu.tdt4215.index.SentenceCountQueryPolicy;
import com.ntnu.tdt4215.index.manager.MergingManager;
import com.ntnu.tdt4215.index.manager.SimpleManager;
import com.ntnu.tdt4215.parser.Icd10FSM;
import com.ntnu.tdt4215.parser.IndexingFSM;
import com.ntnu.tdt4215.parser.NLHWebsiteCrawlerFSM;
import com.ntnu.tdt4215.query.NorwegianQueryFactory;
import com.ntnu.tdt4215.query.QueryFactory;

public class NLHIcd10OneIndex extends SearchEngine {

	private MergingManager idxIcd10;
	private SimpleManager idxNLH;
	
	private static final File INDEXNLH = new File("indexes/NLH");
	private static final File INDEXICD10 = new File("indexes/icd10");
	
	// The way we split a large document in sentences and return scored results
	public MultipleQueryPolicy icd10MQP = new SentenceCountQueryPolicy(); //new SentenceQueryPolicy(0.2f);
	// The query factory for fulltext text (patientCase, chapters...)
	public QueryFactory fulltextQPF = new NorwegianQueryFactory();
		
	
	public NLHIcd10OneIndex() throws IOException {
		super();
		// Create the managers
		Directory dirIcd10 = new SimpleFSDirectory(INDEXICD10);
		idxIcd10 = new MergingManager(dirIcd10, fulltextQPF);
		addIndex("icd10", idxIcd10);
		idxIcd10.setQueryPolicy(icd10MQP);
		
		Directory dirNLH = new SimpleFSDirectory(INDEXNLH);
		idxNLH = new SimpleManager(dirNLH, fulltextQPF);
		addIndex("NLH", idxNLH);
	}
	
	@Override
	public Collection<ScoredDocument> getResults(int maxResults, String queryString) throws IOException, ParseException {
		Collection<ScoredDocument> icds = idxIcd10.getResults(1, queryString);
		String icdQueryStr = "";
		for (ScoredDocument d: icds) {
			float score = d.getScore();
			String id = d.getField("id");
			for (int i = 0; i < score; i++) {
				icdQueryStr += id + " ";
			}
		}
		String newQuery = "";		
		if (icdQueryStr.length() > 0) {
			newQuery = "content:\"" + QueryParser.escape(queryString) + 
						"\" OR icd:\"" + QueryParser.escape(icdQueryStr) + "\"";
		}
		//System.out.println(newQuery);

		return idxNLH.getResults(maxResults, newQuery);
	}

	@Override
	public void indexAll() throws IOException {
		IndexingFSM icd10fsm = new Icd10FSM("documents/icd10no.owl");
		addAll("icd10", icd10fsm);
		
		IndexingFSM NLHfsm = new NLHWebsiteCrawlerFSM("documents/NLH/T/");
		NLHfsm.initialize();
		while (NLHfsm.hasNext()) {
			NLHChapter chap = (NLHChapter) NLHfsm.next();
			try {
				// We look for entries in icd10 that match the chapter
				Collection<ScoredDocument> res = idxIcd10.getResults(1, chap.getContent());
				// We add these entries inside an index
				if (res.size() > 0) {
					chap.setIcd(res);
				}
				// We add the chapter to the index
				idxNLH.addDoc(chap.getDocument());
			} catch (ParseException e) {
				System.err.println("Couldn't parse properly" + chap.getTitle() +
									". This chapter won't be indexed");
			}
		}
		NLHfsm.finish();
		IndexingFSM NLHfsm2 = new NLHWebsiteCrawlerFSM("documents/NLH/L/");
		addAll("NLH", NLHfsm2);
		IndexingFSM NLHfsm3 = new NLHWebsiteCrawlerFSM("documents/NLH/G/");
		addAll("NLH", NLHfsm3);
		this.closeWriter();

	}
	
	@Override
	public void clean() throws IOException {
		deleteDirectory(INDEXNLH);
		deleteDirectory(INDEXICD10);
	}

}
