package au.com.tyo.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import au.com.tyo.android.AndroidUtils;
import au.com.tyo.android.CommonInitializer;
import au.com.tyo.app.ui.Page;
import au.com.tyo.app.ui.UI;
import au.com.tyo.app.ui.UIEntity;
import au.com.tyo.app.ui.UIPage;
import au.com.tyo.utils.StringUtils;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 23/5/17.
 */

public class PageAgent {

    public static final String LOG_TAG = PageAgent.class.getSimpleName();

    /**
     *
     */
    private static String pagesPackage;

    /**
     *
     */
    protected Controller controller;

    /**
     * the main activity / fragment
     */
    protected Object uiObject;

    /**
     * TODO
     *
     * maybe we could maintain a list of activities we use
     */

    protected View contentView;

    /**
     *
     */
    private ActivityActionListener actionListener;

    /**
     *
     */
    private Class pageClass = null;

    /**
     * Could be a nested page
     *
     */
    protected UIEntity page;

    /**
     *
     */
    private Context context;
    
    public interface ActivityActionListener {

        void bindData(Intent intent);

        void onSaveData(Bundle savedInstanceState);

        void bindData();
    }


    public PageAgent(Object uiObject) {
        this.uiObject = uiObject;

        if (isActivity()) {
            context = getActivity();
        }
        else if (isFragment()) {
            context = getFragment().getContext();
        }
        else
            throw new IllegalStateException("uiObject must be an instance of Activity or Fragment");

        if (uiObject instanceof ActivityActionListener)
            actionListener = (ActivityActionListener) uiObject;

        init();
    }

    public void setPageClass(Class pageClass) {
        this.pageClass = pageClass;
    }

    public static void setPagesPackage(String packageName) {
        pagesPackage = packageName;
    }

    public UIPage getPage() {
        return (UIPage) page;
    }

    public void setPage(UIPage page) {
        this.page = page;
    }

    private boolean isActivity() {
        return (uiObject instanceof Activity);
    }

    private boolean isFragment() {
        return (uiObject instanceof android.support.v4.app.Fragment);
    }

    public Activity getActivity() {
        return (Activity) uiObject;
    }

    public android.support.v4.app.Fragment getFragment() {
        return (android.support.v4.app.Fragment) uiObject;
    }

    private void init() {
        if (controller == null) {
            if (CommonApp.getInstance() == null)
                CommonApp.setInstance(CommonInitializer.initializeInstance(CommonApp.class, context));
            controller = (Controller) CommonApp.getInstance();
        }
    }

    /**
     * Create an assoicated page that contains all the widgets / controls
     *
     * if a custom page is needed just override this method to create a different page setting
     */
    protected void createPage() {
        if (!isActivity())
            return;

        if (null == page) {
            if (null == pageClass) {
                if (pagesPackage == null)
                    pagesPackage = AndroidUtils.getPackageName(context);

                try {
                    String pageActivityClassName = getActivity().getClass().getName();
                    String extName;
                    if (pageActivityClassName.indexOf('.') > -1)
                        extName = pageActivityClassName.substring(pageActivityClassName.lastIndexOf('.') + "Activity".length() + 1);
                    else
                        extName = pageActivityClassName.substring("Activity".length());
                    String pageClassName = pagesPackage + ".Page" + extName;
                    pageClass = Class.forName(pageClassName);
                }
                catch (Exception ex) {

                }
            }

            if (pageClass != null) {
                try {
                    Constructor ctor = null;
                    ctor = pageClass.getConstructor(Controller.class, Activity.class);
                    page = (UIPage) ctor.newInstance(new Object[]{controller, getActivity()});
                } catch (NoSuchMethodException e) {
                    Log.e(LOG_TAG, StringUtils.exceptionStackTraceToString(e));
                } catch (IllegalAccessException e) {
                    Log.e(LOG_TAG, StringUtils.exceptionStackTraceToString(e));
                } catch (InstantiationException e) {
                    Log.e(LOG_TAG, StringUtils.exceptionStackTraceToString(e));
                } catch (InvocationTargetException e) {
                    Log.e(LOG_TAG, StringUtils.exceptionStackTraceToString(e));
                }
            }
        }

        if (null == page)
            page = new Page(controller, getActivity());
    }

    public ActivityActionListener getActionListener() {
        return actionListener;
    }

