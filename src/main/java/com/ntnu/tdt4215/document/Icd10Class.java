package com.ntnu.tdt4215.document;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;

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
	
	public Icd10Class(Statement stmt) {
	    Resource  subject   = stmt.getSubject();// get the subject
	    Statement labelStmt = subject.getProperty(RDFS.label);
	    if (labelStmt != null) {
	    	content = labelStmt.getString();
	    }
	    Statement codeStmt = subject.getProperty(code_compacted);
	    if (codeStmt != null) {
	    	id = codeStmt.getString();
	    }
	    setContent();
	    setId();
	} 
		
	public Document getDocument() {
		return document;
	}
	
	protected void setContent() {
		FieldType ft = new FieldType();
		ft.setStored(false);
		ft.setTokenized(true);
		ft.setIndexed(true);
		document.add(new Field("content", content, ft));
	}
	
	protected void setId() {
		FieldType ft = new FieldType();
		ft.setStored(true);
		ft.setTokenized(false);
		ft.setIndexed(false);
		document.add(new Field("id", id, ft));
	}

	public String toString() {
		return "{id:\"" + id + "\", content:\"" + content + "\"}";
		
	}
}
