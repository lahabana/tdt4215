package com.ntnu.tdt4215.document.factory;

import com.ntnu.tdt4215.document.NLHChapter;

public class NLHWebsiteFactory implements NLHFactory {

	public NLHChapter create(String title, String content) {
		return new NLHChapter(title, content);
	}

}
