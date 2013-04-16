package com.ntnu.tdt4215.document;

import java.util.Collection;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;

public class NLHIcd10Inline {
	protected Document document = new Document();
	String title;
	String content;
	static FieldType ftContent = new FieldType();
	static FieldType ftTitle = new FieldType();

	static {
		ftTitle.setStored(true);
		ftTitle.setTokenized(false);
		ftTitle.setIndexed(false);
		ftContent.setStored(true);
		ftContent.setTokenized(true);
		ftContent.setIndexed(true);
	}
	
	public NLHIcd10Inline(String title, Collection<ScoredDocument> content) {
		document = new Document();
		setTitle(title);
		setContent(content);
	}

	protected void setTitle(String title) {
		this.title = title;
		document.add(new Field("title", title, ftTitle));
	}
	
	protected void setContent(Collection<ScoredDocument> content) {
		String res = "";
		for (ScoredDocument d: content) {
			float score = d.getScore();
			String id = d.getField("id");
			for (int i = 0; i < score; i++) {
				res += id + " ";
			}
		}
		Field f = new Field("content", res, ftContent);
		document.add(f);
		this.content = res;
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
