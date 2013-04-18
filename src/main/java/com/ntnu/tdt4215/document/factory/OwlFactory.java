package com.ntnu.tdt4215.document.factory;

import com.hp.hpl.jena.ontology.OntClass;
import com.ntnu.tdt4215.document.OwlClass;

public interface OwlFactory {

	OwlClass create(OntClass ontClass);

}
