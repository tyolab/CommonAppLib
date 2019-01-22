package au.com.tyo.app.model;

import android.graphics.drawable.Drawable;

public interface Searchable {

	boolean requiresFurtherProcess();

	String getTitle();

	String getSnippet();

	String getShort();

	long getPosition(); // the position in the return result list
	
	void setPosition(long position);

	Drawable getDrawable();

	long getId();
	
}
