package com.ntnu.tdt4215.parser;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
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
public class Icd10FSM extends OwlFSM {
	static String ns = "http://research.idi.ntnu.no/hilab/ehr/ontologies/icd10no.owl#";
	static Property code_compacted = ResourceFactory.createProperty(ns + "code_compacted");
	
	/**
	 * Our selector to select only the classes
	 */
	protected static Selector stSelector = new SimpleSelector(null, null, (RDFNode) null) {
        public boolean selects(Statement s) {
        	if (s.getObject().equals(OWL.Class)) {
        		Resource  subject   = s.getSubject();
        		if (subject.hasProperty(code_compacted)) {
        		    Statement codeStmt = subject.getProperty(code_compacted);
        		    if (codeStmt.getString().length() != 0 &&
        		    	!codeStmt.getString().contains("-")) {
        		    	return true;
        		    }
        		}
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
