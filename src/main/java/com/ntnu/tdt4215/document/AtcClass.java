package com.ntnu.tdt4215.document;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;

import com.hp.hpl.jena.ontology.OntClass;

public class AtcClass implements OwlClass {

	String content = "";
	String id = "";
	Document document = new Document();
	static FieldType ftId = new FieldType();
	static FieldType ftContent = new FieldType();
	static {
		ftId.setStored(true);
		ftId.setTokenized(false);
		ftId.setIndexed(false);
		ftContent.setStored(true);
		ftContent.setTokenized(true);
		ftContent.setIndexed(true);
	}

	public AtcClass(OntClass ontClass) {
		extractInfo(ontClass);
		id = ontClass.getLocalName();
		setId();
	    setContent();
	}

	public void extractInfo(OntClass ontClass) {
	    // get the title
		String label = ontClass.getLabel("no");
		content += " " + (label == null ? "" : label);
		// Extract the parents
	    OntClass parent = ontClass;
	    while (parent.hasSuperClass()) {
	    	parent = parent.getSuperClass();
	    	String superLabel = parent.getLabel("no");
		    if (superLabel!= null) {
		    	content += " " + superLabel;
		    }
		    if (parent.getSuperClass() == null || parent.getSuperClass().equals(parent)) {
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
