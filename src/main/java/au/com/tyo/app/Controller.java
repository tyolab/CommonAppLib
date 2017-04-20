/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD.
 *
 */

package au.com.tyo.app;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;

import java.util.List;

import au.com.tyo.android.CommonController;
import au.com.tyo.android.services.ImageDownloader;
import au.com.tyo.app.data.DisplayItem;
import au.com.tyo.app.data.ImagedSearchableItem;
import au.com.tyo.app.data.Searchable;
import au.com.tyo.app.ui.UI;

/**
 * @author Eric Tang <eric.tang@tyo.com.au>
 */


public interface Controller extends CommonController {

	void onMessageBackgroundTaskStageTwo();

	void onMessageBackgroundTaskStageThree();

	void onMessageBackgroundTaskStageFour();

	void onMessageBackgroundTaskStageFive();

	void onMessageBackgroundTaskStageSix();

	void onMessageCustomeTwo();

	void onMessageCustomeOne();

	void onMessageCustomeFour();

	void onMessageCustomeFive();

	void onMessageCustomeSix();

	void onMessageCustomeSeven();

	void onMessageCustomeEight();

	void onMessageCustomeNine();

	void onMessageCustomeTen();

	void onMessageBackgroundTaskStageOne();

	void onMessageBackgroundTaskEndded();

	void onMessageBackgroundTaskStarted();

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
	
	void onOpenSearchItemClicked(Searchable item);
	
	void search(Searchable page, int fromHistory, boolean b);

	List<String> getQueryList();

	void onResume();

	void onDestroy();

	void onPause();

	void onPrepareOptionsMenu(Menu menu);

	boolean onKeyLongPress(int keyCode, KeyEvent event);

	void onStop();

	boolean onBackgroundTaskEndded(Activity activity);

	void onBackgroundTaskStarted(Activity activity);

    void onWidowReady();
}
