package au.com.tyo.app.ui;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import au.com.tyo.android.AndroidUtils;
import au.com.tyo.app.CommonAppLog;
import au.com.tyo.app.CommonExtra;
import au.com.tyo.app.Constants;
import au.com.tyo.app.Controller;
import au.com.tyo.app.R;
import au.com.tyo.app.model.Searchable;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 18/7/17.
 *
 * A screen has
 *
 * === SECTION_HEADER ===
 *
 *  <Body>
 *
 *   Content (a PageFragment)
 *
 *   </Body>
 *
 * === FOOTER ===
 *
 */

public class Page extends PageFragment implements UIPage, MenuItem.OnMenuItemClickListener {

    private static final String LOG_TAG = "Screen";

    /**
     * Common Widgets
     */
    protected ViewContainerWithProgressBar mainViewContainer;

    protected View pageView;

    protected View pageProgressView;
    protected TextView textViewProgressInfo;

    protected ViewGroup footerView;

    protected ViewGroup headerView;

    protected AllAdView ad;

    private SuggestionView suggestionView;

    private SearchView searchView;

    private BodyView bodyView;

    private boolean toShowSearchView;

    protected boolean hideActionBar = false;

    protected int mainUiResId = -1;

    private InformationView informationView;

    private ActionBarMenu actionBarMenu;

    /**
     * The the main / content view from the screen
     */
    protected View mainView;

    /**
     * Controller
     */
    private Controller controller;

    /**
     * The associated activity
     */
    private Activity activity;

    /**
     * The indicator for whether the page is a child page
     */
    private boolean isSubpage = false;

    /**
     * The request code
     */
    private int requestCode = -1;

    /**
     * The result that needs to be returned to previous page
     */
    private Object result;

    /**
     * The fragments assicated with the pages
     */
    private List<Fragment> fragments;

    /**
     * Page title for showing on the action bar (tool bar)
     */
    private String title;

    /**
     * Colors for the page elements
     */
    protected Integer statusBarColor = null;
    protected Integer toolbarColor = null;
    protected Integer bodyViewColor = null;
    protected Integer titleTextColor = null;

    /**
     *
     * @param controller
     * @param activity
     */
    public Page(Controller controller, Activity activity) {
        this.activity = activity;
        this.controller = controller;
        toShowSearchView = false;

        configActionBarMenu(controller);
    }

    @Override
    public void setStatusBarColor(Integer statusBarColor) {
        this.statusBarColor = statusBarColor;
    }

