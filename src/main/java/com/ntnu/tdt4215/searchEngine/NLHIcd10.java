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
import com.ntnu.tdt4215.document.ScoredDocument;
import com.ntnu.tdt4215.document.factory.NLHIcd10Factory;
import com.ntnu.tdt4215.document.factory.NLHIcd10InlineFactory;
import com.ntnu.tdt4215.index.MultipleQueryPolicy;
import com.ntnu.tdt4215.index.SentenceCountQueryPolicy;
import com.ntnu.tdt4215.index.manager.MergingManager;
import com.ntnu.tdt4215.index.manager.SimpleManager;
import com.ntnu.tdt4215.parser.Icd10FSM;
import com.ntnu.tdt4215.parser.IndexingFSM;
import com.ntnu.tdt4215.parser.NLHWebsiteCrawlerFSM;
import com.ntnu.tdt4215.query.NorwegianQueryFactory;
import com.ntnu.tdt4215.query.QueryFactory;


public class NLHIcd10 extends SearchEngine {
	private MergingManager idxIcd10;
	private SimpleManager idxNLH;
	private SimpleManager idxNLHIcd10;

	private static final File INDEXNLH = new File("indexes/NLH");
	private static final File INDEXICD10 = new File("indexes/icd10");
	private static final File INDEXNLHICD10 = new File("indexes/NLHicd10");

	// Part interesting to configure
	// The way the NLHIcd associations documents are stored in the lucene index
	public NLHIcd10Factory NLHIcd10F = new NLHIcd10InlineFactory(); // NLHIcd10sFactory();
	// The way we split a large document in sentences and return scored results
	public MultipleQueryPolicy icd10MQP = new SentenceCountQueryPolicy(); //new SentenceQueryPolicy(0.2f);
	// The query factory for fulltext text (patientCase, chapters...)
	public QueryFactory fulltextQPF = new NorwegianQueryFactory();
	// The multiplicative factor in the number of results retrieved on the NLHIcd associations
	public int factor_hits_icd = 1;
	// The multiplicative factor in the number of results retrieved on the Fulltext search
	public int factor_hits_ft = 4;
	// How much to increase the score of a NLH Chapter that is retrieved by both methods
	public float boost_icd = 0.5f;
	
	public NLHIcd10() throws IOException {
		super();
		// Create the managers
		Directory dirIcd10 = new SimpleFSDirectory(INDEXICD10);
		idxIcd10 = new MergingManager(dirIcd10, fulltextQPF);
		addIndex("icd10", idxIcd10);
		idxIcd10.setQueryPolicy(icd10MQP);
		
		Directory dirNLH = new SimpleFSDirectory(INDEXNLH);
		idxNLH = new SimpleManager(dirNLH, fulltextQPF);
		addIndex("NLH", idxNLH);
		
		Directory dirNLHIcd10 = new SimpleFSDirectory(INDEXNLHICD10);
		idxNLHIcd10 = new SimpleManager(dirNLHIcd10, NLHIcd10F.getQueryFactory());
		addIndex("NLHicd10", idxNLHIcd10);
	}

	public Collection<ScoredDocument> getResults(int nbHits, String querystr)
			throws IOException, ParseException {
		NLHIcd10F.getQueryFactory().prepare(idxNLHIcd10.getReader());
		// Get the most relevant chapters according to the ICD entries
		Collection<ScoredDocument> icdMatches = getNLHChaptersFromIcd(nbHits * factor_hits_icd, querystr);
		// Get the most relevant ICD chapters in fulltext search
		Collection<ScoredDocument> chapters = idxNLH.getResults(nbHits * factor_hits_ft, querystr);
		// Merge both results
		return mergeResults(chapters, icdMatches);
	}

	/**
	 * Returns the NLH chapters that match the best the query string depending on the icd
	 * @param nbHits
	 * @param querystr
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public Collection<ScoredDocument> getNLHChaptersFromIcd(int nbHits, String querystr) throws IOException, ParseException {
		Collection<ScoredDocument> docs = idxIcd10.getResults(1, querystr);
		Collection<ScoredDocument> icdChapters = null;
		String queryIcd = "";
		// If there are icd entries well look for the entries that we have 
		// linked to a chapter
		if (docs.size() > 0) {
			/*for (ScoredDocument d : docs) {
				queryIcd += d.getField("id") + " ";
				System.out.print(d + "/");
			}
			System.out.println();*/
			if (docs.size() > 0) {
				for (ScoredDocument d : docs) {
					float score = d.getScore();
					String id = d.getField("id");
					for (int i = 0; i < score; i++) {
						queryIcd += id + " ";
					}
				}
			}
			icdChapters = idxNLHIcd10.getResults(nbHits, queryIcd);
		}
		return icdChapters;
	}
	
	/**
	 * Take two collection and merge them together and return a sorted collection of results
	 * @param chapters
	 * @param icdMatches
	 * @return
	 */
	private Collection<ScoredDocument> mergeResults(Collection<ScoredDocument> chapters,
			Collection<ScoredDocument> icdMatches) {
		ArrayList<ScoredDocument> res = new ArrayList<ScoredDocument>();
		// For each document retrieved by full text we increase its ranking if it also appeared in ICD
		for (ScoredDocument sd: chapters) {
			if (icdMatches != null && icdMatches.contains(sd)) {
				sd.setScore(sd.getScore() + boost_icd);// Probably add some sort of reinforcement here
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
		
		IndexingFSM NLHfsm = new NLHWebsiteCrawlerFSM("documents/NLH/T/");
		NLHfsm.initialize();
		while (NLHfsm.hasNext()) {
			NLHChapter chap = (NLHChapter) NLHfsm.next();
			try {
				// We look for entries in icd10 that match the chapter
				Collection<ScoredDocument> res = idxIcd10.getResults(1, chap.getContent());
				// We add these entries inside an index
				if (res.size() > 0) {
					//idxNLHIcd10.addDoc(new NLHIcd10s(chap.getTitle(), res).getDocument());
					idxNLHIcd10.addDoc(NLHIcd10F.create(chap.getTitle(), res).getDocument());
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
