package com.ntnu.tdt4215.document;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;

import com.hp.hpl.jena.ontology.OntClass;

public class AtcClass extends OwlClass {

	String content = "";
	static FieldType ftContent = new FieldType();
	static {
		ftContent.setStored(true);
		ftContent.setTokenized(true);
		ftContent.setIndexed(true);
	}

	public AtcClass(OntClass ontClass) {
		super(ontClass);
		extractInfo(ontClass);
		setId();
		document.add(new Field("content", content, ftContent));
		System.out.println(this);
	}

	@Override
	public void extractInfo(OntClass ontClass) {
		id = ontClass.getLocalName();
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

	@Override
	public String toString() {
		return "{id:\"" + id + "\", content:\"" + content + "\"}";	
	}

}
