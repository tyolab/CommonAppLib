/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD.
 *
 */

package au.com.tyo.app.data;

public class ImagedSearchableItem extends SearchableItem {
	
	private byte[] imageBytes;
	
	private String thumnailLink;
	
	private String url;
	
	public ImagedSearchableItem() {
		imageBytes = null;
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

}
