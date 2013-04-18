package com.ntnu.tdt4215.document;


import org.apache.lucene.document.Document;
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
public class Icd10Class implements OwlClass {

	String content = "";
	String id = "";
	Document document = new Document();
	static String ns = "http://research.idi.ntnu.no/hilab/ehr/ontologies/icd10no.owl#";
	static Property code_compacted = ResourceFactory.createProperty(ns + "code_compacted");
	static Property synonym = ResourceFactory.createProperty(ns + "synonym");
	static FieldType ftId = new FieldType();
	static FieldType ftContent = new FieldType();
	static {
		ftId.setStored(true);
		ftId.setTokenized(false);
		ftId.setIndexed(false);
		ftContent.setStored(false);
		ftContent.setTokenized(true);
		ftContent.setIndexed(true);
	}
	
	public Icd10Class(OntClass ontClass) {
		extractInfo(ontClass);
	    setContent();
	    setId();
	}

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
		    if (parent.getSuperClass().equals(parent)) {
		    	break;
		    }
	    }
	}
		
	public Document getDocument() {
		return document;
	}
	
	protected void setContent() {
		document.add(new Field("content", content, ftContent));
	}
	
	protected void setId() {
		document.add(new Field("id", id, ftId));
	}

	public String toString() {
		return "{id:\"" + id + "\", content:\"" + content + "\"}";
		
	}
}
