package com.ntnu.tdt4215.searchEngine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;

import com.ntnu.tdt4215.document.NLHChapter.NLHChapter;
import com.ntnu.tdt4215.document.NLHChapter.NLHFactory;
import com.ntnu.tdt4215.document.NLHChapter.NLHWebsiteFactory;
import com.ntnu.tdt4215.document.association.NLHOwlFactory;
import com.ntnu.tdt4215.document.association.NLHOwlInlineFactory;
import com.ntnu.tdt4215.document.owl.AtcFactory;
import com.ntnu.tdt4215.document.owl.Icd10Factory;
import com.ntnu.tdt4215.index.MultipleQueryPolicy;
import com.ntnu.tdt4215.index.ScoredDocument;
import com.ntnu.tdt4215.index.SentenceCountQueryPolicy;
import com.ntnu.tdt4215.index.manager.MergingManager;
import com.ntnu.tdt4215.index.manager.SimpleManager;
import com.ntnu.tdt4215.parser.IndexingFSM;
import com.ntnu.tdt4215.parser.NLHWebsiteCrawlerFSM;
import com.ntnu.tdt4215.parser.OwlFSM;
import com.ntnu.tdt4215.query.NorwegianQueryFactory;
import com.ntnu.tdt4215.query.QueryFactory;

/**
 * A searchEngine that look into NLH and enrich with two ontologies (icd10 and Atc) 
 * the link between the ontologies and the NLH chapters are stored in separate indexes
 *
 */
public class SeparateIndexSE extends SearchEngine {
	private MergingManager idxIcd10;
	private MergingManager idxAtc;
	private SimpleManager idxNLH;
	private SimpleManager idxNLHIcd10;
	private SimpleManager idxNLHAtc;

	private static final File INDEXNLH = new File("indexes/NLH");
	private static final File INDEXICD10 = new File("indexes/icd10");
	private static final File INDEXATC = new File("indexes/atc");
	private static final File INDEXNLHATC = new File("indexes/NLHatc");
	private static final File INDEXNLHICD10 = new File("indexes/NLHicd10");

	// Part interesting to configure
	// The way the NLHIcd associations documents are stored in the lucene index
	public NLHOwlFactory NLHOwlF = new NLHOwlInlineFactory(); // NLHIcd10sFactory();
	// The way we split a large document in sentences and return scored results
	public MultipleQueryPolicy sentenceMQP = new SentenceCountQueryPolicy(); //new SentenceQueryPolicy(0.2f);
	// The query factory for fulltext text (patientCase, chapters...)
	public QueryFactory fulltextQPF = new NorwegianQueryFactory();
	// The multiplicative factor in the number of results retrieved on the NLHIcd associations
	public int factor_hits_icd = 1;
	public static int factor_hits_atc = 1;
	// The multiplicative factor in the number of results retrieved on the Fulltext search
	public int factor_hits_ft = 4;
	// How much to increase the score of a NLH Chapter that is retrieved by both methods
	public float boost_icd = 0.05f;
	// How much to increase the score of a NLH Chapter that is retrieved by both methods
	public float boost_atc = 0.05f;

	public SeparateIndexSE() throws IOException {
		super();
		// Create the managers
		Directory dirIcd10 = new SimpleFSDirectory(INDEXICD10);
		idxIcd10 = new MergingManager(dirIcd10, fulltextQPF);
		addIndex("icd10", idxIcd10);
		idxIcd10.setQueryPolicy(sentenceMQP);

		Directory dirAtc = new SimpleFSDirectory(INDEXATC);
		idxAtc = new MergingManager(dirAtc, fulltextQPF);
		addIndex("atc", idxAtc);
		idxAtc.setQueryPolicy(sentenceMQP);

		Directory dirNLH = new SimpleFSDirectory(INDEXNLH);
		idxNLH = new SimpleManager(dirNLH, fulltextQPF);
		addIndex("NLH", idxNLH);

		Directory dirNLHIcd10 = new SimpleFSDirectory(INDEXNLHICD10);
		idxNLHIcd10 = new SimpleManager(dirNLHIcd10, NLHOwlF.getQueryFactory());
		addIndex("NLHicd10", idxNLHIcd10);

		Directory dirNLHatc = new SimpleFSDirectory(INDEXNLHATC);
		idxNLHAtc = new SimpleManager(dirNLHatc, NLHOwlF.getQueryFactory());
		addIndex("NLHatc", idxNLHAtc);
	}