    @Override
    public void setToolbarColor(Integer toolbarColor) {
        this.toolbarColor = toolbarColor;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Activity getActivity() {
        return activity;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    @Override
    public boolean isSubpage() {
        return isSubpage;
    }

    @Override
    public void setSubpage(boolean subpage) {
        isSubpage = subpage;
    }

    @Override
    public boolean isToShowSearchView() {
        return toShowSearchView;
    }

    @Override
    public void setToShowSearchView(boolean toShowSearchView) {
        this.toShowSearchView = toShowSearchView;
    }

    /**
     *
     * @param controller
     */
    protected void configActionBarMenu(Controller controller) {
        // do nothing yet
        if (null == actionBarMenu)
            actionBarMenu = new ActionBarMenu();
    }

    @Override
    public void setMainView(View mainView) {
        this.mainView = mainView;
    }

    @Override
    public void assignMainUiContainer(FrameLayout frameLayout) {
        if (mainView.getParent() != null)
            ((ViewGroup) mainView.getParent()).removeView(mainView);
        mainView.setVisibility(View.VISIBLE);
        frameLayout.addView(mainView);
    }

    public ActionBarMenu getActionBarMenu() {
        return actionBarMenu;
    }

    public void setActionBarMenu(ActionBarMenu actionBarMenu) {
        this.actionBarMenu = actionBarMenu;
    }

    public InformationView getInformationView() {
        return informationView;
    }

    public void setInformationView(InformationView informationView) {
        this.informationView = informationView;
    }

    public int getMainUiResId () {
        return  mainUiResId;
    }

    @Override
    public void onPause(Context context) {
        // should be overrode if needed
    }

    @Override
    public void onResume(Context context) {
        // should be overrode if needed
    }

    @Override
    public void onStop(Context currentActivity) {
        // should be overrode if needed
    }

    @Override
    public void initialiseComponents() {
        if (null != bodyView) {
            bodyView.detectScreenLocation();

            DisplayMetrics dm = new DisplayMetrics();
            controller.getCurrentActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            int offset = dm.heightPixels - bodyView.getMeasuredHeight();
            bodyView.setScreenOffset(offset);
        }
    }

    public Controller getController() {
        return controller;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void initializeUi(View v) {
        setMainView(v);
        initializeUi();
    }

    @Override
    public void initializeUi() {

        if (null == mainView) {
            if (mainUiResId == -1) {
                mainUiResId = R.layout.activity_common;
            }
            setMainView(LayoutInflater.from(activity).inflate(mainUiResId, null));
        }

        if (null != mainView.findViewById(R.id.activity_view_with_progressbar)) {
            mainViewContainer = (ViewContainerWithProgressBar) mainView.findViewById(R.id.activity_view_with_progressbar);
            mainViewContainer.addContentView(R.layout.page);
            setMainView(mainViewContainer.getContentView());
        }

        setupComponents();
    }

    @Override
    public void hideSearchBar() {
        if (null != searchView)
            searchView.setVisibility(View.GONE);
    }

    @Override
    public void showSearchBar() {
        if (null != searchView)
            searchView.setVisibility(View.VISIBLE);
    }

    @Override
    public SearchInputView getSearchInputView() {
        return searchView.getSearchInputView();
    }

    @Override
    public void setSuggestionViewVisibility(boolean b) {
        if (b) {
            ad.hide();
            suggestionView.setVisibility(View.VISIBLE);
            super.hideContentView();
        }
        else {
            ad.show();
            super.showContentView();
            suggestionView.setVisibility(View.GONE);
        }
    }

    @Override
    public SuggestionView getSuggestionView() {
        return suggestionView;
    }

    /**
     * do whatever it needs to be done, for example, when search input is focused
     * lock the drawer views
     */
    @Override
    public void onSearchInputFocusStatus(boolean focused) {

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (null != ad)
            ad.loadBannerAd();
    }

    @Override
    public void setupComponents() {
        /**
         * Only if the default layout is used then we do the UI elements (search bar, footer, body, header, etc) setup
         *
         * for the extreme case we might need the sanity check
         */
        /**
         * actionbar / toolbar
         */
        setupToolbar();

        /**
         *
         */
        pageView = mainView.findViewById(R.id.tyodroid_page);
        pageProgressView = mainView.findViewById(R.id.tyodroid_page_progress_bar);
        textViewProgressInfo = mainView.findViewById(R.id.tv_progress_info);

        /**
         * the root view of body.xml
         */
        bodyView = (BodyView) mainView.findViewById(R.id.body_view);

        /**
         * the root view of content.xml
         */
        setContentView((ViewGroup) mainView.findViewById(R.id.content_view));
        loadContentView();

        footerView = (ViewGroup) mainView.findViewById(R.id.footer_view);

        setupHeader();
        setupSearchBar();

        if (null != mainView) {
            ad = (AllAdView) mainView.findViewById(R.id.all_ad_view);
            if (null != ad) {
                addAdView();

                if (controller.getSettings().hasAd())
                    ad.loadBannerAd();
            }
        }

        if (!controller.getNetworkMonitor().hasInternet())
            onNetworkDisonnected();

        setupActionBarMenu();
    }

    protected void loadContentView() {
        loadContentView(activity);
    }

    private void setupToolbar() {
        actionBarMenu.setToolbar((Toolbar) mainView.findViewById(R.id.tyodroid_toolbar));
    }

    /**
     *
     */
    protected void setupSearchBar() {
        if (hasSearchBar())
            setupSearchView();
    }

    /**
     *
     */
    protected void setupHeader() {
        headerView = (ViewGroup) mainView.findViewById(R.id.header_view);
        if (!toShowSearchView) {
            // the search view is in the header
            // hideSearchBar();
            hideHeader();
        }
    }

    /**
     *
     */
    protected void setupActionBarMenu() {
        // do nothing
    }

    private boolean hasSearchBar() {
        return null != mainView.findViewById(R.id.search_nav_bar);
    }

    /*
     * not all the need search function
     * keep these line for future use
     */
    public void setupSearchView() {
        searchView = (SearchView) mainView.findViewById(R.id.search_nav_bar);
        searchView.setupComponents(controller);

        suggestionView = (SuggestionView) mainView.findViewById(R.id.suggestion_view);
        suggestionView.setupComponents(controller);
        suggestionView.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Searchable item = (Searchable) suggestionView.getAdapter().getItem(position);

                if (item instanceof Parcelable) {
                    // just pass the item to the previous activity
                    result = item;
                    activity.finish();
                }
                else {
                    controller.getUi().getCurrentPage().hideSuggestionView();
                    controller.onOpenSearchItemClicked(item);
                }
            }
        });
    }

