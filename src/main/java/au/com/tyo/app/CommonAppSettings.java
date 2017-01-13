/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD.
 *
 */

package au.com.tyo.app;

import android.content.Context;
import au.com.tyo.android.AndroidSettings;

/**
 * @author Eric Tang <eric.tang@tyo.com.au>
 */

public class CommonAppSettings extends AndroidSettings {
	
	public static final String PREF_SHOW_SEARCH_BAR = "pref_show_search_bar";
	
	protected boolean alwaysShowSearchBar;

	public CommonAppSettings(Context context) {
		super(context);
		
		alwaysShowSearchBar = true;
	}
	
	/**
	 * leave show search bar by default to the specific App
	 * 
	 * @param b
	 */
	protected void loadShowSearchBarPreference(boolean b) {
		alwaysShowSearchBar = prefs.getBoolean(PREF_SHOW_SEARCH_BAR, b);
	}
	
	public void setShowSearchBar(boolean b) {
		updatePreference(PREF_SHOW_SEARCH_BAR, b);
		
		alwaysShowSearchBar = b;
	}
	

	public boolean toShowSearchBar() {
		return alwaysShowSearchBar;
	}
}
