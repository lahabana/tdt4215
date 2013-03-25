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
    		return;
    	}
    	// We want to index new documents
    	if (args.length == 1 && args[0].equals("--index")) {
    		initIndex();
    		// Create your fsm
    		BasicFSM fsm = new BasicFSM();
    		manager.addAll(fsm);
    		return;
    	}
    	// We want to search the index
    	if(args.length == 2 && args[0].equals("--search")) {
    		initIndex();
    		File f = new File(args[1]);
    		if (!f.exists() || !f.canRead()) {
    			System.err.println("Can't read the file: " + args[1]);
    			System.exit(1);
    		}
    		String contents = FileUtils.readFileToString(f, Charset.forName("UTF-8"));

    		Vector<Document> docs = manager.getResults(3, contents);
    		System.out.println("Matches:");
    		for (Document d: docs) {
    			System.out.println(d.get("title"));
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
	    manager = new DirectoryManager<NLHChapter>(index, analyzer, qpf);
	}

	private static void showHelp() {
		System.out.println("-------TDT 4215 project app usage-----");
		System.out.println("By: Anne-Sophie Gourlay, David Katuscak and Charly Molter");
		System.out.println("\tOptions:");
		System.out.println("\t\t--index: index documents");
		System.out.println("\t\t--search <file>: search the index for a match to the document passed");
		System.out.println("\t\t--clean: empty the index");
		System.out.println("\t\t--measure: compare to the gold standard and output some stats");
	
	}
}