    public void showFooter() {
        footerView.setVisibility(View.VISIBLE);
    }

    public void hideFooter() {
        footerView.setVisibility(View.GONE);
    }

    public ViewGroup getFooterView() {
        return footerView;
    }

    @Override
    public void showHeader() {
        if (null != headerView)
            headerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideHeader() {
        headerView.setVisibility(View.GONE);
    }

    public ViewGroup getHeaderView() {
        return headerView;
    }

    public SearchView getSearchView() {
        return searchView;
    }

    @Override
    public ViewGroup getBodyView() {
        return bodyView;
    }

    public void setBodyView(BodyView bodyView) {
        this.bodyView = bodyView;
    }

    protected void addAdView() {
        ad.initialize(controller, footerView);
    }

    @Override
    public void hideAd() {
        ad.hide();
    }

    @Override
    public void showAd() {
        ad.show();
    }

    protected void showDialog(Dialog dialog) {
        if(dialog != null && ! controller.getCurrentActivity().isFinishing())
            dialog.show();
    }

    @Override
    public View getMainView() {
        return mainView;
    }

    /**
     *
     */
    @Override
    public void onAdLoaded() {
        Log.d(LOG_TAG, "Ad loaded");
        showAd();
    }

    /**
     * normally we set the action bar like this
     */
    @SuppressLint("NewApi")
    @Override
    public Object setupActionBar() {
        Object barObj = null;

        if (activity instanceof AppCompatActivity) {
            Toolbar toolbar = actionBarMenu.getToolbar();

            if (null == toolbar) {
                toolbar = (Toolbar) activity.findViewById(R.id.tyodroid_toolbar);
                actionBarMenu.setToolbar(toolbar);
            }

            if (toolbar != null) {
                try {
                    ((AppCompatActivity) activity).setSupportActionBar((Toolbar) toolbar);

                } catch (Exception ex) {
                    CommonAppLog.error(LOG_TAG, ex);
                }
            }
            barObj = ((AppCompatActivity) activity).getSupportActionBar();
        }
        else if (AndroidUtils.getAndroidVersion() >= 11)
            barObj = activity.getActionBar();

        if (barObj != null) {

            actionBarMenu.setActionBar(barObj);

            if (barObj instanceof android.app.ActionBar) {
                android.app.ActionBar bar = (ActionBar) barObj;
                if (hideActionBar) {
                    bar.hide();
                }
                else {
                    if (controller.getContext().getResources().getBoolean(R.bool.showIconOnActionBar)) {
                        if (AndroidUtils.getAndroidVersion() >= 11)
                            bar.setDisplayUseLogoEnabled(true);

                        if (AndroidUtils.getAndroidVersion() >= 14)
                            bar.setLogo(R.drawable.ic_logo);
                    }
                    else {
                        bar.setHomeButtonEnabled(true);
                        if (AndroidUtils.getAndroidVersion() >= 11) {
                            bar.setDisplayShowHomeEnabled(true);
                        }

                        bar.setDisplayHomeAsUpEnabled(true);
                    }

                    bar.setDisplayShowTitleEnabled(controller.getContext().getResources().getBoolean(R.bool.showTitleOnActionBar));
                }
            }
            else if (barObj instanceof android.support.v7.app.ActionBar) {
                android.support.v7.app.ActionBar bar = (android.support.v7.app.ActionBar) barObj;

                if (hideActionBar) {
                    bar.hide();
                }
                else {
                    boolean showTitle = controller.getContext().getResources().getBoolean(R.bool.showTitleOnActionBar);

                    if (controller.getContext().getResources().getBoolean(R.bool.showIconOnActionBar)){
                        bar.setLogo(R.drawable.ic_logo);
                        // bar.setIcon(R.drawable.ic_launcher);
                        bar.setDisplayUseLogoEnabled(true);
                    }
                    bar.setHomeButtonEnabled(true);

                    bar.setDisplayShowCustomEnabled(true);
                    bar.setDisplayShowHomeEnabled(true);
                    bar.setDisplayShowTitleEnabled(showTitle);
                    bar.setDisplayUseLogoEnabled(true);

                    if (isSubpage)
                        bar.setDisplayHomeAsUpEnabled(true);
                }
            }
        }
        else {
            if (null != mainView && actionBarMenu.getToolbar() == null)
                actionBarMenu.setToolbar((Toolbar) mainView.findViewById(R.id.tyodroid_toolbar));

            return actionBarMenu.getToolbar();
        }

        return barObj;
    }

    /**
     *
     */
    @Override
    public void hideProgressBar() {
        if (null != pageView)
            pageView.setVisibility(View.VISIBLE);
        if (null != pageProgressView)
            pageProgressView.setVisibility(View.GONE);
    }

    public void showSuggestionView() {
        setSuggestionViewVisibility(true);
    }

    /**
     *
     */
    @Override
    public void hideSuggestionView() {
        searchView.requestFocusForSearchButton();
        setSuggestionViewVisibility(false);
    }

    /**
     *
     * @return
     */
    @Override
    public boolean onBackPressed() {
        if (isSubpage) {
            if (requestCode > -1)
                activity.finishActivity(requestCode);
            else
                activity.finish();
            return true;
        }
        return false;
    }

    /**
     *
     */
    @Override
    public void showProgressBar() {
        showProgressBar("");
    }

    public void showProgressBar(String info) {
//        if (null != pageView)
//            pageView.setVisibility(View.GONE);
        if (null != pageProgressView && pageProgressView.getVisibility() != View.VISIBLE)
            pageProgressView.setVisibility(View.VISIBLE);
        if (null != textViewProgressInfo)
            textViewProgressInfo.setText(info);
    }

    /**
     *
     */
    @Override
    public void onNetworkDisonnected() {
//		footerView.setVisibility(View.GONE);
        hideAd();
    }

    /**
     *
     */
    @Override
    public void onNetworkConnected() {
//		footerView.setVisibility(View.VISIBLE);
        showAd();
    }

    /**
     *
     * @param cls
     */
    @Override
    public void startActivity(Class cls) {
        startActivity(cls, Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP, Constants.DATA, null, null, -1);
    }

    /**
     *
     * @param extra
     */
    @Override
    public void startActivity(CommonExtra extra) {
        Class cls = extra.getActivityClass();
        int flags = extra.getFlags();
        String key = extra.getKey();
        Object data = extra.getParcel();
        View view = extra.getFromView();
        int requestCode= extra.getRequestCode();
        Intent intent = extra.getIntent();
        if (null == intent)
            startActivity(cls, flags, key, data, view, requestCode);
        else {
            if (null != data)
                intent.putExtra(Constants.DATA, (Parcelable) data);
            startActivity(activity, intent, view, requestCode);
        }
    }

    @Override
    public void startActivity(Class cls, int flags, String key, Object data, View view, int requestCode) {
        startActivity(activity, cls, flags, key, data, view, requestCode);
    }

    /**
     * Start a activity with data
     *
     * @param cls
     * @param flags
     * @param data, should be String or Parcelable
     * @param view
     * @param requestCode
     */
    public static void startActivity(Activity context, Class cls, int flags, String key, Object data, View view, int requestCode) {
        Intent intent = new Intent(context, cls);
        intent.addFlags(flags);

        CommonExtra.putExtra(intent, key, data);

        startActivity(context, intent, view, requestCode);
    }

    /**
     *
     * @param intent
     * @param view
     * @param requestCode
     */
    public static void startActivity(Activity context, Intent intent, View view, int requestCode) {
        Bundle options = null;

        if (null != view)
            options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    context,
                    view,
                    Constants.BUNDLE).toBundle();

        if (null != options && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (requestCode > -1)
                context.startActivityForResult(intent, requestCode, options);
            else
                context.startActivity(intent, options);
        }
        else {
            if (requestCode > -1)
                context.startActivityForResult(intent, requestCode);
            else
                context.startActivity(intent);
        }
    }

