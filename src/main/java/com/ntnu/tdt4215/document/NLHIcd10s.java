package com.ntnu.tdt4215.document;

import java.util.Collection;

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
	
	public NLHIcd10s(String title, Collection<ScoredDocument> content) {
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
		int i = 0;
		for (ScoredDocument d: content) {
			res += d.getField("id") + " "; 
			Field f = new Field("content" + i, d.getField("id"), ftContent);
			f.setBoost(d.getScore());
			document.add(f);
			i++;
		}
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
