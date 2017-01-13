/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD.
 *
 */

package au.com.tyo.app.data;

public class DisplayItem {
	
	private byte[] imageBytes;
	
	private String thumnailLink; // thumnail url
	
	private String url;  // image url
	
	private String name;
	
	private String text;
	
	public DisplayItem() {
		imageBytes = null;
	}
	
	public DisplayItem(String title, String text) {
		this();
		this.name = title;
		this.text = text;
	}

	public byte[] getImgBytes() {
		return imageBytes;
	}

	public String getThumbnailLink() {
		return thumnailLink;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String path) {
		url = path;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