    /**
     * Bind data from intent
     *
     * @param intent
     */
    @Override
    public void bindData(Intent intent) {
        if (intent.hasExtra(Constants.PAGE_TOOLBAR_COLOR)) {
            int value = intent.getIntExtra(Constants.PAGE_TOOLBAR_COLOR, -1);
            if (value != -1)
                toolbarColor = value;
        }
        if (intent.hasExtra(Constants.PAGE_STATUSBAR_COLOR)) {
            int value = intent.getIntExtra(Constants.PAGE_STATUSBAR_COLOR, -1);
            if (value != -1)
                statusBarColor = value;
        }
        if (intent.hasExtra(Constants.PAGE_TITLE))
            setTitle(intent.getStringExtra(Constants.PAGE_TITLE));
    }

    /**
     * Bind data from controller
     */
    @Override
    public void bindData() {
        if (getController().getParcel() instanceof Map) {
            Map map = (Map) getController().getParcel();
            if (map.containsKey(Constants.PAGE_TOOLBAR_COLOR))
                toolbarColor = (int) map.get(Constants.PAGE_TOOLBAR_COLOR);
            if (map.containsKey(Constants.PAGE_STATUSBAR_COLOR))
                statusBarColor = (int) map.get(Constants.PAGE_STATUSBAR_COLOR);
            if (map.containsKey(Constants.PAGE_TITLE))
                setTitle((String) map.get(Constants.PAGE_TITLE));
        }
    }

