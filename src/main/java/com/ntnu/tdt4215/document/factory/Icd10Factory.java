package com.ntnu.tdt4215.document.factory;

import com.hp.hpl.jena.ontology.OntClass;
import com.ntnu.tdt4215.document.Icd10Class;
import com.ntnu.tdt4215.document.OwlClass;

public class Icd10Factory implements OwlFactory {

	public OwlClass create(OntClass ontClass) {
		return new Icd10Class(ontClass);
	}

}
