/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD.
 *
 */

package au.com.tyo.app;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;

import java.util.List;

import au.com.tyo.android.CommonController;
import au.com.tyo.android.NetworkMonitor;
import au.com.tyo.android.services.ImageDownloader;
import au.com.tyo.app.model.DisplayItem;
import au.com.tyo.app.model.ImagedSearchableItem;
import au.com.tyo.app.model.Searchable;
import au.com.tyo.app.ui.UI;

/**
 * @author Eric Tang <eric.tang@tyo.com.au>
 */

/**
 *
 * @param <T> UI Class
 */
public interface Controller<T extends UI> extends CommonController {

	void setParcel(Object parcel);

	boolean isAppQuit();

    void onMessageBackgroundTaskStageTwo();

	void onMessageBackgroundTaskStageThree();

	void onMessageBackgroundTaskStageFour();

	void onMessageBackgroundTaskStageFive();

	void onMessageBackgroundTaskStageSix();

	void onMessageCustomTwo();

	void onMessageCustomOne();

	void onMessageCustomFour();

	void onMessageCustomFive();

	void onMessageCustomSix();

	void onMessageCustomSeven();

	void onMessageCustomEight();

	void onMessageCustomNine();

	void onMessageCustomTen();

	void onMessageBackgroundTaskStageOne();

	void onMessageBackgroundTaskEnded();

	void onMessageBackgroundTaskStarted();

	T getUi();

	void createUi();

	CommonAppSettings getSettings();

	void onPostCreate(Bundle savedInstanceState);

	void onConfigurationChanged(Configuration newConfig);
	
	void onUiReady();

	InputManager getInputManager();

	ImageDownloader getImageDownloader();

	NetworkMonitor getNetworkMonitor();
	
	void setUi(T ui);

	void onCurrentActivityStart();

	String getTextForSearchResultItem(ImagedSearchableItem ws, String query);

	DisplayItem getItemText(Searchable item);

	List<?> getSuggestions(String query, String extra, boolean hasToBeBestMatch);
	
	List<?> getSuggestions(String query, boolean hasToBeBestMatch);

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

	boolean onKeyLongPress(int keyCode, KeyEvent event);

	void onStop();

	boolean onBackgroundTaskEnded(Activity activity);

	void onBackgroundTaskStarted(Activity activity);

    void onWidowReady();

	boolean onSupportNavigateUp();

    Object getParcel();

    void initializeOnce();

    void openUrl(String url);

    void onUiCreated();

    void grantPermission(String permission);

    void setResult(Object result);

	Object getResult();
}
