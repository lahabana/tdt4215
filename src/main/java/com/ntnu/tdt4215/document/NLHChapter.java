package com.ntnu.tdt4215.document;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;

public class NLHChapter implements IndexableDocument {
	// The Lucene document that is created.
	protected Document document;
	String title;
	String content;
	
	public NLHChapter() {
		document = new Document();
	}
	
	public NLHChapter(String title, String content) {
		document = new Document();
		setTitle(title);
		setContent(content);
	}
	
	protected void setTitle(String title) {
		this.title = title;
		FieldType ft = new FieldType();
		ft.setStored(true);
		ft.setTokenized(true);
		ft.setIndexed(true);
		document.add(new Field("title", title, ft));
	}
	
	protected void setContent(String content) {
		this.content = content;
		FieldType ft = new FieldType();
		ft.setStored(false);
		ft.setTokenized(true);
		ft.setIndexed(true);
		document.add(new Field("content", content, ft));
	}
	
	public String getTitle() {
		return title;
	}
	
	public Document getDocument() {
		return document;
	}
}
