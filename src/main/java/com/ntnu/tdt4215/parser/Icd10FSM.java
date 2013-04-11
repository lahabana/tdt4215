package com.ntnu.tdt4215.parser;

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.OWL;
import com.ntnu.tdt4215.document.Icd10Class;

/**
 * A concrete FSM to parse ontologies of Icd10 type
 * @author charlymolter
 *
 */
public class Icd10FSM extends OwlFSM<Icd10Class>{
	/**
	 * Our selector to select only the classes
	 */
	protected static Selector stSelector = new SimpleSelector(null, null, (RDFNode) null) {
        public boolean selects(Statement s) {
        	if (s.getObject().equals(OWL.Class)) {
    			return true;
    		}
        	return false;
        }
    };
	
    /**
     * @param file the filename of the ontology
     */
	public Icd10FSM(String file) {
		super(file, stSelector);
	}
	
	public Icd10Class next() {
		return new Icd10Class(iter.nextStatement());
	}

}
