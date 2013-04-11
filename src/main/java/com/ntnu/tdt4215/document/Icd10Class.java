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

	String label = "";
	String code = "";
	Document document = new Document();
	static String ns = "http://research.idi.ntnu.no/hilab/ehr/ontologies/icd10no.owl#";
	static Property code_compacted = ResourceFactory.createProperty(ns + "code_compacted");
	
	public Icd10Class(Statement stmt) {
	    Resource  subject   = stmt.getSubject();// get the subject
	    Statement labelStmt = subject.getProperty(RDFS.label);
	    if (labelStmt != null) {
	    	label = labelStmt.getString();
	    }
	    Statement codeStmt = subject.getProperty(code_compacted);
	    if (codeStmt != null) {
	    	code = codeStmt.getString();
	    }
	    setLabel();
	    setCode();
	} 
		
	public Document getDocument() {
		return document;
	}
	
	protected void setLabel() {
		FieldType ft = new FieldType();
		ft.setStored(false);
		ft.setTokenized(true);
		ft.setIndexed(true);
		document.add(new Field("label", label, ft));
	}
	
	protected void setCode() {
		FieldType ft = new FieldType();
		ft.setStored(true);
		ft.setTokenized(false);
		ft.setIndexed(true);
		document.add(new Field("code", code, ft));
	}

	public String toString() {
		return "{code:\"" + code + "\", label:\"" + label + "\"}";
		
	}
}
