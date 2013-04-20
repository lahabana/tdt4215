package com.ntnu.tdt4215.document.owl;

import com.hp.hpl.jena.ontology.OntClass;

public interface OwlFactory {

	AbstractOwlClass create(OntClass ontClass);

}
