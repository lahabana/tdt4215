package com.ntnu.tdt4215.index;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.lucene.document.Document;

import com.ntnu.tdt4215.document.ScoredDocument;

/**
 * The goal of this interface is to deal with queries that should
 * run more than one query. It uses a principle close to map reduce
 * 1) callsplitQuery which creates from the original query each query
 * 2) for each result call map
 * 3) once all queries has been run call reduce that will return the actual result
 * @author charlymolter
 *
 */
public interface MultipleQueryPolicy {

	/**
	 * Takes the original query string and extracts each subquery
	 * @param query
	 * @return
	 */
	public ArrayList<String> splitQuery(String query);

	/**
	 * add the document as a match in the query
	 * @param doc
	 */
	public void map(Document doc, float score);
	
	/**
	 * Rank/Cut to obtain the actual result to the main query
	 * @param nbHits
	 * @return
	 */
	public Collection<ScoredDocument> reduce(int nbHits);

}
