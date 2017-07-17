/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD.
 *
 */

package au.com.tyo.app.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * @author Eric Tang <eric.tang@tyo.com.au>
 */

public interface UI {

	boolean recreationRequried();

	void initialiseComponents();

	void assignMainUiContainer(FrameLayout frameLayout);

	void initializeUi(Context context);

	SearchInputView getSearchInputView();

	void setSuggestionViewVisibility(boolean b);

	SuggestionView getSuggestionView();

	void onSearchInputFocusStatus(boolean focused);

	void onConfigurationChanged(Configuration newConfig);

	void hideAd();

	void showAd();

	View getMainView();

	void onNetworkDisonnected();

	void onNetworkConnected();

	void initializeUi(View v);

	void onAdLoaded();

	Object setupActionBar(Object bar);

	void hideProgressBar();

	void hideSuggestionView();

	public void setUiRecreationRequierd(boolean value);

	void onPause(Context context);

	void onResume(Context context);

	void setSplashScreenOverlayView(View viewOverlay);

	void setupStartupAdView(View viewOverlay, Activity splashScreen);

    void onWidowReady();

    void setMainView(View mainView);

	void setupComponents();

    boolean onBackPressed();

    void showProgressBar();

    void onStop(Context currentActivity);

	Toolbar getToolbar();

    void onAppStart();

    void setupTheme(Activity activity);

    ViewGroup getBodyView();

	ActionBarMenu getActionBarMenu();
}
