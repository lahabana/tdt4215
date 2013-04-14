package com.ntnu.tdt4215.parser;

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.OWL;
import com.ntnu.tdt4215.document.AtcClass;

public class AtcFSM extends OwlFSM {
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
	public AtcFSM(String file) {
		super(file, stSelector);
	}
	
	public AtcClass next() {
		return new AtcClass(iter.nextStatement());
	}

}
