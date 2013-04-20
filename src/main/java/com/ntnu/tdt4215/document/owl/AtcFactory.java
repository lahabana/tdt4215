package com.ntnu.tdt4215.document.owl;

import com.hp.hpl.jena.ontology.OntClass;

public class AtcFactory implements OwlFactory {

	public AbstractOwlClass create(OntClass ontClass) {
		return new AtcClass(ontClass);
	}

}
