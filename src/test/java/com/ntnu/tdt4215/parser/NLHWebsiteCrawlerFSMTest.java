package com.ntnu.tdt4215.parser;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;

import com.ntnu.tdt4215.document.NLHChapter;
import com.ntnu.tdt4215.parser.NLHWebsiteCrawlerFSM;


public class NLHWebsiteCrawlerFSMTest {

	//@Test
	@Ignore
	public void ListTest() {
		String[] folders = {"Download/G/", "Download/L/", "Download/T/"};
		NLHWebsiteCrawlerFSM fsm = new NLHWebsiteCrawlerFSM(folders);
		for (File f: fsm.files) {
			System.out.println(f.getPath());
		}
		assertTrue(true);
	}
	
	//@Test
	@Ignore
	public void ExtractChaptersSelectors() {
		String[] folders = {"TestChapters/"};
		NLHWebsiteCrawlerFSM fsm = new NLHWebsiteCrawlerFSM(folders);
		fsm.initialize();
		while (fsm.hasNext()) {
			NLHChapter ch = fsm.next();
			System.out.println(ch.getTitle());
		}
		fsm.finish();
		assertTrue(true);
	}
	
	
}
