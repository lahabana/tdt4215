package com.ntnu.tdt4215.parser;


import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ntnu.tdt4215.document.NLHChapter;
import com.ntnu.tdt4215.document.factory.NLHFactory;

public class NLHWebsiteCrawlerFSM implements IndexingFSM {

	protected ArrayList<File> files;
	protected Iterator<File> fileIterator;
	protected Document currentDocument;
	protected Iterator<String> insideChapterIterator;
	private ArrayList<String> chaptersSelectorsInDocument;
	protected static FileFilter filter = new NLHFileFilter();
	protected NLHFactory factory;
	
	public NLHWebsiteCrawlerFSM(String[] folders, NLHFactory factor) {
		files = new ArrayList<File>();
		for (int i = 0; i < folders.length; i++) {
			loadFileFolder(folders[i]);	
		}
		factory = factor;
	}

	public boolean hasNext() {
		return fileIterator.hasNext() || insideChapterIterator.hasNext();
	}

	public NLHChapter next() {
		// We have finished the current file
		if (!insideChapterIterator.hasNext()) {
			// we go to the next file
			File f = fileIterator.next();
			try {
				// Parse the document and get an iterator on each chapter
				currentDocument = Jsoup.parse(f, null);
				currentDocument.outputSettings().charset("UTF-8");
				insideChapterIterator = ChaptersInDocument(currentDocument);
			} catch (IOException e) {
				System.err.println("Couldn't parse the document:" + f.getPath());
				throw new NoSuchElementException();
			}
		}
		String selector = insideChapterIterator.next();
		return createDocument(selector);
		 
	}

	public void initialize() {
		fileIterator = files.iterator();
		chaptersSelectorsInDocument = new ArrayList<String>();
		insideChapterIterator = chaptersSelectorsInDocument.iterator();
	}

	public void finish() {
		fileIterator = null;
		insideChapterIterator = null;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	protected void loadFileFolder(String folder) {
		File dir = new File(folder);
		if (!dir.canRead() || !dir.isDirectory()) {
			System.err.println("Can't read directory:" + dir + " it is not a directory");
		} else {
			for (File f: dir.listFiles(filter)) {
				files.add(f);
			}
		}
	}
	
	/** 
	 * Returns all the chapters in the document passed
	 * @param currentFile2
	 * @return
	 */
	private Iterator<String> ChaptersInDocument(Document doc) {
		chaptersSelectorsInDocument = new ArrayList<String>();
		
		boolean isRootChapter = currentDocument.select("#page").size() == 1;
		// If there is no elements in the menu we are in a main chapter 
		// so there is only one chapter in the page
		if (isRootChapter) {
			chaptersSelectorsInDocument.add(currentDocument.select("#page").first().attr("id"));
		} else {
			// We get the main chapter
			chaptersSelectorsInDocument.add(currentDocument.select(".seksjon2").first().attr("id"));
			// We look at all the sub chapters in the page
			Elements elts = currentDocument.select("#menu li a");
			for (Element elt: elts) {
				// We add it to our list of selectors only if it's a chapter
				if (elt.text().matches("[A-Z]([0-9]\\.?)+.*")) {
					String[] matches = elt.attr("href").split("#");
					chaptersSelectorsInDocument.add(matches[matches.length - 1]);
				}
			}
		}
		return chaptersSelectorsInDocument.iterator();
	}
	
	/**
	 * Create an NHLChapter from the data that is linked to selector
	 * @param selector
	 * @return
	 */
	protected NLHChapter createDocument(String selector) {
		String title;
		String content;
		Element elt = currentDocument.getElementById(selector);
		// Get the title
		title = elt.select("h1,h2,h3,h4,h5,h6").first().text();
		// Get all the text
		content = elt.text();
		return factory.create(title, content);
	}
}

class NLHFileFilter implements FileFilter {

	public boolean accept(File file) {
		if (!file.canRead() || !file.isFile() || file.isHidden()) {
			return false;
		}
		if (!file.getName().matches("[A-Z]([0-9]\\.?)+\\.htm")) {
			return false;
		}
		return true;
	}
}