    public void setActionListener(ActivityActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void onCreate(Bundle savedInstanceState) {
        setControllerContext();

        initialise(savedInstanceState);

        onActivityStart();
    }

    /**
     *
     */
    protected void onActivityStart() {
		/*
         * after UI initialization, do whatever needs to be done, like setting tup the settings, etc.
         *
         * both page / controller need to be informed
         *
         */

        controller.onCurrentActivityStart();

        page.onActivityStart();
    }

    protected void startDataHandlingActivity() {
        // do nothing here
    }

    protected void createUI(UIPage screen) {
        if (controller.getUi() == null || controller.getUi().recreationRequired())
            controller.createUi();

        if (isActivity()) {
            controller.getUi().setCurrentScreen(screen);
            controller.getUi().onScreenAttached(screen);
        }
    }

    protected void setupActionbar() {

        if (AndroidUtils.getAndroidVersion() < 7)
            setupTitleBar1();

        if (AndroidUtils.getAndroidVersion() >= 7)
            setupActionBar(controller.getUi());
        else
            setupTitleBar2();
    }

    @SuppressLint("NewApi")
    protected Object setupActionBar(UI ui) {
        return ui.getCurrentPage().setupActionBar();
    }

    private Activity getActivitySelf() {
        if (isActivity())
            return getActivity();
        return getFragment().getActivity();
    }

    protected boolean checkExtras() {
        Activity activity= getActivitySelf();

        String action = activity.getIntent().getAction();
        if ((action != null &&
                action.equalsIgnoreCase("android.intent.action.ASSIST")) ||
                activity.getIntent().getExtras() != null ||
                (activity.getIntent().getDataString() != null &&
                        activity.getIntent().getDataString().length() > 0))
            return true;
        return false;
    }

    protected void processExtras(Bundle savedInstanceState) {
        Activity activity = getActivitySelf();
        if (savedInstanceState == null) {
            Intent intent = activity.getIntent();
            if (null != intent && (null != intent.getData() || null != intent.getExtras())) {
                String action = intent.getAction();
                // to see where it is from
                if (action != null && action.equalsIgnoreCase("android.intent.action.ASSIST")) {
                    Log.d(LOG_TAG, "starting native voice recognizer from main activity");
                    activity.getIntent().setAction("");
                } else {
                    if (getActionListener() != null)
                        getActionListener().bindData(intent);
                    else
                        consumeInterActivityData(intent);
                }
            }
            else {
                // in case we need to bind other data that is not passed through intent
                if (getActionListener() != null)
                    getActionListener().bindData();
            }
        }
        else if (getActionListener() != null)
            getActionListener().onSaveData(savedInstanceState);
    }

    /**
     *
     * @param intent
     */
    protected void consumeInterActivityData(Intent intent) {
        //  do nothing

//        String url = intent.getDataString();
//        if (url != null && url.length() > 0) {
//            intent.setData(null);
//        }
//
//        intent.replaceExtras((Bundle) null);
    }

    public void preInitialize(Bundle savedInstanceState, UIPage screen) {

        if (savedInstanceState != null)
            controller.onRestoreInstanceState(savedInstanceState);

        if (isActivity()) {
            createUI(screen);

            controller.getUi().setupTheme(getActivity());
        }
    }

    /**
     * Super call is happened here
     *
     * @param savedInstanceState
     */
    protected void initialise(Bundle savedInstanceState) {

        /**
         * do it in a different way
         *
         * alternatively,
         *

         setContentView(R.layout.activity_common);
         FrameLayout frameLayout = (FrameLayout) getWindow()
         .getDecorView().findViewById(android.R.id.content);

         */

        /**
         * Create contentView
         */
        initialiseUi();

        if (contentView.getParent() != null && contentView instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) contentView.getParent();
            if (null != parent) parent.removeView(contentView);
        }

        if (isActivity())
            getActivity().setContentView(contentView);

        /**
         * set up action bar as we use toolbar now it is part of content view
         */
        setupActionbar();

		/*
		 * now show the overflow menu
		 */
        showOverflowMenu();

        processExtras(savedInstanceState);

        controller.onUiReady();
    }

    /**
     * Call activity's onUICreated after UI gets initialised
     */
    protected void initialiseUi() {
        controller.getUi().initializeUi(context);
        contentView = controller.getUi().getCurrentPage().getMainView();
    }

    private void setupTitleBar1() {
//        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
    }

    private void setupTitleBar2() {
//        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.information_view);
    }

    /**
     * show the overflow menu (three-dot)
     */
    private void showOverflowMenu() {
        try {
            ViewConfiguration config = ViewConfiguration.get(context);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if(menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param page
     */
    public void onResume(UIPage page) {
        controller.getUi().setCurrentScreen(page);

        setControllerContext();

        /**
         * Can't remember why we need to deal with extra onResume
         * comment it off fow now
         */
        // processExtras();

        controller.onResume();
    }

    private void setControllerContext() {

        if (isActivity()) {
            controller.setCurrentActivity(getActivity());
            controller.setContext(getActivity());
        }
    }

    public void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
//	         controller.search(query);
        }
    }
}
