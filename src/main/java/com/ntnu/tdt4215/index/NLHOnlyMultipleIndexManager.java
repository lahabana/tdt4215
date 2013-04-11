package com.ntnu.tdt4215.index;

import java.io.IOException;
import java.util.Vector;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;

import com.ntnu.tdt4215.parser.IndexingFSM;
import com.ntnu.tdt4215.query.QueryFactory;

/**
 * A simple manager that just has a NLHChapter index
 * @author charlymolter
 *
 */
public class NLHOnlyMultipleIndexManager extends MultipleIndexManager {

	public NLHOnlyMultipleIndexManager(Directory index2, Analyzer analyzer2, QueryFactory qpf) {
		super(index2, analyzer2, qpf);
		SimpleManager idx = new SimpleManager(index, analyzer, queryFactory);
		addIndex("NLHIndex", idx);
	}
	
	public Vector<Document> getResults(int maxElt, String queryStr) throws IOException, ParseException {
		return getIndex("NLHIndex").getResults(maxElt, queryStr);
	}

	public void addAll(IndexingFSM fsm) throws IOException {
		getIndex("NLHIndex").addAll(fsm);
	}
}
