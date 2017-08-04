package au.com.tyo.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import java.lang.reflect.Field;

import au.com.tyo.android.AndroidUtils;
import au.com.tyo.android.CommonInitializer;
import au.com.tyo.app.ui.UI;
import au.com.tyo.app.ui.UIPage;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 23/5/17.
 */

public class CommonActivityAgent {

    public static final String LOG_TAG = CommonActivityAgent.class.getSimpleName();

    protected Controller controller;

    /**
     * the main activity
     */
    protected Activity activity;

    /**
     * TODO
     *
     * maybe we could maintain a list of activities we use
     */

    protected View contentView;
    
    private ActivityActionListener actionListener;
    
    public interface ActivityActionListener {

        void bindData(Intent intent);

        void onSaveData(Bundle savedInstanceState);

        void bindData();
    }


    public CommonActivityAgent(Activity activity) {
        this.activity = activity;

        if (activity instanceof ActivityActionListener)
            actionListener = (ActivityActionListener) activity;

        init();
    }

    private void init() {
        if (controller == null) {
            if (CommonApp.getInstance() == null)
                CommonApp.setInstance(CommonInitializer.initializeInstance(CommonApp.class, activity));
            controller = (Controller) CommonApp.getInstance();
        }
    }

    public ActivityActionListener getActionListener() {
        return actionListener;
    }

    public void setActionListener(ActivityActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void onCreate(Bundle savedInstanceState) {

        controller.setCurrentActivity(activity);
        controller.setContext(activity);

        initialise(savedInstanceState);

        onActivityStart();
    }

    /**
     *
     */
    protected void onActivityStart() {
		/*
         * after UI initialization, do whatever needs to be done, like setting tup the settings, etc.
         */
        controller.onCurrentActivityStart();
    }

    protected void startDataHandlingActivity() {
        // do nothing here
    }

    protected void createUI(UIPage screen) {
        if (controller.getUi() == null || controller.getUi().recreationRequired())
            controller.createUi();
        controller.getUi().setCurrentScreen(screen);
        controller.getUi().onScreenAttached(screen);
    }

    protected void setupActionbar() {
        Object toolbar = controller.getUi().getCurrentScreen().getToolbar();

        if (null == toolbar)
            toolbar = activity.findViewById(R.id.tyodroid_toolbar);

        if (toolbar != null) {
            try {
                ((AppCompatActivity) activity).setSupportActionBar((Toolbar) toolbar);
            }
            catch (Exception ex) {
                CommonAppLog.error(LOG_TAG, ex);
            }
        }

        if (AndroidUtils.getAndroidVersion() < 7)
            setupTitleBar1();

        if (AndroidUtils.getAndroidVersion() >= 7)
            setupActionBar(controller.getUi());
        else
            setupTitleBar2();
    }

    @SuppressLint("NewApi")
    protected Object setupActionBar(UI ui) {
        Object bar = null;
        if (AndroidUtils.getAndroidVersion() >= 11)
            bar = activity.getActionBar();

        if (bar == null && activity instanceof AppCompatActivity)
            bar = ((AppCompatActivity) activity).getSupportActionBar();

        return ui.getCurrentScreen().setupActionBar(bar);
    }

    protected boolean checkExtras() {
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

        createUI(screen);

        controller.getUi().setupTheme(activity);
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
        activity.setContentView(contentView);

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
        controller.getUi().initializeUi(activity);
        contentView = controller.getUi().getCurrentScreen().getMainView();
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
            ViewConfiguration config = ViewConfiguration.get(activity);
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

        controller.setCurrentActivity(activity);
        controller.setContext(activity);

        /**
         * Can't remember why we need to deal with extra onResume
         * comment it off fow now
         */
        // processExtras();

        controller.onResume();
    }

    public void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
//	         controller.search(query);
        }
    }
}
