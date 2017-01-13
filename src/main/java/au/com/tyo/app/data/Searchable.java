package au.com.tyo.app.data;

public interface Searchable {

	boolean requiresFurtherProcess();

	String getTitle();

	String getSnippet();

	String getShort();
	
	long getPosition(); // the position in the return result list
	
	void setPosition(long position);
	
}
