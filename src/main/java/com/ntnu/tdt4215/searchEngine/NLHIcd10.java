package com.ntnu.tdt4215.searchEngine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;

import com.ntnu.tdt4215.document.NLHChapter;
import com.ntnu.tdt4215.document.NLHIcd10s;
import com.ntnu.tdt4215.document.ScoredDocument;
import com.ntnu.tdt4215.index.SentenceQueryPolicy;
import com.ntnu.tdt4215.index.manager.MergingManager;
import com.ntnu.tdt4215.index.manager.SimpleManager;
import com.ntnu.tdt4215.parser.Icd10FSM;
import com.ntnu.tdt4215.parser.IndexingFSM;
import com.ntnu.tdt4215.parser.NLHWebsiteCrawlerFSM;
import com.ntnu.tdt4215.query.MultifieldQueryFactory;
import com.ntnu.tdt4215.query.NorwegianQueryFactory;
import com.ntnu.tdt4215.query.QueryFactory;


public class NLHIcd10 extends SearchEngine {
	private QueryFactory norwegianQPF;
	private MultifieldQueryFactory multifieldQPF;
	private MergingManager idxIcd10;
	private SimpleManager idxNLH;
	private SimpleManager idxNLHIcd10;
	private static final File INDEXNLH = new File("indexes/NLH");
	private static final File INDEXICD10 = new File("indexes/icd10");
	private static final File INDEXNLHICD10 = new File("indexes/NLHicd10");

	public NLHIcd10() throws IOException {
		super();
		createQPFs();
		createManagers();
	}

	private void createQPFs() {
		norwegianQPF = new NorwegianQueryFactory();
		multifieldQPF = new MultifieldQueryFactory();
	}
	
	private void createManagers() throws IOException {
		Directory dirIcd10 = new SimpleFSDirectory(INDEXICD10);
		idxIcd10 = new MergingManager(dirIcd10, norwegianQPF);
		addIndex("icd10", idxIcd10);
		
		Directory dirNLH = new SimpleFSDirectory(INDEXNLH);
		idxNLH = new SimpleManager(dirNLH, norwegianQPF);
		addIndex("NLH", idxNLH);
		Directory dirNLHIcd10 = new SimpleFSDirectory(INDEXNLHICD10);
		idxNLHIcd10 = new SimpleManager(dirNLHIcd10, multifieldQPF);
		addIndex("NLHicd10", idxNLHIcd10);
	}

	public Collection<ScoredDocument> getResults(int nbHits, String querystr)
			throws IOException, ParseException {
		multifieldQPF.extractFields(idxNLHIcd10.getReader());
		
		// Get all the icd entries that are close to the patient case
		idxIcd10.setQueryPolicy(new SentenceQueryPolicy(0.5f));
		Collection<ScoredDocument> docs = idxIcd10.getResults(2, querystr);
		Collection<ScoredDocument> icdChapters = null;
		String queryIcd = "";
		// If there are icd entries well look for the entries that we have 
		// linked to a chapter
		if (docs.size() > 0) {
			for (ScoredDocument d : docs) {
				queryIcd += d.getField("id") + " ";
			}
			icdChapters = idxNLHIcd10.getResults(nbHits, queryIcd);
		}
		// Useful to see how much icd contributes to the overall result
		/*System.out.println("icd" + queryIcd);
		if (icdChapters != null) {
			for (ScoredDocument d: icdChapters) {
				System.out.print(d + "/");
			}
			System.out.println();
		}*/
		// Search for chapters matching the patient case
		Collection<ScoredDocument> chapters = idxNLH.getResults(nbHits * 4, querystr);
		ArrayList<ScoredDocument> res = new ArrayList<ScoredDocument>(nbHits);
		// For each document retrieved by full text we increase its ranking if it also appeared in ICD
		for (ScoredDocument sd: chapters) {
			if (icdChapters != null && icdChapters.contains(sd)) {
				sd.setScore(sd.getScore() + 0.1f);// Probably add some sort of reinforcement here
			}
			res.add(sd);
		}
		// Sort the final results
		Collections.sort(res, Collections.reverseOrder());
		return res;
	}

	@Override
	public void clean() throws IOException {
		deleteDirectory(INDEXNLH);
		deleteDirectory(INDEXICD10);
		deleteDirectory(INDEXNLHICD10);
	}

	@Override
	public void indexAll() throws IOException {
		IndexingFSM icd10fsm = new Icd10FSM("documents/icd10no.owl");
		addAll("icd10", icd10fsm);
		
		idxIcd10.setQueryPolicy(new SentenceQueryPolicy(0f));
		IndexingFSM NLHfsm = new NLHWebsiteCrawlerFSM("documents/NLH/T/");
		NLHfsm.initialize();
		while (NLHfsm.hasNext()) {
			NLHChapter chap = (NLHChapter) NLHfsm.next();
			try {
				// We look for entries in icd10 that match the chapter
				Collection<ScoredDocument> res = idxIcd10.getResults(1, chap.getContent());
				// We add these entries inside an index
				if (res.size() > 0) {
					idxNLHIcd10.addDoc(new NLHIcd10s(chap.getTitle(), res).getDocument());
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
