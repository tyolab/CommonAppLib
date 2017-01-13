/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD.
 *
 */

package au.com.tyo.app;

import java.util.List;

import android.content.res.Configuration;
import android.os.Bundle;
import au.com.tyo.android.CommonController;
import au.com.tyo.android.services.ImageDownloader;
import au.com.tyo.app.data.DisplayItem;
import au.com.tyo.app.data.ImagedSearchableItem;
import au.com.tyo.app.data.Searchable;
import au.com.tyo.app.data.SearchableItem;
import au.com.tyo.app.ui.UI;

/**
 * @author Eric Tang <eric.tang@tyo.com.au>
 */


public interface Controller extends CommonController {

	UI getUi();

	void createUi();

	CommonAppSettings getSettings();

	void onPostCreate(Bundle savedInstanceState);

	void onConfigurationChanged(Configuration newConfig);
	
	void onUiReady();

	InputManager getInputManager();

	ImageDownloader getImageDownloader();

	NetworkMonitor getNetworkMonitor();
	
	void setUi(UI ui);

	void onAppStart();

	String getTextForSearchResultItem(ImagedSearchableItem ws, String query);

	DisplayItem getItemText(Searchable item);

	List<?> getSuggesions(String query, String extra, boolean hasToBeBestMatch);
	
	List<?> getSuggesions(String query, boolean hasToBeBestMatch);

	void loadHistory();
	
	void displayHistory();

	String getTextForSearchResultItem(String text);

	void onHistoryItemClick(ImagedSearchableItem page, int fromHistory,
			boolean b);
	
	void onSearchInputFocusEscaped();

	void search(String query);

	void onSearchInputFocused();
	
	void open(Searchable item);
	
	void search(Searchable page, int fromHistory, boolean b);

	List<String> getQueryList();
	
}
