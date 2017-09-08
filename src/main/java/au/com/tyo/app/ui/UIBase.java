/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD.
 *
 */

package au.com.tyo.app.ui;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import au.com.tyo.app.CommonExtra;
import au.com.tyo.app.Controller;
import au.com.tyo.app.R;

public class UIBase implements UI {

	/**
	 * It has to be a private member as the sub controller class won't be the same
	 */
	private Controller controller;

    private UIPage currentScreen;

    private View splashScreenOverlayView;

    protected boolean uiRecreationRequired = false;

	public UIBase(Controller controller) {
		this.controller = controller;
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
			controller.getSettings().setThemeId(R.style.CommonAppTheme_Light_NoActionBar);
			activity.setTheme(R.style.CommonAppTheme_Light_NoActionBar);
		}
	}

    @Override
    public void startActivity(Class aClass) {
        getCurrentPage().startActivity(aClass);
    }

    @Override
    public void startActivity(CommonExtra extra) {
        getCurrentPage().startActivity(extra);
    }

    @Override
    public void startActivity(Class cls, int flags, String key, Object data, View view, int requestCode) {
        getCurrentPage().startActivity(cls, flags, key, data, view, requestCode);
    }
}
