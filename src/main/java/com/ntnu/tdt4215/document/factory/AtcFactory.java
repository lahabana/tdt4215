package com.ntnu.tdt4215.document.factory;

import com.hp.hpl.jena.ontology.OntClass;
import com.ntnu.tdt4215.document.AtcClass;
import com.ntnu.tdt4215.document.OwlClass;

public class AtcFactory implements OwlFactory {

	public OwlClass create(OntClass ontClass) {
		return new AtcClass(ontClass);
	}

}
