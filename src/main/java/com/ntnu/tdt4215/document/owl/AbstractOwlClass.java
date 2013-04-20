package com.ntnu.tdt4215.document.owl;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;

import com.hp.hpl.jena.ontology.OntClass;
import com.ntnu.tdt4215.document.IndexableDocument;

/**
 * An interface for owl classes to be indexed
 * @author charlymolter
 *
 */
public abstract class AbstractOwlClass implements IndexableDocument {
	String id = "";
	Document document = new Document();
	static FieldType ftId = new FieldType();
	static {
		ftId.setStored(true);
		ftId.setTokenized(false);
		ftId.setIndexed(false);
	}
	
	public AbstractOwlClass(OntClass ontClass) {
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
