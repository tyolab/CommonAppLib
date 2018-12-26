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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.tyo.android.AndroidUtils;
import au.com.tyo.android.CommonInitializer;
import au.com.tyo.android.CommonUIBase;
import au.com.tyo.android.DialogFactory;
import au.com.tyo.app.ui.activity.CommonActivityAbout;
import au.com.tyo.app.ui.activity.CommonActivityForm;
import au.com.tyo.app.ui.activity.CommonActivityList;
import au.com.tyo.app.ui.activity.CommonActivityWebView;
import au.com.tyo.app.CommonExtra;
import au.com.tyo.app.Constants;
import au.com.tyo.app.Controller;
import au.com.tyo.app.ui.activity.CommonActivityBackgroundProgress;
import au.com.tyo.app.ui.page.Page;
import au.com.tyo.app.ui.page.PageWebView;

import static au.com.tyo.app.Constants.REQUEST_NONE;

public class UIBase<ControllerType extends Controller> extends CommonUIBase implements UI {

    /**
     * It has to be a private member as the sub controller class won't be the same
     */
    private ControllerType controller;

    private UIPage currentScreen;

    private UIPage previousPage;

    private View splashScreenOverlayView;

    protected boolean uiRecreationRequired = false;

    private PageWebView.WebPageListener webPageListener;

    private UIPage mainPage;

    private UIPage contextPage;

    public UIBase(ControllerType controller) {
        this.controller = controller;
    }

