package com.ntnu.tdt4215.parser;

import java.io.IOException;
import java.io.InputStream;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;
import com.ntnu.tdt4215.document.Icd10Class;

/**
 * An abstract class to make an FSM on a owl file
 * The file will be read and parsed on each call to initialize()
 * To make a concrete version of this class you need to implement next()
 * @author charlymolter
 *
 * @param <T>
 */
public abstract class OwlFSM implements IndexingFSM {
	private String filename;
	protected StmtIterator iter;
	private Selector selector;
	
	public OwlFSM(String file, Selector sel) {
		filename = file;
		selector = sel;
	}
	
	public boolean hasNext() {
		return iter.hasNext();
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
		Icd10Class.model = ModelFactory.createOntologyModel();
		Icd10Class.model.read(in, null);
		try {
			in.close();
		} catch (IOException e) {
			System.err.println("Couldn't close the inputStream");
		}
		iter = Icd10Class.model.listStatements(selector);
	}

	public void finish() {
		iter = null;
		Icd10Class.model = null;
	}

}
