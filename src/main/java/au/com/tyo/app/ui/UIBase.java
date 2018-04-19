/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD.
 *
 */

package au.com.tyo.app.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.View;

import au.com.tyo.android.AndroidUtils;
import au.com.tyo.android.CommonUIBase;
import au.com.tyo.android.DialogFactory;
import au.com.tyo.app.CommonActivityList;
import au.com.tyo.app.CommonActivityWebView;
import au.com.tyo.app.CommonExtra;
import au.com.tyo.app.Constants;
import au.com.tyo.app.Controller;
import au.com.tyo.app.ui.page.PageWebView;

import static au.com.tyo.app.Constants.REQUEST_NONE;

public class UIBase extends CommonUIBase implements UI {

	/**
	 * It has to be a private member as the sub controller class won't be the same
	 */
	private Controller controller;

    private UIPage currentScreen;

    private View splashScreenOverlayView;

    protected boolean uiRecreationRequired = false;

    private PageWebView.WebPageListener webPageListener;

    private UIPage mainPage;

	public UIBase(Controller controller) {
		this.controller = controller;
	}

	@Override
    public UIPage getMainPage() {
        return mainPage;
    }

    @Override
    public void setMainPage(UIPage mainPage) {
        this.mainPage = mainPage;
    }

    public UIPage getCurrentPage() {
        return currentScreen;
    }

    public void setCurrentScreen(UIPage currentScreen) {
        this.currentScreen = currentScreen;
    }

    public View getSplashScreenOverlayView() {
        return splashScreenOverlayView;
    }

    @Override
    public void setSplashScreenOverlayView(View viewOverlay) {
        this.splashScreenOverlayView = viewOverlay;
    }

    @Override
    public boolean recreationRequired() {
        return uiRecreationRequired;
    }

    @Override
    public void onScreenAttached(UIPage screen) {
        currentScreen = screen;
    }

    public void setUiRecreationRequired(boolean value) {
        uiRecreationRequired = value;
    }

    @Override
    public void setupStartupAdView(View viewOverlay, Activity splashScreen) {

    }

    /**
     * When the window is create, all layout / elements / components are inflated
     *
     */
    @Override
    public void onWidowReady() {
        getCurrentPage().initialiseComponents();
    }

    public void initializeUi(Context context) {
        setUiRecreationRequired(false);
        getCurrentPage().initializeUi();
        onPageInitialized();
    }

    protected void onPageInitialized() {
        // do nothing
    }

    @Override
    public void onPause(Context context) {

    }

    @Override
    public void onResume(Context context) {

    }

    @Override
    public boolean onBackPressed() {
		return currentScreen.onBackPressed();
    }

    @Override
    public void onStop(Context currentActivity) {

    }

    /**
	 *
	 */
	@Override
	public void onAppStart() {

	}

	/**
	 *
	 * @param activity
	 */
	public void setupTheme(Activity activity) {
		int themeId = controller.getSettings().getThemeId();
		if (themeId > 0)
			activity.setTheme(themeId);
		else {
			// we use light theme by default
//			controller.getSettings().setThemeId(R.style.CommonAppTheme_Light_NoActionBar);
//			activity.setTheme(R.style.CommonAppTheme_Light_NoActionBar);
            try {
                themeId = AndroidUtils.getPredefinedApplicationThemeId(activity);
            } catch (PackageManager.NameNotFoundException e) {

            }
            if (themeId > 0)
                controller.getSettings().setThemeId(themeId);
        }
	}

    @Override
    public void startActivity(Class aClass) {
        startActivity(aClass, null);
    }

    @Override
    public void startActivity(Class aClass, Object data) {
        startActivity(aClass, -1/*Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK*/, null, data, null, REQUEST_NONE);
    }

    @Override
    public void startActivity(CommonExtra extra) {
        getCurrentPage().startActivity(extra);
    }

    public void startActivity(Class cls, int flags, String key, Object data, View view, int requestCode) {
        startActivity(cls, flags, key, data, view, requestCode, false);
    }

    @Override
    public void startActivity(Class cls, int flags, String key, Object data, View view, int requestCode, boolean isMainActivity) {
        getCurrentPage().startActivity(cls, flags, key, data, view, requestCode, isMainActivity);
    }

    @Override
    public void viewHtmlPageFromAsset(String assetFile, String title, Integer statusBarColor, PageWebView.WebPageListener webPageListener) {
	    setWebPageListener(webPageListener);

        getCurrentPage().viewHtmlPageFromAsset(CommonActivityWebView.class, assetFile, title, statusBarColor);
    }

    @Override
    public void showDialog(int messageArrayResId, int themeId, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {
        Dialog dialog = DialogFactory.createDialog(getCurrentPage().getActivity(), themeId, messageArrayResId, okListener, cancelListener);
        dialog.show();
    }

    @Override
    public Context getContext() {
        return getCurrentPage().getActivity();
    }

    public void gotoPage(Class cls) {
        startActivity(cls);
    }

    public void gotoPage(Class cls, Object data) {
        startActivity(cls, data);
    }

    protected void gotoPageWithData(Class cls, Object data, String title) {
        gotoPageWithData(cls, data, true, REQUEST_NONE, title);
    }

    protected void gotoPageWithData(Class cls, Object data) {
	    gotoPageWithData(cls, data, true, REQUEST_NONE, null);
    }

    protected void gotoPageWithData(Class cls, Object data, boolean throughController, int requestCode, String title) {
	    gotoPageWithData(cls, Constants.DATA, data, throughController, requestCode, title);
    }

    protected void gotoPageWithData(Class cls, String key, Object data, boolean throughController, int requestCode, String title) {
        controller.setParcel(null);
        Context context = getCurrentPage().getActivity();

        CommonExtra extra = new CommonExtra(cls);
        extra.createIntent(context);

        extra.setRequestCode(requestCode);

        if (null != title)
            extra.setExtra(Constants.PAGE_TITLE, title);

        if (throughController)
            controller.setParcel(data);
        else {
            if (data instanceof Uri)
                extra.getIntent().setData((Uri) data);
            else
                extra.setParcelExtra(key, data);
        }

        startActivity(extra);
    }

    @Override
    public void pickFromList(Object list, String title) {
        gotoPageWithData(CommonActivityList.class, Constants.DATA_LIST, list, true, Constants.REQUEST_PICK, title);
    }

    @Override
    public PageWebView.WebPageListener getWebPageListener() {
        return webPageListener;
    }

    public void setWebPageListener(PageWebView.WebPageListener webPageListener) {
        this.webPageListener = webPageListener;
    }
}