    public ControllerType getController() {
        return controller;
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

    public UIPage getPreviousPage() {
        return previousPage;
    }

    @Override
    public void setPreviousPage(UIPage previousPage) {
        this.previousPage = previousPage;
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
        if (null != currentScreen)
            return currentScreen.onBackPressed();
        return false;
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
    public void startActivity(Page fromPage, Class aClass) {
        startActivity(fromPage, aClass, null);
    }

    @Override
    public void startActivity(Page fromPage, Class aClass, Object data) {
        startActivity(fromPage, aClass, -1/*Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK*/, null, data, null, REQUEST_NONE);
    }

    @Override
    public void startActivity(Page fromPage, CommonExtra extra) {
        fromPage.startActivity(extra);
    }

    public void startActivity(Page fromPage, Class cls, int flags, String key, Object data, View view, int requestCode) {
        startActivity(fromPage, cls, flags, key, data, view, requestCode, false);
    }

    @Override
    public void startActivity(Page fromPage, Class cls, int flags, String key, Object data, View view, int requestCode, boolean isMainActivity) {
        fromPage.startActivity(cls, flags, key, data, view, requestCode, isMainActivity);
    }

    @Override
    public void viewHtmlPageFromAsset(String assetFile, String title, Integer statusBarColor, PageWebView.WebPageListener webPageListener) {
        setWebPageListener(webPageListener);

        getCurrentPage().viewHtmlPageFromAsset(CommonActivityWebView.class, assetFile, title, statusBarColor);
    }

    @Override
    public void showDialog(String title, String info, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {
        Dialog dialog = createDialog(getCurrentPage().getActivity(), title, info, -1, okListener, cancelListener);
        dialog.show();
    }

    @Override
    public void showDialog(int messageArrayResId, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {
        showDialog(messageArrayResId, -1, okListener, cancelListener);
    }

    @Override
    public void showDialog(int messageArrayResId, int themeId, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {
        Dialog dialog = createDialog(getCurrentPage().getActivity(), messageArrayResId, themeId, okListener, cancelListener);
        dialog.show();
    }

    public static Dialog createDialog(Activity activity, String title, String info, int themeId, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {
        return DialogFactory.createDialog(activity, themeId, title, info, okListener, cancelListener);
    }

    public static Dialog createDialog(Activity activity, int messageArrayResId, int themeId, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {
        return DialogFactory.createDialog(activity, themeId, messageArrayResId, okListener, cancelListener);
    }

    public static Dialog createDialog(Activity activity, int messageArrayResId) {
       return createDialog(activity, messageArrayResId, -1, DialogFactory.dismissMeListener, null);
    }

    @Override
    public Context getContext() {
        return getCurrentPage().getActivity();
    }

    @Override
    public void gotoPage(Class cls) {
        gotoPage((Page) getCurrentPage(), cls);
    }

    @Override
    public void gotoPage(Class cls, Object data) {
        startActivity((Page) getCurrentPage(), cls, data);
    }

    @Override
    public void gotoPageWithData(Class cls, Object data, String title) {
        gotoPageWithData((Page) getCurrentPage(), cls, data, true, REQUEST_NONE, title);
    }

    @Override
    public void gotoPageWithData(Class cls, Object data) {
        gotoPageWithData((Page) getCurrentPage(), cls, data, true);
    }

    @Override
    public void gotoPageWithData(Class cls, Object data, boolean throughController) {
        gotoPageWithData((Page) getCurrentPage(), cls, data, throughController, REQUEST_NONE, null);
    }

    @Override
    public void gotoPageWithData(Class cls, String key, Object data, String title) {
        gotoPageWithData((Page) getCurrentPage(), cls, key, data, true, REQUEST_NONE, title);
    }

    @Override
    public void gotoPageWithData(Class cls, Object data, boolean throughController, int requestCode, String title) {
        gotoPageWithData((Page) getCurrentPage(), cls, Constants.DATA, data, throughController, requestCode, title);
    }

    @Override
    public void gotoPage(Page fromPage, Class cls) {
        startActivity(fromPage, cls);
    }

    @Override
    public void gotoPage(Page fromPage, Class cls, Object data) {
        startActivity(fromPage, cls, data);
    }

    @Override
    public void gotoPageWithData(Page fromPage, Class cls, Object data, String title) {
        gotoPageWithData(fromPage, cls, data, true, REQUEST_NONE, title);
    }

    @Override
    public void gotoPageWithData(Page fromPage, Class cls, Object data) {
        gotoPageWithData(fromPage, cls, data, true);
    }

    @Override
    public void gotoPageWithData(Page fromPage, Class cls, Object data, boolean throughController) {
        gotoPageWithData(fromPage, cls, data, throughController, REQUEST_NONE, null);
    }

    @Override
    public void gotoPageWithData(Page fromPage, Class cls, String key, Object data, String title) {
        gotoPageWithData(fromPage, cls, key, data, true, REQUEST_NONE, title);
    }

    @Override
    public void gotoPageWithData(Page fromPage, Class cls, Object data, boolean throughController, int requestCode, String title) {
        gotoPageWithData(fromPage, cls, Constants.DATA, data, throughController, requestCode, title);
    }

    @Override
    public void gotoPageWithData(Page fromPage, Class cls, String key, Object data, boolean throughController, int requestCode, String title) {
        controller.setParcel(null);
        Context context = fromPage.getActivity();

        CommonExtra extra = new CommonExtra(cls);
        extra.createIntent(context);

        extra.setRequestCode(requestCode);

        if (null != title)
            extra.setExtra(Constants.PAGE_TITLE, title);

        if (throughController) {
            controller.setParcel(data);
            extra.setExtra(Constants.DATA_LOCATION_CONTROLLER, true);
        } else {
            if (data instanceof Uri) {
                extra.getIntent().setData((Uri) data);
                // extra.getIntent().addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            else
                extra.setParcelExtra(key, data);
        }

        startActivity(fromPage, extra);
    }

    @Override
    public void pickFromList(Object list, String title) {
        gotoPageWithData((Page) getCurrentPage(), CommonActivityList.class, Constants.DATA_LIST, list, true, Constants.REQUEST_PICK, title);
    }

    @Override
    public PageWebView.WebPageListener getWebPageListener() {
        return webPageListener;
    }

    public void setWebPageListener(PageWebView.WebPageListener webPageListener) {
        this.webPageListener = webPageListener;
    }

    @Override
    public void gotoMainPage(Page fromPage) {
        gotoPage(fromPage, CommonInitializer.mainActivityClass);
    }

    @Override
    public void gotoAboutPage(Map data, String title) {
        gotoPageWithData((Page) getCurrentPage(), CommonActivityAbout.class, data, title);
    }

    @Override
    public void gotoListPageForResult(String title, String fullListTitle, List fullList, String quickAccessTitle, List quickAccess, int[] selected, int requestCode) {
        Map data = new HashMap();
        data.put(Constants.DATA_LIST_QUICK_ACCESS_TITLE, quickAccessTitle);
        data.put(Constants.DATA_LIST_QUICK_ACCESS_LIST, quickAccess);
        data.put(Constants.DATA_LIST_FULL_LIST_TITLE, fullListTitle);
        data.put(Constants.DATA_LIST_FULL_LIST_DATA, fullList);
        data.put(Constants.DATA_LIST_SELECTED, selected);
        gotoPageWithData((Page) getCurrentPage(), CommonActivityList.class, data, true, requestCode > -1  ? requestCode : Constants.REQUEST_PICK, title);
    }

    @Override
    public void gotoFormPage(String id, String title) {
        gotoPageWithData((Page) getCurrentPage(), CommonActivityForm.class, Constants.EXTRA_KEY_FORM_ID, id, false, REQUEST_NONE, title);
    }

    @Override
    public void gotoBackgroundProgressStatusPage(Page fromPage) {
        gotoPageWithData(fromPage, CommonActivityBackgroundProgress.class, null, false, Constants.REQUEST_CODE_DP_RESULT, null);
    }

    @Override
    public UIPage getContextPage() {
        return contextPage;
    }

    @Override
    public void setContextPage(UIPage contextPage) {
        this.contextPage = contextPage;
    }

    @Override
    public void onBackPressedOnProgressPage() {
        // no ops yet
    }
}
