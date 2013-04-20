package com.ntnu.tdt4215.document;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;

import com.hp.hpl.jena.ontology.OntClass;

/**
 * An interface for owl classes to be indexed
 * @author charlymolter
 *
 */
public abstract class OwlClass implements IndexableDocument {
	String id = "";
	Document document = new Document();
	static FieldType ftId = new FieldType();
	static {
		ftId.setStored(true);
		ftId.setTokenized(false);
		ftId.setIndexed(false);
	}
	
	public OwlClass(OntClass ontClass) {
	}
	
	/**
	 * Extracts the info from the ontology and set the attributes that correspond
	 * @param ontClass
	 */
	abstract public void extractInfo(OntClass ontClass);
	
	public Document getDocument() {
		return document;
	}

	protected void setId() {
		document.add(new Field("id", id, ftId));
	}
}
