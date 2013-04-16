package com.ntnu.tdt4215.document;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
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
	public static OntModel model;
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
	
	public Icd10Class(Statement stmt) {
		// Get the code of the entry
		Resource  subject   = stmt.getSubject();
	    Statement codeStmt = subject.getProperty(code_compacted);
	    if (codeStmt != null) {
	    	id = codeStmt.getString();
	    }
	    extractInfo(stmt);
	    setContent();
	    setId();
	} 
	
	public void extractInfo(Statement stmt) {
		Resource  subject   = stmt.getSubject();// get the subject
	    // get the title
	    Statement labelStmt = subject.getProperty(RDFS.label);
	    if (labelStmt != null) {
	    	content = labelStmt.getString();
	    }
	    
	    // Get the synonyms
	    if (subject.hasProperty(synonym)) {
	    	Statement propertyStmt = subject.getProperty(synonym);
	    	content += " " + propertyStmt.getString();
	    }
	    
	    if (subject.hasProperty(RDFS.subClassOf)) {
	    	Statement subclass = subject.getProperty(RDFS.subClassOf);
	    	boolean stop = false;
	    	while(!stop) {
		    	Property parent = model.getProperty(subclass.getResource().getURI());
		    	content += " " + parent.getProperty(RDFS.label).getString();
		    	if (!parent.hasProperty(RDFS.subClassOf)) {
		    		stop = true;
		    	} else {
		    		if (parent.getProperty(RDFS.subClassOf).getResource().equals(subclass.getResource())) {
		    			stop = true;
		    		} else {
		    			subclass = parent.getProperty(RDFS.subClassOf);
		    		}
		    	}
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
