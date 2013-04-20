package com.ntnu.tdt4215.searchEngine;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.lucene.analysis.no.NorwegianAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;

import com.ntnu.tdt4215.document.NLHChapter.NLHChapter;
import com.ntnu.tdt4215.document.NLHChapter.NLHFactory;
import com.ntnu.tdt4215.document.NLHChapter.NLHWebsiteFactory;
import com.ntnu.tdt4215.document.owl.AtcFactory;
import com.ntnu.tdt4215.document.owl.Icd10Factory;
import com.ntnu.tdt4215.index.ScoredDocument;
import com.ntnu.tdt4215.index.manager.MergingManager;
import com.ntnu.tdt4215.index.manager.SimpleManager;
import com.ntnu.tdt4215.index.multipleQueryPolicy.MultipleQueryPolicy;
import com.ntnu.tdt4215.index.multipleQueryPolicy.SentenceCountQueryPolicy;
import com.ntnu.tdt4215.index.queryFactory.QueryFactory;
import com.ntnu.tdt4215.index.queryFactory.SimpleQueryFactory;
import com.ntnu.tdt4215.parser.IndexingFSM;
import com.ntnu.tdt4215.parser.NLHWebsiteCrawlerFSM;
import com.ntnu.tdt4215.parser.OwlFSM;

/**
 * A searchEngine that look into NLH and enrich with two ontologies (icd10 and Atc) 
 * the link between the ontologies and the NLH chapters are stored directly in the NLH index
 */
public class SingleIndexSE extends SearchEngine {

	private MergingManager idxIcd10;
	private MergingManager idxAtc;
	private SimpleManager idxNLH;
	
	private static final File INDEXNLH = new File("indexes/NLH");
	private static final File INDEXICD10 = new File("indexes/icd10");
	private static final File INDEXATC = new File("indexes/atc");
	
	// The way we split a large document in sentences and return scored results
	public MultipleQueryPolicy owlMQP = new SentenceCountQueryPolicy(); //new SentenceQueryPolicy(0.2f);
	// The query factory for fulltext text (patientCase, chapters...)
	public QueryFactory fulltextQPF = new SimpleQueryFactory(new NorwegianAnalyzer(QueryFactory.VERSION));

	public SingleIndexSE() throws IOException {
		super();
		// Create the managers
		Directory dirIcd10 = new SimpleFSDirectory(INDEXICD10);
		idxIcd10 = new MergingManager(dirIcd10, fulltextQPF);
		addIndex("icd10", idxIcd10);
		idxIcd10.setQueryPolicy(owlMQP);

		Directory dirAtc = new SimpleFSDirectory(INDEXATC);
		idxAtc = new MergingManager(dirAtc, fulltextQPF);
		addIndex("atc", idxAtc);
		idxAtc.setQueryPolicy(owlMQP);
		
		Directory dirNLH = new SimpleFSDirectory(INDEXNLH);
		idxNLH = new SimpleManager(dirNLH, fulltextQPF);
		addIndex("NLH", idxNLH);
	}
	
	@Override
	public Collection<ScoredDocument> getResults(int maxResults, String queryString) throws IOException, ParseException {
		Collection<ScoredDocument> icds = idxIcd10.getResults(1, queryString);
		String icdQueryStr = makeQueryStringFromOwlResults(icds);

		Collection<ScoredDocument> atcs = idxAtc.getResults(1, queryString);
		String atcQueryStr = makeQueryStringFromOwlResults(atcs);

		String newQuery = "content:\"" + QueryParser.escape(queryString) + "\"";		
		if (icdQueryStr.length() > 0) {
			newQuery +=" OR icd:\"" + QueryParser.escape(icdQueryStr) + "\"";
		}
		if (atcQueryStr.length() > 0) {
			newQuery +=" OR atc:\"" + QueryParser.escape(atcQueryStr) + "\"";			
		}
		//System.out.println(newQuery);

		return idxNLH.getResults(maxResults, newQuery);
	}

	/**
	 * Creates a query string from Owl entries returned by a previous search
	 * @param docs
	 * @return
	 */
	protected String makeQueryStringFromOwlResults(Collection<ScoredDocument> docs) {
		String res = "";
		for (ScoredDocument d: docs) {
			float score = d.getScore();
			String id = d.getField("id");
			for (int i = 0; i < score; i++) {
				res += id + " ";
			}
		}
		return res;
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
		String[] folders = {"documents/NLH/T/"};
		NLHFactory factory = new NLHWebsiteFactory(); 
		IndexingFSM NLHfsm = new NLHWebsiteCrawlerFSM(folders, factory);
		NLHfsm.initialize();
		while (NLHfsm.hasNext()) {
			NLHChapter chap = (NLHChapter) NLHfsm.next();
			// We look for entries in icd10 that match the chapter
			Collection<ScoredDocument> res = idxIcd10.getResults(1, chap.getContent());
			// We add these entries inside an index
			if (res.size() > 0) {
				chap.setIcd(res);
			}
			// We add the chapter to the index
			idxNLH.addDoc(chap);
		}
		NLHfsm.finish();

		String[] folders2 = {"documents/NLH/L/"};
		NLHfsm = new NLHWebsiteCrawlerFSM(folders2, factory);
		NLHfsm.initialize();
		while (NLHfsm.hasNext()) {
			NLHChapter chap = (NLHChapter) NLHfsm.next();
			// We look for entries in icd10 that match the chapter
			Collection<ScoredDocument> res = idxAtc.getResults(1, chap.getContent());
			// We add these entries inside an index
			if (res.size() > 0) {
				chap.setAtc(res);
			}
			// We add the chapter to the index
			idxNLH.addDoc(chap);
		}
		NLHfsm.finish();

		String[] folders3 = {"documents/NLH/G/"};
		NLHfsm = new NLHWebsiteCrawlerFSM(folders3, factory);
		addAll("NLH", NLHfsm);
		this.close();
	}
	
	@Override
	public void clean() throws IOException {
		deleteDirectory(INDEXNLH);
		deleteDirectory(INDEXICD10);
	}

}
