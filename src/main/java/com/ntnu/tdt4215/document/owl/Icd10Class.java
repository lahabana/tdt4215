package com.ntnu.tdt4215.document.owl;


import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * A class to create a document from the Icd10 ontology
 * @author charlymolter
 *
 */
public class Icd10Class extends AbstractOwlClass {

	String content = "";
	static String ns = "http://research.idi.ntnu.no/hilab/ehr/ontologies/icd10no.owl#";
	static Property code_compacted = ResourceFactory.createProperty(ns + "code_compacted");
	static Property synonym = ResourceFactory.createProperty(ns + "synonym");
	static FieldType ftContent = new FieldType();
	static {
		ftContent.setStored(true);
		ftContent.setTokenized(true);
		ftContent.setIndexed(true);
	}
	
	public Icd10Class(OntClass ontClass) {
		super(ontClass);
		extractInfo(ontClass);
		setId();
		document.add(new Field("content", content, ftContent));
	}

	@Override
	public void extractInfo(OntClass ontClass) {
	    Statement codeStmt = ontClass.getProperty(code_compacted);
	    if (codeStmt != null) {
	    	id = codeStmt.getString();
	    }
		Statement labelStmt = ontClass.getProperty(RDFS.label);
	    if (labelStmt != null) {
	    	content = labelStmt.getString();
	    }
	    // Get the synonyms
	    if (ontClass.hasProperty(synonym)) {
	    	Statement propertyStmt = ontClass.getProperty(synonym);
	    	content += " " + propertyStmt.getString();
	    }
	    // Extract the parents
	    OntClass parent = ontClass;
	    while (parent.hasSuperClass()) {
	    	parent = parent.getSuperClass();
	    	Statement superLabelStmt = parent.getProperty(RDFS.label);
		    if (superLabelStmt != null) {
		    	content += " " + superLabelStmt.getString();
		    }
		    if (parent.getSuperClass() == null || parent.getSuperClass().equals(parent)) {
		    	break;
		    }
	    }
	}

	@Override
	public String toString() {
		return "{id:\"" + id + "\", content:\"" + content + "\"}";
		
	}
}
