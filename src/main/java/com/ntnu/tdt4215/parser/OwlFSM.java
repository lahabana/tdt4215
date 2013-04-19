package com.ntnu.tdt4215.parser;

import java.io.IOException;
import java.io.InputStream;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.ntnu.tdt4215.document.OwlClass;
import com.ntnu.tdt4215.document.factory.OwlFactory;

/**
 * An abstract class to make an FSM on a owl file
 * The file will be read and parsed on each call to initialize()
 * To make a concrete version of this class you need to implement next()
 * @author charlymolter
 *
 * @param <T>
 */
public class OwlFSM implements IndexingFSM {
	private String filename;
	private OntModel model;
	private OwlFactory factory;
	private OntClass next = null;
	private ExtendedIterator<OntClass> classes;
	
	public OwlFSM(String file, OwlFactory fact) {
		filename = file;
		factory = fact;
	}
	
	public boolean hasNext() {
		return next != null;
	}
	
	public OwlClass next() {
		OwlClass res = factory.create(next);
		skip();
		return res;
	}

	/**
	 * Move the iterator to the next class without a children
	 */
	private void skip() {
		while (classes.hasNext()) {
            OntClass possible = classes.next();
            if (possible.getSubClass() == null || possible.getSubClass().equals(possible)) {
            	next = possible;
            	return;
            }
        }
		next = null;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

	public void initialize() {
		// use the FileManager to find the input file
		InputStream in = FileManager.get().open(filename);
		if (in == null) {
		    throw new IllegalArgumentException(
		                                 "File: " + filename + " not found");
		}
		// create an empty model
		model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		model.read(in, null);
		
		try {
			in.close();
		} catch (IOException e) {
			System.err.println("Couldn't close the inputStream");
		}
		classes = model.listClasses();
		skip();
	}

	public void finish() {
		model = null;
		classes = null;
	}

}
