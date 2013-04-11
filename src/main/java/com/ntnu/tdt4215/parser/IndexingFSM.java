package com.ntnu.tdt4215.parser;

import java.util.Iterator;

import com.ntnu.tdt4215.document.IndexableDocument;

/**
 * A finite state machine (iterator) to retrieve documents
 * Used by DirectoryManager.addAll()
 * You can easily implement it to parse and add documents one by one
 * when you have a collection
 */
public interface IndexingFSM extends Iterator<IndexableDocument> {

	/**
	 * Whatever you want to do to start the machine
	 */
	public void initialize();
	
	/**
	 * Whatever you need to do to finish the machine
	 * (close files...)
	 */
	public void finish();
}
