package com.vsantoja.app.evernote.bean;

import java.io.Serializable;

/**
 * Created by vsantoja on 29/02/16.
 */
public class NoteEvernote implements Serializable
{
	private String guid;
	private String guidNoteBook;
	private String title;
	private String content;

	public NoteEvernote() {
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getGuidNoteBook() {
		return guidNoteBook;
	}

	public void setGuidNoteBook(String guidNoteBook) {
		this.guidNoteBook = guidNoteBook;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}