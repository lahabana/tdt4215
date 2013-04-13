package com.ntnu.tdt4215.document;

import java.util.Vector;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;

/**
 * A document that holds the ICD10 entries that are the most relevant to the chapter
 */
public class NLHIcd10s implements IndexableDocument {
	protected Document document = new Document();
	String title;
	String content;
	
	public NLHIcd10s(String title, Vector<Document> content) {
		document = new Document();
		setTitle(title);
		setContent(content);
	}
	
	protected void setTitle(String title) {
		this.title = title;
		FieldType ft = new FieldType();
		ft.setStored(true);
		ft.setTokenized(false);
		ft.setIndexed(false);
		document.add(new Field("title", title, ft));
	}
	
	protected void setContent(Vector<Document> content) {
		String res = "";
		for (Document d: content) {
			res += d.get("id") + " "; 
		}
		res = res.substring(0, res.length() - 1);
		this.content = res;
		FieldType ft = new FieldType();
		ft.setStored(true);
		ft.setTokenized(true);
		ft.setIndexed(true);
		document.add(new Field("content", this.content, ft));
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