    /**
     *
     * @param id
     * @return
     */
    public View findViewById(int id) {
        if (getContentView() != null)
            return getContentView().findViewById(id);
        return null;
    }

    @Override
    public void onFinish() {
        if (null != result) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(Constants.RESULT, (Parcelable) result);
            activity.setResult(Activity.RESULT_OK, resultIntent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    @Override
    public void addFragmentToList(Fragment fragment) {
        if (fragments == null)
            fragments = new ArrayList<>();
        fragments.add(fragment);
    }

    public void replaceFragment(Fragment fragment) {
        replaceFragment(getContentViewResId(), fragment);
    }

    public void replaceFragment(int fragmentContainerResId, Fragment fragment) {
        ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction()
                .replace(fragmentContainerResId, fragment).commit();
    }

    public void addFragmentToContainer(Fragment fragment) {
        addFragmentToContainer(getContentViewResId(), fragment);
    }

    public void addFragmentToContainer(int fragmentContainerResId, Fragment fragment) {
        ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction()
                .add(fragmentContainerResId, fragment).commit();
    }

    public void showFragment(Fragment fragment) {
        ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction()
                .show(fragment).commit();
    }

    public void hideFragment(Fragment fragment) {
        ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction()
                .hide(fragment).commit();
    }

    public Fragment getFragment(int index) {
        if (index < 0 || index > fragments.size())
            return null;
        return fragments.get(index);
    }

    public int getFragmentCount() {
        return null == fragments ? 0 : fragments.size();
    }

    @Override
    public void setPageTitleOnToolbar(String title) {
        if (null != actionBarMenu)
            actionBarMenu.getSupportActionBar().setTitle(title);
    }

    @Override
    public void onActivityStart() {
        // do nothing
        if (null != title)
            setPageTitleOnToolbar(title);

        if (statusBarColor != null)
            setPageStatusBarColor(statusBarColor);

        if (toolbarColor != null)
            setPageToolbarColor(toolbarColor);

        if (bodyViewColor != null)
            bodyView.setBackgroundColor(bodyViewColor);

        if (titleTextColor != null)
            setPageToolbarTitleColor(titleTextColor);
    }

    public FragmentManager getSupportFragmentManager() {
        return ((AppCompatActivity) activity).getSupportFragmentManager();
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null != fragments) {
            for (Fragment fragment : fragments)
                fragment.onActivityResult(requestCode, resultCode, data);

            return true; // by default page will handle the activity result
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(MenuInflater menuInflater, Menu menu) {
        return onMenuCreated(menu);
    }

    protected void createMenuItemAbout(MenuInflater menuInflater, Menu menu) {
        menuInflater.inflate(au.com.tyo.android.R.menu.common_menu, menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Object actionBar, Menu menu) {
        getActionBarMenu().initializeMenuForActionBar(actionBar, menu);
        prepareMenu();
        return false;
    }

    /**
     *
     */
    protected void prepareMenu() {
        // no ops
    }

    protected boolean onMenuCreated(Menu menu) {
        // by default no menu created
        getActionBarMenu().setupMenu(menu);
        return true;
    }

    public int getActionBarHeight() {
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getActivity().getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }

    /**
     * Event for notifying UI instance is created
     */
    @Override
    public void onUiCreated() {
        // no ops
    }

    public void setPageToolbarColor(int color) {
        getActionBarMenu().getToolbar().setBackgroundColor(color);
    }

    public void setPageStatusBarColor(int color) {
        AndroidUtils.setStatusBarColor(activity, color);
    }

    public void setPageToolbarTitleColor(int color) {
        getActionBarMenu().getToolbar().setTitleTextColor(color);
    }

    @Override
    public void onPostCreate() {
        if (getContentViewResId() <= 0)
            setContentViewResId(R.layout.content);
    }

    public String getString(int resId) {
        return activity.getResources().getString(resId);
    }

    public Drawable getDrawable(int resId) {
        return ContextCompat.getDrawable(activity, resId);
    }

    public int getColor(int resId) {
        return ContextCompat.getColor(activity, resId);
    }

    public int getInteger(int resId) {
        return activity.getResources().getInteger(resId);
    }

    public Resources getResources() {
        return activity.getResources();
    }

    public void finish() {
        activity.finish();
    }

    @Override
    public void onStart() {
        //
    }
}
