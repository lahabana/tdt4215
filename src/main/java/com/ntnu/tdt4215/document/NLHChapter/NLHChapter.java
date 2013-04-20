package com.ntnu.tdt4215.document.NLHChapter;

import java.util.Collection;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;

import com.ntnu.tdt4215.document.IndexableDocument;
import com.ntnu.tdt4215.document.ScoredDocument;

public class NLHChapter implements IndexableDocument {
	// The Lucene document that is created.
	protected Document document;
	String title;
	String content;
	String icd;
	private String atc;
	static FieldType ftContent = new FieldType();
	static FieldType ftTitle = new FieldType();
	static FieldType ftOwl = new FieldType();

	static {
		ftTitle.setStored(true);
		ftTitle.setTokenized(true);
		ftTitle.setIndexed(true);
		ftContent.setStored(false);
		ftContent.setTokenized(true);
		ftContent.setIndexed(true);
		ftOwl.setStored(true);
		ftOwl.setIndexed(true);
		ftOwl.setTokenized(true);
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

	public void setIcd(Collection<ScoredDocument> icdEntries) {
		String res = getStorableString(icdEntries);
		Field f = new Field("icd", res, ftOwl);
		document.add(f);
		this.icd = res;
	}

	public void setAtc(Collection<ScoredDocument> atcEntries) {
		String res = getStorableString(atcEntries);
		Field f = new Field("atc", res, ftOwl);
		document.add(f);
		this.atc = res;
	}

	/**
	 * Create a String from the chapters retrieved that respect the format we want
	 * @param docs
	 * @return
	 */
	protected String getStorableString(Collection<ScoredDocument> docs) {
		String res = "";
		for (ScoredDocument d: docs) {
			float score = d.getScore();
			String id = d.getField("id");
			for (int i = 0; i < score; i++) {
				res += id + " ";
			}
		}
		return res;
	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}

	public String getIcd() {
		return icd;
	}

	public String getAtc() {
		return atc;
	}

	public Document getDocument() {
		return document;
	}
}
