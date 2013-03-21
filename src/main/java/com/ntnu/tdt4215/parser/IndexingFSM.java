package com.ntnu.tdt4215.parser;

import org.apache.lucene.document.Document;

/**
 * A finite state machine (iterator) to retrieve documents
 * Used by DirectoryManager.addAll()
 * You can easily implement it to parse and add documents one by one
 * when you have a collection
 */
public interface IndexingFSM {

	/**
	 * Whatever you want to do to start the machine
	 */
	public void initialize();
	
	/**
	 * Checks if all documents in the collection has been treated
	 * @return
	 */
	public boolean isFinished();
	
	/**
	 * Returns the document we point to 
	 * !!!! it doesn't move the point forward
	 */
	public Document getCurrent();
	
	/**
	 * Move the pointer to the next element in the collection
	 */
	public void next();
	
	/**
	 * Whatever you need to do to finish the machine
	 * (close files...)
	 */
	public void finish();
}
