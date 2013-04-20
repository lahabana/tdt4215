package com.ntnu.tdt4215.document.association;

import java.util.Collection;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;

import com.ntnu.tdt4215.document.IndexableDocument;
import com.ntnu.tdt4215.index.ScoredDocument;

public abstract class AbstractNLHIcd10 implements IndexableDocument {
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
	
	public AbstractNLHIcd10(String title, Collection<ScoredDocument> content) {
		document = new Document();
		setTitle(title);
		setContent(content);
	}

	abstract protected void setContent(Collection<ScoredDocument> content);

	protected void setTitle(String title) {
		this.title = title;
		document.add(new Field("title", title, ftTitle));
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