	@Override
	public Collection<ScoredDocument> getResults(int nbHits, String querystr)
			throws IOException, ParseException {
		NLHOwlF.getQueryFactory().prepare(idxNLHIcd10.getReader());
		// Get the most relevant chapters according to the ICD entries
		Collection<ScoredDocument> icdMatches = getNLHChaptersFromOwl(idxNLHIcd10, idxIcd10, nbHits * factor_hits_icd, querystr);
		// Get the most relevant chapters according to the ICD entries
		Collection<ScoredDocument> atcMatches = getNLHChaptersFromOwl(idxNLHAtc, idxAtc, nbHits * factor_hits_atc, querystr);
		// Get the most relevant ICD chapters in fulltext search
		Collection<ScoredDocument> chapters = idxNLH.getResults(nbHits * factor_hits_ft, querystr);
		// Merge both results
		return mergeResults(chapters, icdMatches, atcMatches);
	}

	/**
	 * Returns the NLH chapters that match the best the query string depending on the icd
	 * @param nbHits
	 * @param querystr
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public Collection<ScoredDocument> getNLHChaptersFromOwl(SimpleManager primaryIdx, MergingManager secondaryIdx, 
															int nbHits, String querystr) throws IOException, ParseException {
		Collection<ScoredDocument> docs = secondaryIdx.getResults(1, querystr);
		Collection<ScoredDocument> chapters = null;
		String queryIcd = "";
		// If there are icd entries well look for the entries that we have 
		// linked to a chapter
		if (docs.size() > 0) {
			for (ScoredDocument d : docs) {
				float score = d.getScore();
				String id = d.getField("id");
				for (int i = 0; i < score; i++) {
					queryIcd += id + " ";
				}
			}
			chapters = primaryIdx.getResults(nbHits, queryIcd);
		}
		return chapters;
	}
	
	/**
	 * Take two collection and merge them together and return a sorted collection of results
	 * @param chapters
	 * @param icdMatches
	 * @param atcMatches
	 * @return
	 */
	private Collection<ScoredDocument> mergeResults(Collection<ScoredDocument> chapters,
			Collection<ScoredDocument> icdMatches, Collection<ScoredDocument> atcMatches) {
		ArrayList<ScoredDocument> res = new ArrayList<ScoredDocument>();
		// For each document retrieved by full text we increase its ranking if it also appeared in ICD
		for (ScoredDocument sd: chapters) {
			if (icdMatches != null && icdMatches.contains(sd)) {
				sd.setScore(sd.getScore() + boost_icd);// Probably add some sort of reinforcement here
			}
			if (atcMatches != null && atcMatches.contains(sd)) {
				sd.setScore(sd.getScore() + boost_atc);// Probably add some sort of reinforcement here
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
		deleteDirectory(INDEXATC);
		deleteDirectory(INDEXNLHICD10);
		deleteDirectory(INDEXNLHATC);
	}

	@Override
	public void indexAll() throws IOException {
		System.out.println("Index atc");
		IndexingFSM atcfsm = new OwlFSM("documents/atc_no_ext_corrected.owl", new AtcFactory());
		addAll("atc", atcfsm);
		
		System.out.println("Index icd10");
		IndexingFSM icd10fsm = new OwlFSM("documents/icd10no.owl", new Icd10Factory());
		addAll("icd10", icd10fsm);

		System.out.println("Index NLH");
		NLHFactory factory = new NLHWebsiteFactory();
		String[] folders = {"documents/NLH/T/"};
		IndexingFSM NLHfsm = new NLHWebsiteCrawlerFSM(folders, factory);
		IndexNLH(NLHfsm, idxNLH, idxIcd10, idxNLHIcd10);

		String[] folders2 = {"documents/NLH/L/"};
		NLHfsm = new NLHWebsiteCrawlerFSM(folders2, factory);
		IndexNLH(NLHfsm, idxNLH, idxAtc, idxNLHAtc);

		String[] folders3 = {"documents/NLH/G/"};
		NLHfsm = new NLHWebsiteCrawlerFSM(folders3, factory);
		IndexNLH(NLHfsm, idxNLH, null, null);
		this.closeWriter();
	}

	private void IndexNLH(IndexingFSM NLHfsm, SimpleManager primaryIdx, MergingManager secondaryIdx, SimpleManager linkingIdx) throws IOException {
		NLHfsm.initialize();
		while (NLHfsm.hasNext()) {
			NLHChapter chap = (NLHChapter) NLHfsm.next();
			try {
				if (secondaryIdx != null && linkingIdx != null) {
					// We look for entries in icd10 that match the chapter
					Collection<ScoredDocument> res = secondaryIdx.getResults(1, chap.getContent());
					// We add these entries inside an index
					if (res.size() > 0) {
						linkingIdx.addDoc(NLHOwlF.create(chap.getTitle(), res).getDocument());
					}
				}
				// We add the chapter to the index
				primaryIdx.addDoc(chap.getDocument());
			} catch (ParseException e) {
				System.err.println("Couldn't parse properly" + chap.getTitle() +
									". This chapter won't be indexed");
			}
		}
		NLHfsm.finish();
	}

}
