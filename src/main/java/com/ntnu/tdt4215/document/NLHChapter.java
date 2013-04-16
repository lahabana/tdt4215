package com.ntnu.tdt4215.document;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;

public class NLHChapter implements IndexableDocument {
	// The Lucene document that is created.
	protected Document document;
	String title;
	String content;
	static FieldType ftContent = new FieldType();
	static FieldType ftTitle = new FieldType();

	static {
		ftTitle.setStored(true);
		ftTitle.setTokenized(true);
		ftTitle.setIndexed(true);
		ftContent.setStored(false);
		ftContent.setTokenized(true);
		ftContent.setIndexed(true);
	}
	
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
		document.add(new Field("title", title, ftTitle));
	}
	
	protected void setContent(String content) {
		this.content = content;
		document.add(new Field("content", content, ftContent));
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getContent() {
		return content;
	}
	
	public Document getDocument() {
		return document;
	}
}
