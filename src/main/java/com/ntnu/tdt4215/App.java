package com.ntnu.tdt4215;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.queryparser.classic.ParseException;

import com.ntnu.tdt4215.document.ScoredDocument;
import com.ntnu.tdt4215.gui.SearchWindow;
import com.ntnu.tdt4215.searchEngine.SearchEngine;
import com.ntnu.tdt4215.searchEngine.SeparateIndexSE;
import com.ntnu.tdt4215.searchEngine.SingleIndexSE;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JFrame;

public class App {
	
	private static int nbHits = 5;
	static SearchEngine manager;
	static PrintStream stdout;
	private static SearchWindow gui;
	
	public static void main(String[] args) throws ParseException, IOException {
		stdout = new PrintStream(System.out, true, "UTF-8");
		System.setOut(stdout);
		if (args.length >= 2 && args[0].equals("--search-engine")) {
			if (args[1].equals("separate")) {
				manager = new SeparateIndexSE();
			} else if (args[1].equals("single")) {
				manager = new SingleIndexSE();
			} else {
				showHelp();
				System.exit(1);
			}
			String[] argsTmp = new String[args.length - 2];
			for (int i = 0; i < argsTmp.length; i++) {
				argsTmp[i] = args[i + 2];
			}
			args = argsTmp;
		} else {
			manager = new SeparateIndexSE();
		}

		//nothing is specified we just launch the GUI
		if (args.length == 0) {
			launchGui();
			return;
		}
		if (args[0].equals("--clean")) {
    		try {
				manager.clean();
				return;
			} catch (IOException e) {
				System.err.println(e.getMessage());
				System.exit(2);
			}
		}
    	if (args[0].equals("--index")) {
    		// We want to index all accessible documents
    		try {
				manager.indexAll();
				return;
			} catch (IOException e) {
				System.err.println("An error occured while indexing");
				System.exit(2);
			}
    	}
	    
    	// We are going to do a search
    	if (args.length > 1 && !extractOptions(args)) {
	    	showHelp();
	    	System.exit(1);
	    }
		if (args[0].equals("--search")) {
			searchLoop();
			return;
		}
		if (args[0].equals("--gui")) {
			launchGui();
			return;
		}
		showHelp();
		System.exit(1);
    }
	
	/**
	 * Launch the Graphic user interface
	 */
	private static void launchGui() {
		gui = new SearchWindow(manager);
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private static boolean extractOptions(String[] args) {
		nbHits = Integer.parseInt(args[1]);
		if (manager instanceof SeparateIndexSE) {
			if (args.length != 6) {
				return false;
			}
			((SeparateIndexSE)manager).factor_hits_icd = Integer.parseInt(args[2]);
			((SeparateIndexSE)manager).factor_hits_ft = Integer.parseInt(args[3]);
			((SeparateIndexSE)manager).boost_icd = Float.parseFloat(args[4]);
			((SeparateIndexSE)manager).boost_atc = Float.parseFloat(args[5]);
			return true;
		}
		if (args.length != 2) {
			return false;
		}
		return true;

	}

	/**
	 * Creates a loop that will ask for the user to enter a filename to send 
	 * @throws IOException
	 * @throws ParseException
	 */
	private static void searchLoop() throws IOException, ParseException {
		boolean stop = false;
		java.io.BufferedReader stdin = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
		while (!stop) {
			System.out.println("Please insert the filename that contains "+ 
							   "the patient case to quit the program type twice return");
			String line = stdin.readLine();
			if (line == null || line.equals("")) {
				stop = true;
			} else {
				System.out.println("Searching the content of file:"+ line);
				searchFile(line);
			}
		}
		System.out.println("Quitting");	
	}

	/**
	 * Show the help message
	 */
	private static void showHelp() {
		System.out.println("-------TDT 4215 project app usage-----");
		System.out.println("By: Anne-Sophie Gourlay, David Katuscak and Charly Molter");
		System.out.println("\tOptions:");
		System.out.println("\t\t--search-engine separate|single: (optional) must be the first option" +
						   "select the type of search engine to use (default separate)");
		System.out.println("\t\t--index: index documents");
		System.out.println("\t\t--search start the program to search the index");
		System.out.println("\t\t--clean: empty the index");
		System.out.println("\t\t--gui: launch the gui to search (the documents must already have been indexed)");
		System.out.println("\twhen searching or using the GUI after the first option you can add options specific to the search engine");
	}
	
	/**
	 * Search the index with as a query the content of the file line
	 * @param line
	 * @throws IOException
	 * @throws ParseException
	 */
	private static void searchFile(String line) throws IOException, ParseException {
		File f = new File(line);
		if (!f.exists() || !f.canRead()) {
			System.err.println("Can't read the file: " + line);
		} else {
    		String contents = FileUtils.readFileToString(f, Charset.forName("UTF-8"));
    		Collection<ScoredDocument> docs = manager.getResults(nbHits, contents);
    		System.out.println("Matches:");
    		Iterator<ScoredDocument> it = docs.iterator();
    		int i = 0;
    		while (it.hasNext() && i < nbHits) {
    			ScoredDocument doc = it.next();
    			System.out.println(/*doc.getScore() + */doc.getField("title"));
    			i++;
    		}
		}
	}


}
