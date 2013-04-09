package com.ntnu.tdt4215;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

import com.ntnu.tdt4215.document.NLHChapter;
import com.ntnu.tdt4215.index.DirectoryManager;
import com.ntnu.tdt4215.parser.BasicFSM;
import com.ntnu.tdt4215.parser.NLHWebsiteCrawlerFSM;
import com.ntnu.tdt4215.query.QueryFactory;
import com.ntnu.tdt4215.query.SimpleQueryFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Vector;

public class App {
	
	static Directory index;
	static StandardAnalyzer analyzer;
	static QueryFactory qpf;
	static DirectoryManager<NLHChapter> manager;
	final static File FILE = new File("index");
	
	public static void main(String[] args) throws IOException, ParseException {
		// We clean the folder containing the index
    	if (args.length == 1 && args[0].equals("--clean")) {
    		if (FILE.exists() && FILE.isDirectory() && FILE.canWrite()) {
    			FileUtils.deleteDirectory(FILE);
    		} else {
    			System.err.println("Can't delete the directory:" + FILE.getAbsolutePath());
    			System.exit(-1);
    		}
    	} else if (args.length == 1 && args[0].equals("--index")) {
    		// We want to index new documents
    		initIndex();
    		indexAll();
    	} else if(args.length == 1 && args[0].equals("--search")) {
    		// We want to search the index
    		initIndex();
    		searchLoop();
    	} else {
    		// Invalid entry we show the help
    		showHelp();
    	}
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
	 * Index all the documents that will be used for search
	 * @throws IOException
	 */
	private static void indexAll() throws IOException {
		// Create your fsm
		//BasicFSM fsm = new BasicFSM();
		String[] folders = {"Download/G/", "Download/L/", "Download/T/"};
		NLHWebsiteCrawlerFSM fsm = new NLHWebsiteCrawlerFSM(folders);
		manager.addAll(fsm);
	}

	/**
	 * Show the help message
	 */
	private static void showHelp() {
		System.out.println("-------TDT 4215 project app usage-----");
		System.out.println("By: Anne-Sophie Gourlay, David Katuscak and Charly Molter");
		System.out.println("\tOptions:");
		System.out.println("\t\t--index: index documents");
		System.out.println("\t\t--search start the program to search the index");
		System.out.println("\t\t--clean: empty the index");
		System.out.println("\t\t--measure: compare to the gold standard and output some stats");
	}
	
	/**
	 * Opens the index and prepare it to be either queried or to add documents to the index
	 * @throws IOException
	 */
	private static void initIndex() throws IOException {
	    // Create the folder that will hold the index on the FS
		if (!FILE.exists()) {
	    	if (!FILE.mkdir()) {
    			System.err.println("Can't create the directory:" + FILE.getAbsolutePath() + 
    								" to save the index");
    			System.exit(-1);	    		
	    	}
	    }
		index = new SimpleFSDirectory(FILE);
	
	    qpf = new SimpleQueryFactory();
	    analyzer = new StandardAnalyzer(Version.LUCENE_40);
	    manager = new DirectoryManager<NLHChapter>(index, analyzer, qpf);
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
    		Vector<Document> docs = manager.getResults(3, contents);
    		System.out.println("Matches:");
    		for (Document d: docs) {
    			System.out.println(d.get("title"));
    		}
		}
	}


}
