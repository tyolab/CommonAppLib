/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD.
 *
 */

package au.com.tyo.app.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import java.util.List;
import java.util.Map;

import au.com.tyo.app.CommonExtra;
import au.com.tyo.app.ui.page.Page;
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

    void startActivity(Page fromPage, Class activityTripClass);

    void startActivity(Page fromPage, Class aClass, Object data);

    void startActivity(Page fromPage, CommonExtra extra);

    void startActivity(Page fromPage, Class cls, int flags, String key, Object data, View view, int requestCode);

	void startActivity(Page fromPage, Class cls, int flags, String key, Object data, View view, int requestCode, boolean isMainActivity);

    UIPage getMainPage();

    void setMainPage(UIPage mainPage);

    UIPage getCurrentPage();

    void setCurrentScreen(UIPage screen);

    void onScreenAttached(UIPage screen);

    void viewHtmlPageFromAsset(String assetFile, String title, Integer statusBarColor, PageWebView.WebPageListener webPageListener);

    void showDialog(String title, String info, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener);

    void showDialog(int messageArrayResId, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener);

    void showDialog(int messageArrayResId, int themeId, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener);

    Context getContext();

    PageWebView.WebPageListener getWebPageListener();

    void startActivity(Context context, Class cls, boolean isMainActivity);

    void gotoPage(Class cls);

    void gotoPage(Class cls, Object data);

    void gotoPageWithData(Class cls, Object data, String title);

    void gotoPageWithData(Class cls, Object data);

    void gotoPageWithData(Class cls, Object data, boolean throughController);

    void gotoPageWithData(Class cls, String key, Object data, String title);

    void gotoPageWithData(Class cls, Object data, boolean throughController, int requestCode, String title);

    void gotoPage(Page fromPage, Class cls);

    void gotoPage(Page fromPage, Class cls, Object data);

    void gotoPageWithData(Page fromPage, Class cls, Object data, String title);

    void gotoPageWithData(Page fromPage, Class cls, Object data);

    void gotoPageWithData(Page fromPage, Class cls, Object data, boolean throughController);

    void gotoPageWithData(Page fromPage, Class cls, String key, Object data, String title);

    void gotoPageWithData(Page fromPage, Class cls, Object data, boolean throughController, int requestCode, String title);

    void gotoPageWithData(Page fromPage, Class cls, String key, Object data, boolean throughController, int requestCode, String title);

    void pickFromList(Object list, String title);

    void gotoMainPage(Page fromPage);

    void gotoMainPage();

    void gotoAboutPage();

    void gotoAboutPage(Map data, String title);

    void rateThisApp();

    void gotoListPageForResult(int listId, String title, String fullListTitle, List fullList, String quickAccessTitle, List quickAccess, int[] selected, int requestCode, boolean showSearchBar);

    void gotoFormPage(String id, String title);

    void gotoBackgroundProgressStatusPage(Page fromPage, String title);

    UIPage getContextPage();

    void setContextPage(UIPage contextPage);

    boolean onBackPressedOnProgressPage();

    UIPage getPreviousPage();

    void setPreviousPage(UIPage page);

    void openUrl(String url);

    void showFormValidationFailureDialog();

    void gotoPageFileManager(List unsafeFiles, String title, boolean allowMultipleSelections);
}
