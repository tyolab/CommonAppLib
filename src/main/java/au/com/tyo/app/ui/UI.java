/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD.
 *
 */

package au.com.tyo.app.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import au.com.tyo.app.CommonExtra;
import au.com.tyo.app.ui.page.PageWebView;

/**
 * @author Eric Tang <eric.tang@tyo.com.au>
 */

public interface UI {

	boolean recreationRequired();

	void initializeUi(Context context);

	void setUiRecreationRequired(boolean value);

	void onPause(Context context);

	void onResume(Context context);

	void setSplashScreenOverlayView(View viewOverlay);

    void setupStartupAdView(View viewOverlay, Activity splashScreen);

    void onWidowReady();

    boolean onBackPressed();

    void onStop(Context currentActivity);

    void onAppStart();

    void setupTheme(Activity activity);

    void startActivity(Class activityTripClass);

	void startActivity(CommonExtra extra);

    void startActivity(Class cls, int flags, String key, Object data, View view, int requestCode);

	void startActivity(Class cls, int flags, String key, Object data, View view, int requestCode, boolean isMainActivity);

    UIPage getCurrentPage();

    void setCurrentScreen(UIPage screen);

    void onScreenAttached(UIPage screen);

    void viewHtmlPageFromAsset(String assetFile, String title, Integer statusBarColor, PageWebView.WebPageListener webPageListener);

    void showDialog(int messageArrayResId, int themeId, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener);

    Context getContext();

    PageWebView.WebPageListener getWebPageListener();
}
