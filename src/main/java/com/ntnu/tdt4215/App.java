package com.ntnu.tdt4215;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

import com.ntnu.tdt4215.index.DirectoryManager;
import com.ntnu.tdt4215.query.QueryFactory;
import com.ntnu.tdt4215.query.SimpleQueryFactory;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

public class App {
	
	static Directory index;
	static StandardAnalyzer analyzer;
	static QueryFactory qpf;
	static DirectoryManager manager;
	final static File FILE = new File("bingou");
	
	public static void main(String[] args) throws IOException, ParseException {
		// We clean the folder containing the index
    	if (args.length == 1 && args[0].equals("--clean")) {
    		if (FILE.exists() && FILE.isDirectory() && FILE.canWrite()) {
    			FileUtils.deleteDirectory(FILE);
    		} else {
    			System.err.println("Can't delete the directory:" + FILE.getAbsolutePath());
    			System.exit(-1);
    		}
    		return;
    	}
    	// We measure the performances
    	if (args.length == 1 && args[0].equals("--measure")) {
    		initIndex();
    		return;
		}
    	// We want to index new documents
    	if (args.length == 1 && args[0].equals("--index")) {
    		initIndex();
    		// Create your fsm
    		//manager.addAll(fsm);
    		return;
    	}
    	// We want to search the index
    	if(args.length == 2 && args[0].equals("--search")) {
    		initIndex();
    		Vector<Document> docs = manager.getResults(3, args[1]);
    		for(Document d: docs) {
    			System.out.println("Match:" + d.get("title"));
    		}
    		return;
    	} 
    	showHelp();
    }
	
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
	    manager = new DirectoryManager(index, analyzer, qpf);
	}

	private static void showHelp() {
		System.out.println("-------TDT 4215 project app usage-----");
		System.out.println("By: Anne-Sophie Gourlay, David Katuščák and Charly Molter");
		System.out.println("\tOptions:");
		System.out.println("\t\t--index: index documents");
		System.out.println("\t\t--search <query>: search the index for a match query");
		System.out.println("\t\t--clean: empty the index");
		System.out.println("\t\t--measure: compare to the gold standard and output some stats");
	
	}
}
