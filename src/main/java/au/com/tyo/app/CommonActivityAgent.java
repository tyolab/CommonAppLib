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

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 23/5/17.
 */

public class CommonActivityAgent {

    public static final String LOG_TAG = CommonActivityAgent.class.getSimpleName();

    protected Controller controller;

    protected Activity activity;

    protected View contentView;


    public CommonActivityAgent(Activity activity) {
        this.activity = activity;

        init();
    }

    private void init() {
        if (controller == null) {
            if (CommonApp.getInstance() == null)
                CommonApp.setInstance(CommonInitializer.initializeInstance(CommonApp.class, activity));
            controller = (Controller) CommonApp.getInstance();
        }
    }

    public void onCreate(Bundle savedInstanceState) {

        controller.setCurrentActivity(activity);
        controller.setContext(activity);

        initialise(savedInstanceState);

        onActivityStart();
    }

    protected void onActivityStart() {
		/*
         * after UI initialization, do whatever needs to be done, like setting tup the settings, etc.
         */
        controller.onAppStart();
    }

    protected void startDataHandlingActivity() {
        // do nothing here
    }

    protected void createUI() {
        if (controller.getUi() == null || controller.getUi().recreationRequried())
            controller.createUi();
    }

    protected void setupTheme() {
        int themeId = controller.getSettings().getThemeId();
        if (themeId > 0)
            activity.setTheme(themeId);
        else {
            // we use light theme by default
            controller.getSettings().setThemeId(R.style.AppTheme_Light_NoActionBar);
            activity.setTheme(R.style.AppTheme_Light_NoActionBar);
        }
    }

    protected void setupActionbar() {
        Object toolbar = controller.getUi().getToolbar();

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

        return ui.setupActionBar(bar);
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

    protected void processExtras() {
        Intent intent = activity.getIntent();
        String action =intent.getAction();
        // to see where it is from
        if (action != null && action.equalsIgnoreCase("android.intent.action.ASSIST")) {
            Log.d(LOG_TAG, "starting native voice recognizer from main activity");
            activity.getIntent().setAction("");
        }
        else {
            String url = intent.getDataString();
            if (url != null && url.length() > 0) {
                intent.setData(null);
            }

            intent.replaceExtras((Bundle) null);
        }
    }

    public void preInitialize(Bundle savedInstanceState) {

        if (savedInstanceState != null)
            controller.onRestoreInstanceState(savedInstanceState);

        createUI();
        setupTheme();
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


        processExtras();

        controller.onUiReady();
    }

    protected void initialiseUi() {
        controller.getUi().initializeUi(activity);
        contentView = controller.getUi().getMainView();
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

    public void onResume() {
        controller.setCurrentActivity(activity);
        controller.setContext(activity);

        processExtras();

        controller.onResume();
    }

    public void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
//	         controller.search(query);
        }
    }
}
