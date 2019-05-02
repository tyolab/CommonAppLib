package au.com.tyo.app.ui.page;

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
import android.os.Handler;
import android.os.Parcelable;
import android.os.PersistableBundle;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import au.com.tyo.android.AndroidHelper;
import au.com.tyo.android.AndroidUtils;
import au.com.tyo.android.CommonInitializer;
import au.com.tyo.android.CommonPermission;
import au.com.tyo.android.utils.ActivityUtils;
import au.com.tyo.app.CommonAppLog;
import au.com.tyo.app.CommonExtra;
import au.com.tyo.app.CommonLog;
import au.com.tyo.app.Constants;
import au.com.tyo.app.Controller;
import au.com.tyo.app.R;
import au.com.tyo.app.model.Searchable;
import au.com.tyo.app.ui.ActionBarMenu;
import au.com.tyo.app.ui.UIPage;
import au.com.tyo.app.ui.view.AllAdView;
import au.com.tyo.app.ui.view.BodyView;
import au.com.tyo.app.ui.view.InformationView;
import au.com.tyo.app.ui.view.SearchInputView;
import au.com.tyo.app.ui.view.SearchView;
import au.com.tyo.app.ui.view.SuggestionView;
import au.com.tyo.app.ui.view.ViewContainerWithProgressBar;

import static au.com.tyo.app.Constants.REQUEST_NONE;

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

public class Page<ControllerType extends Controller> extends PageFragment implements UIPage, MenuItem.OnMenuItemClickListener, Runnable, SearchInputView.SearchInputFocusWatcher  {

    private static final String LOG_TAG = "Page";

    /**
     * Common Widgets
     */
    protected ViewContainerWithProgressBar mainViewContainer;

    /**
     * Page View
     */
    protected View pageView;

    /**
     * Page Progress View
     */
    protected View pageProgressView;
    protected TextView textViewProgressInfo;

    /**
     * Page Overlay
     */
    private View pageOverlay;

    //############  WITHIN PAGE  ##############
    protected ViewGroup footerView;

    protected ViewGroup headerView;

    protected AllAdView ad;

    private SuggestionView suggestionView;

    private SearchView searchView;

    private BodyView bodyView;

    private boolean toShowSearchView;

    protected int mainUiResId = -1;

    private InformationView informationView;

    private ActionBarMenu actionBarMenu;

    /**
     * The the main / content view from the screen
     */
    protected View mainView;

    /**
     * Toolbar container
     */
    protected View toolbarContainer;

    /**
     * Controller
     */
    private ControllerType controller;

    /**
     * The associated activity
     */
    private Activity activity;

    /**
     * The indicator for whether the page is a child page
     */
    private boolean isSubpage;

    /**
     * The request code
     */
    private int requestCode = -1;

    /**
     * The result that needs to be returned to previous page
     */
    private Object result;

    /**
     * The result code
     */
    private int resultCode;

    /**
     * The result key
     */
    private String resultKey;

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
     * Back to exit method
     */
    private boolean doubleBackToExit;
    private int backKeyCount;

    /**
     * Required Permissions
     */
    private String[] requiredPermissions;

    /**
     * Page Initializer for pages that share the same attributes
     * it is for all pages in an App
     */
    private PageInitializer pageInitializer;

    ////////////////////////////////////////////////////////////

    protected boolean hideActionBar = false;

    protected boolean showTitleInToolbar = false;

    private boolean keepShowingSuggestionView;
    private Drawable upArrow;

    /**
     *
     * @param controller
     * @param activity
     */
    public Page(ControllerType controller, Activity activity) {
        super(null);

        this.activity = activity;
        this.controller = controller;
        toShowSearchView = false;
        keepShowingSuggestionView = false;
        upArrow = null;

        /**
         * can be set
         * showTitleInToolbar = controller.getContext().getResources().getBoolean(R.bool.showTitleOnActionBar)
         */
        showTitleInToolbar = true;
        doubleBackToExit = true;
        setSubpage(true);
        setResult(null);
        setResultKey(Constants.RESULT);

        configActionBarMenu();

        pageInitializer = PageInitializer.getInstance();

        if (pageInitializer != null)
            pageInitializer.initializePageOnConstruct(this);
    }

    public boolean isShowTitleInToolbar() {
        return showTitleInToolbar;
    }

    public void setShowTitleInToolbar(boolean showTitleInToolbar) {
        this.showTitleInToolbar = showTitleInToolbar;
    }

    public String[] getRequiredPermissions() {
        return requiredPermissions;
    }

    public void setRequiredPermissions(String[] requiredPermissions) {
        this.requiredPermissions = requiredPermissions;
    }

    @Override
    public void setStatusBarColor(Integer statusBarColor) {
        this.statusBarColor = statusBarColor;
    }

    @Override
    public void setToolbarColor(Integer toolbarColor) {
        this.toolbarColor = toolbarColor;
    }

    public void setTitleTextColor(Integer titleTextColor) {
        this.titleTextColor = titleTextColor;
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

    @Override
    public Object getResult() {
        return result;
    }

    @Override
    public void setResult(Object result) {
        setResult(result, Activity.RESULT_OK);
    }

    public void setResult(Object result, int i) {
        this.result = result;
        this.resultCode = i;
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

    public String getResultKey() {
        return resultKey;
    }

    public void setResultKey(String resultKey) {
        this.resultKey = resultKey;
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
     * Override this method if need a customer action menu bar
     */
    protected void configActionBarMenu() {
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
    public void onPause() {
        super.onPause();
        // should be overrode if needed
    }

    @Override
    public void onResume() {
        super.onResume();
        // should be overrode if needed
    }

    @Override
    public void onStop() {
        super.onStop();
        // should be overrode if needed
        task = null;

        // Page becomes invisible to user
        // But setting to null will cause problems for the operations that needs context
        getController().getUi().setContextPage(null);
    }

    @Override
    public void initialiseComponents() {
        if (null != bodyView) {
            bodyView.detectScreenLocation();

            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            int offset = dm.heightPixels - bodyView.getMeasuredHeight();
            bodyView.setScreenOffset(offset);
        }
    }

    public ControllerType getController() {
        return controller;
    }

    public void setController(Controller controller) {
        this.controller = (ControllerType) controller;
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
        loadAd();
    }

    @OverridingMethodsMustInvokeSuper
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
         * Page related
         */
        pageView = mainView.findViewById(R.id.tyodroid_page);
        pageProgressView = mainView.findViewById(R.id.tyodroid_page_progress_bar);
        textViewProgressInfo = (TextView) mainView.findViewById(R.id.tv_progress_info);
        pageOverlay = mainView.findViewById(R.id.tyodroid_page_overlay);

        /**
         * the root view of body.xml
         */
        bodyView = (BodyView) mainView.findViewById(R.id.body_view);

        /**
         * the root view of content.xml
         */
        setContentView((ViewGroup) bodyView.findViewById(R.id.content_view));
        loadContentView();

        footerView = (ViewGroup) mainView.findViewById(R.id.footer_view);

        setupHeader();
        setupSearchBar();

        if (null != mainView) {
            ad = (AllAdView) mainView.findViewById(R.id.all_ad_view);
            if (null != ad) {
                addAdView();
                loadAd();
            }
        }

        if (!controller.getNetworkMonitor().hasInternet())
            onNetworkDisconnected();

        setupActionBarMenu();

        setupPageOverlay(pageOverlay);
    }

    private void loadAd() {
        if (null != ad && controller.getSettings().hasAd())
            ad.loadBannerAd();
    }

    protected void setupPageOverlay(View pageOverlay) {
        // no ops
    }

    protected void hidePageOverlay() {
        if (null != pageOverlay)
            pageOverlay.setVisibility(View.GONE);
    }

    protected void showPageOverlay() {
        if (null != pageOverlay)
            pageOverlay.setVisibility(View.VISIBLE);
    }

    protected boolean isPageOverlayVisible() {
        if (null != pageOverlay)
            return pageOverlay.getVisibility() == (View.VISIBLE);
        return false;
    }

    protected void loadContentView() {
        loadContentView(activity);
    }

    @OverridingMethodsMustInvokeSuper
    protected void setupToolbar() {
        toolbarContainer = mainView.findViewById(R.id.tyodroid_toolbar_container);
        Toolbar toolbar = (Toolbar) toolbarContainer.findViewById(R.id.tyodroid_toolbar);
        if (null == toolbar && toolbarContainer instanceof Toolbar)
            toolbar = (Toolbar) toolbarContainer;
        actionBarMenu.setToolbar(toolbar);
    }

    public Toolbar getToolbar() {
        return actionBarMenu.getToolbar();
    }

    public void hideToolbar() {
        actionBarMenu.hide();
    }

    public void showToolbar() {
        actionBarMenu.show();
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

        if (null != searchView) {
            searchView.setupComponents(controller);
            searchView.getSearchInputView().setSearchInputFocusWatcher(this);
        }

        suggestionView = (SuggestionView) mainView.findViewById(R.id.suggestion_view);
        if (null != suggestionView)
            suggestionView.setupComponents(controller);
        else
            Log.w(LOG_TAG, "Search bar is set, but suggestion view is not found");

        setOnSuggestionItemClickListener();
    }

    public void setOnSuggestionItemClickListener() {
        if (null != suggestionView) {
            suggestionView.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Object obj = suggestionView.getAdapter().getItem(position);

                    if (obj instanceof Searchable) {
                        Searchable item = (Searchable) obj;

                        if (item.isAvailable()) {
                            onSearchableItemClick(item);
                            hideSuggestionView();
                        }
                    }
                    else {
                        // same as parcelable
                        onSuggestionItemClick(obj);
                    }
                }
            });
        }
    }

    /**
     * If other type of item listed on the suggestion view
     *
     * @param obj
     */
    protected void onSuggestionItemClick(Object obj) {
        Log.i(LOG_TAG, "A suggestion item is clicked: " + obj.toString());

        // by default we just return to the obj to previous activity
        // override it to deal with it differently
        setResultAndFinish(obj);
    }

    protected void setResultAndFinish(Object obj) {
        result = obj;
        finish();
    }

    /**
     * if the item is implemented with Searchable interface
     *
     * @param item
     */
    protected void onSearchableItemClick(Searchable item) {
        controller.onOpenSearchItemClicked(item);
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
            // Toolbar toolbar = actionBarMenu.getToolbar();

            // if (null == toolbar) {
                setupToolbar();
             Toolbar toolbar = actionBarMenu.getToolbar();
            //}

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

                    bar.setDisplayShowTitleEnabled(showTitleInToolbar);
                }
            }
            else if (barObj instanceof androidx.appcompat.app.ActionBar) {
                androidx.appcompat.app.ActionBar bar = (androidx.appcompat.app.ActionBar) barObj;

                if (hideActionBar) {
                    bar.hide();
                }
                else {
                    boolean showTitle = showTitleInToolbar;

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

                    if (isSubpage())
                        bar.setDisplayHomeAsUpEnabled(true);
                }
            }
        }
        else {
            if (null != mainView && actionBarMenu.getToolbar() == null)
                setupToolbar();

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
        if (isPageOverlayVisible()) {
            hidePageOverlay();
            return true;
        }
        else if (isSubpage()) {
            finish();

            onPageFinishedByBackPressed();

            return true;
        }
        else {
            if (doubleBackToExit) {

                if (backKeyCount > 0) {
                    exitApp();
                    return true;
                }

                ++backKeyCount;
                Toast.makeText(getActivity(), "Press BACK again to exit", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        backKeyCount = 0;
                    }
                }, 3000);
                return true;
            }
        }
        return false;
    }

    protected void onPageFinishedByBackPressed() {
        if (controller.getUi().getMainPage() == null)
            controller.startMainActivity();
    }

    public void finish() {

        // we finish using the parcel if there is one
        // controller.setParcel(null);
        if (getActivity().getIntent().getBooleanExtra(Constants.DATA_LOCATION_CONTROLLER, false))
            controller.setParcel(null);

        checkIfFinishWithResult();
        getActivity().finish();
    }

    private void checkIfFinishWithResult() {
        if (null != result) {
            Intent resultIntent = new Intent();

            if (requestCode < 5000) {
                if (result instanceof Parcelable)
                    resultIntent.putExtra(resultKey, (Parcelable) result);
                else if (result instanceof Parcelable[])
                    resultIntent.putExtra(resultKey, (Parcelable[]) result);
                else if (result instanceof String)
                    resultIntent.putExtra(resultKey, (String) result);
                else if (result instanceof Integer)
                    resultIntent.putExtra(resultKey, (Integer) result);
                else if (result instanceof Boolean)
                    resultIntent.putExtra(resultKey, (Boolean) result);
                else if (result instanceof Long)
                    resultIntent.putExtra(resultKey, (Long) result);
                else {
                    resultIntent.putExtra(Constants.RESULT_LOCATION, Constants.RESULT_LOCATION_CONTROLLER);
                    controller.setResult(result);
                }
            }
            else {
                resultIntent.putExtra(Constants.RESULT_LOCATION, Constants.RESULT_LOCATION_CONTROLLER);
                controller.setResult(result);
            }
            activity.setResult(Activity.RESULT_OK, resultIntent);
        }
    }

    public void finishCompletely() {
        AndroidUtils.finishActivity(getActivity());
    }

    public void sendExitAppCommand() {
        if (null != CommonInitializer.mainActivityClass) {
            Intent intent = new Intent(getActivity(), CommonInitializer.mainActivityClass);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(Constants.EXIT_APP, true);
            getActivity().startActivity(intent);
        }

        finishCompletely();
    }

    public void exitApp() {
        finishCompletely();
    }

    public void sendReloadCommand() {
        if (null != CommonInitializer.mainActivityClass) {
            Intent intent = new Intent(getActivity(), CommonInitializer.mainActivityClass);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra(Constants.RELOAD, true);
            getActivity().startActivity(intent);
        }
    }

    /**
     *
     */
    @Override
    public void showProgressBar() {
        showProgressBar("");
    }

    public void showProgressBar(String info) {
        if (null != pageProgressView && pageProgressView.getVisibility() != View.VISIBLE)
            pageProgressView.setVisibility(View.VISIBLE);
        if (null != textViewProgressInfo)
            textViewProgressInfo.setText(info);
    }

    /**
     *
     */
    @Override
    public void onNetworkDisconnected() {
        hideAd();
    }

    /**
     *
     */
    @Override
    public void onNetworkConnected() {
        showAd();
    }

    /**
     *
     * @param cls
     */
    @Override
    public void startActivity(Class cls, boolean isMainPage) {
        startActivity(cls, -1, Constants.DATA, null, null, -1, isMainPage);
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
    public void startActivity(Intent intent) {
        startActivity(activity, intent, null, -1);
    }

    @Override
    public void startActivity(Class cls, int flags, String key, Object data, View view, int requestCode) {
        startActivity(activity, cls, flags, key, data, view, requestCode, false);
    }

    @Override
    public void startActivity(Class cls, int flags, String key, Object data, View view, int requestCode, boolean isMainActivity) {
        startActivity(activity, cls, flags, key, data, view, requestCode, isMainActivity);
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
    public static void startActivity(Activity context, Class cls, int flags, String key, Object data, View view, int requestCode, boolean isMainPage) {
        Intent intent = new Intent(context, cls);

        if (flags > -1)
            intent.addFlags(flags);

        if (null != data)
            CommonExtra.putExtra(intent, key != null ? key : Constants.DATA, data);

        if (isMainPage)
            CommonExtra.putExtra(intent, Constants.PAGE_IS_MAIN, true);

        startActivity(context, intent, view, requestCode);
    }

    /**
     *
     * @param context
     * @param intent
     */
    public static void startActivity(Activity context, Intent intent) {
        startActivity(context, intent, null, -1);
    }

    /**
     * @param context
     * @param intent
     * @param view
     * @param requestCode
     */
    public static void startActivity(Context context, Intent intent, View view, int requestCode) {
        Bundle options = null;
        Activity activity = null;

        if (context instanceof Activity)
            activity = (Activity) context;

        if (null != activity && null != view)
            options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    activity,
                    view,
                    Constants.BUNDLE).toBundle();

        if (requestCode != REQUEST_NONE)
            intent.putExtra(Constants.PAGE_REQUEST_CODE, requestCode);

        if (null != options && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (requestCode > REQUEST_NONE && null != activity)
                activity.startActivityForResult(intent, requestCode, options);
            else
                context.startActivity(intent, options);
        }
        else {
            if (requestCode > REQUEST_NONE && null != activity)
                activity.startActivityForResult(intent, requestCode);
            else
                context.startActivity(intent);
        }
    }

    @Override
    public void viewHtmlPageFromAsset(Class activityClass, String assetFile, String title, Integer statusBarColor) {
        CommonExtra extra = new CommonExtra(activityClass);

        if (null != statusBarColor)
            extra.setExtra(getActivity(), Constants.PAGE_STATUSBAR_COLOR, String.valueOf(statusBarColor));
        extra.setExtra(getActivity(), Constants.PAGE_TITLE, title);
        extra.setExtra(getActivity(), Constants.DATA_ASSETS_PATH, assetFile);

        startActivity(extra);
    }

    /**
     *
     * @param intent
     * @param key
     * @return
     */
    private Integer getColorFromIntent(Intent intent, String key) {
        if (intent.hasExtra(key)) {
            try {
                int value = Integer.parseInt(intent.getStringExtra(key));
                return value;
            }
            catch (Exception exception) {}
        }
        return null;
    }

    /**
     * TODO
     *
     * @param map
     * @param key
     * @return
     */
    private Integer getColorFromMap(Map map, String key) {
        if (map.containsKey(key))
            return (Integer) map.get(key);
        return null;
    }

    /**
     * Bind data from intent
     *
     * @param intent
     */
    @Override
    public void bindData(Intent intent) {
        if (checkAppCommands(intent))
            return;

        if (intent.hasExtra(Constants.PAGE_TOOLBAR_COLOR))
            toolbarColor = getColorFromIntent(intent, Constants.PAGE_TOOLBAR_COLOR);

        if (intent.hasExtra(Constants.PAGE_STATUSBAR_COLOR))
            statusBarColor = getColorFromIntent(intent, Constants.PAGE_STATUSBAR_COLOR);

        if (intent.hasExtra(Constants.PAGE_TITLE_FOREGROUND_COLOR))
            titleTextColor = getColorFromIntent(intent, Constants.PAGE_TITLE_FOREGROUND_COLOR);

        if (intent.hasExtra(Constants.PAGE_BACKGROUND_COLOR))
            bodyViewColor = getColorFromIntent(intent, Constants.PAGE_BACKGROUND_COLOR);

        if (intent.hasExtra(Constants.PAGE_TITLE_FOREGROUND_COLOR))
            titleTextColor = getColorFromIntent(intent, Constants.PAGE_TITLE_FOREGROUND_COLOR);

        if (intent.hasExtra(Constants.PAGE_TITLE))
            setTitle(intent.getStringExtra(Constants.PAGE_TITLE));

        if (intent.hasExtra(Constants.PAGE_REQUEST_CODE))
            setRequestCode(intent.getIntExtra(Constants.PAGE_REQUEST_CODE, REQUEST_NONE));

        if (intent.hasExtra(Constants.PAGE_IS_MAIN))
            setSubpage(!intent.getBooleanExtra(Constants.PAGE_IS_MAIN, false));

        if (intent.hasExtra(Constants.PAGE_RESULT_KEY))
            setResultKey(intent.getStringExtra(Constants.PAGE_RESULT_KEY));
    }

    /**
     *
     *
     * @param intent
     * @return boolean, stop execute the rest of the code if true
     */
    @Override
    public boolean checkAppCommands(Intent intent) {
        if (null != intent) {
            if (intent.hasExtra(Constants.EXIT_APP) &&
                    intent.getBooleanExtra(Constants.EXIT_APP, true) &&
                    (CommonInitializer.mainActivityClass == null ||
                            getActivity().getClass().getName().equals(CommonInitializer.mainActivityClass.getName()))) {
                exitApp();
                return true;
            }
            if (intent.hasExtra(Constants.RELOAD) &&
                    intent.getBooleanExtra(Constants.RELOAD, true) && null != controller.getUi()) {
                controller.getUi().setUiRecreationRequired(true);
                reload();
                return true;
            }
        }

        return false;
    }

    protected void reload() {
        // nothing yet
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
            if (map.containsKey(Constants.PAGE_BACKGROUND_COLOR))
                bodyViewColor = (Integer) map.get(Constants.PAGE_BACKGROUND_COLOR);
            if (map.containsKey(Constants.PAGE_TITLE_FOREGROUND_COLOR))
                titleTextColor = (Integer) map.get(Constants.PAGE_TITLE_FOREGROUND_COLOR);
            if (map.containsKey(Constants.PAGE_IS_MAIN))
                setSubpage(!(boolean) map.get(Constants.PAGE_IS_MAIN));
            setRequestCode(map.containsKey(Constants.PAGE_REQUEST_CODE) ? (Integer) map.get(Constants.PAGE_REQUEST_CODE) : REQUEST_NONE);
        }
    }

    /**
     *
     * @param id
     * @return
     */
    public final <T extends View> T findViewById(int id) {
        if (getContentView() != null)
            return getContentView().findViewById(id);
        return null;
    }

    /**
     * This function will be called when the activity finish function is called
     */
    @Override
    public void onFinish() {
        // no ops
        CommonLog.d(this, "Page Finished");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onHomeButtonClick();
            return true;
        }
        return false;
    }

    protected boolean onHomeButtonClick() {
        return onBackPressed();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    public void addFragment(Fragment fragment) {
        addFragmentToList(fragment);
    }

    public void removeFragments() {
        // the alternative way
//        while (fragmentManager.getBackStackEntryCount() > 0) {
//            fragmentManager.popBackStackImmediate();
//        }
        if (null != fragments && fragments.size() > 0) {
            FragmentTransaction fragmentTransaction = ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction();
            for (Fragment fragment : fragments) {
                fragmentTransaction.remove(fragment);
            }

            fragmentTransaction.commit();
            fragments.clear();
        }
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
        replaceFragment(fragmentContainerResId, fragment, null);
    }

    public void replaceFragment(int fragmentContainerResId, Fragment fragment, String tag) {
        ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction()
                .replace(fragmentContainerResId, fragment, tag).commit();
    }

    public void addFragmentToContainer(Fragment fragment) {
        addFragmentToContainer(R.id.content_view, fragment);
    }

    public void addFragmentToContainer(int fragmentContainerResId, Fragment fragment) {
        addFragmentToContainer(fragmentContainerResId, fragment, null);
    }

    public void addFragmentToContainer(int fragmentContainerResId, Fragment fragment, String tag) {
        ((FragmentActivity) activity).getSupportFragmentManager()
                .beginTransaction()
                .add(fragmentContainerResId, fragment, tag)
                .commit();
    }

    public void showFragment(Fragment fragment) {
        ((FragmentActivity) activity).getSupportFragmentManager()
                .beginTransaction()
                .show(fragment)
                .commit();
    }

    public void hideFragment(Fragment fragment) {
        ((FragmentActivity) activity).getSupportFragmentManager()
                .beginTransaction()
                .hide(fragment)
                .commit();
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

    @OverridingMethodsMustInvokeSuper
    @Override
    public void onActivityStart() {
        if (null != pageInitializer)
            pageInitializer.initializePageOnActivityStart(this);

        if (statusBarColor != null)
            setPageStatusBarColor(statusBarColor);

        if (toolbarColor != null)
            setPageToolbarColor(toolbarColor);

        if (bodyViewColor != null)
            bodyView.setBackgroundColor(bodyViewColor);

        if (null != title && title.length() > 0)
            setPageTitleOnToolbar(title);

        updatePageTitleTextColor();

        if (!isSubpage())
            getController().getUi().setMainPage(this);
    }

    protected void updatePageTitleTextColor() {
        if (titleTextColor != null) {
            setPageToolbarTitleColor(titleTextColor);

            if (null == upArrow) {
                if (AndroidUtils.getAndroidVersion() >= 21)
                    upArrow = VectorDrawableCompat.create(getActivity().getResources(), R.drawable.ic_arrow_back_black_24dp, getActivity().getTheme());
                else
                    upArrow = AppCompatResources.getDrawable(getActivity(), R.drawable.ic_arrow_back_black_24dp);
            }

            Drawable drawable = DrawableCompat.wrap(upArrow);
            DrawableCompat.setTint(drawable.mutate(), titleTextColor);
            // drawable.setColorFilter(getActivity().getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            getActionBarMenu().getSupportActionBar().setHomeAsUpIndicator(drawable);
        }
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
        createMenu(menuInflater, menu);
        return onMenuCreated(menu);
    }

    protected void createMenu(MenuInflater menuInflater, Menu menu) {
        // override this method and put the menu creation code in
    }

    protected void createMenuItemAbout(MenuInflater menuInflater, Menu menu) {
        menuInflater.inflate(R.menu.common_menu, menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Object actionBar, Menu menu) {
        getActionBarMenu().initializeMenuForActionBar(menu);
        prepareMenu(menu);
        return false;
    }

    /**
     *
     * @param menu
     */
    protected void prepareMenu(Menu menu) {
        // no ops
    }

    @OverridingMethodsMustInvokeSuper
    protected boolean onMenuCreated(Menu menu) {
        getActionBarMenu().setMenu(menu);

        // by default no menu created
        setupMenuItemOnClickListener();

        if (null != titleTextColor)
            getActionBarMenu().setMenuTextColor(titleTextColor);

        onMenuPostCreated();
        return true;
    }

    protected void setupMenuItemOnClickListener() {
        getActionBarMenu().setupMenuItemOnClickListener(this);
    }

    protected void onMenuPostCreated() {
        // do nothing
        // put the code for the menu touch up here, such as updating the title of the menu item(s)
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
    public void onPostCreate(Bundle savedInstanceState) {
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

    @Override
    public void onStart() {
        super.onStart();
        // check the permissions required for the app since Android 6
        checkPermissions();
    }

    protected void checkPermissions() {
        if (null != getRequiredPermissions()) {
            List<String> list = new ArrayList();

            for (String permission : getRequiredPermissions()) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (!CommonPermission.checkPermission(getActivity(), permission))
                        list.add(permission);
                    else
                        onRequestedPermissionsGranted(permission);
                }
                else
                    onRequestedPermissionsGranted(permission);
            }

            if (list.size() > 0) {
                String[] permissions = new String[list.size()];
                for (int i = 0; i < list.size(); ++i)
                    permissions[i] = list.get(i);

                CommonPermission.requestPermissions(getActivity(), permissions);
            }
        }
    }

    protected void requestPermissions() {
        // no ops
    }

    public boolean isDoubleBackToExit() {
        return doubleBackToExit;
    }

    public void setDoubleBackToExit(boolean doubleBackToExit) {
        this.doubleBackToExit = doubleBackToExit;
    }

    public int getBackKeyCount() {
        return backKeyCount;
    }

    public void setBackKeyCount(int backKeyCount) {
        this.backKeyCount = backKeyCount;
    }

    @Override
    public boolean onDestroy() {
        super.onDestroy();

        // keep it in the memory, it will gone once the whole memory gets recalled
        // if (!isSubpage())
        //     getController().getUi().setMainPage(null);

        return false;
    }

    @Override
    public void onRequestedPermissionsGranted(String permission) {
        controller.getSettings().grantPermission(permission);
    }

    @Override
    public void onRequestedPermissionsDenied(String permission) {
        // no ops
    }

    @Override
    public void saveState(Bundle savedInstanceState) {
        // no ops yet
    }

    @Override
    public void onDataBound() {
        //
    }

    public void setPageInFullScreenMode() {
        AndroidHelper.setFullScreenMode(getActivity());
    }

    public void hideHardwareButtons() {
        AndroidHelper.hideHardwareButtons(getActivity());
    }

    @Override
    public void handleIntent(Intent intent) {
        checkAppCommands(intent);
    }

    @Override
    public void onPreCreateCheckFailed() {
        // nothing
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return false;
    }

    //////////////////////////////////////////////////////////////////////
    //
    // Anythings related to the background async / runnable tasks
    // while user is waiting for the result
    //
    //////////////////////////////////////////////////////////////////////
    private ProgressTask task = null;

    public void startBackgroundTask() {
        startBackgroundTask(this);
    }

    /**
     *
     */
    public void startBackgroundTask(int id, Runnable runnable) {
        setResult(null);

        if (null != mainViewContainer)
            mainViewContainer.startTask(id, runnable);
        else {
            if (task == null) {
                task = new ProgressTask(id, runnable);
                task.execute();
            }
        }
    }

    /**
     *
     */
    public void startBackgroundTask(Runnable runnable) {
        startBackgroundTask(-1, runnable);
    }

    /**
     * Runnable implementation
     */
    @Override
    public void run() {
        // nothing yet
        // pass the result with controller if any
    }

    public class ProgressTask extends ViewContainerWithProgressBar.BackgroundTask implements ViewContainerWithProgressBar.Caller {

        private int id;

        public ProgressTask(int id, Runnable job) {
            super(job);

            this.id = id;

            setCaller(this);
        }

        @Override
        public void onPreExecute() {
            showProgressBar();

            onPrePageBackgroundTaskExecute();
        }

        @Override
        public void onPostExecute(Object o) {
            if (task == null)
                return;

            hideProgressBar();

            onPageBackgroundTaskFinished(id);

            task = null;
        }
    }

    protected void onPageBackgroundTaskFinished(int id) {
        hideProgressBar();
    }

    protected void onPrePageBackgroundTaskExecute() {
    }

    public void setHideActionBar(boolean hideActionBar) {
        this.hideActionBar = hideActionBar;
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // no ops yet
    }

    @Override
    public void onWidowReady() {
        // no ops yet
    }

    @Override
    public View getContentView() {
        return super.getContentView();
    }

    /**
     * If the result is in Controller, it has to be specified
     *
     * @param data
     * @return
     */
    public Object getActivityResult(Intent data) {
        Object result = null;
        if (null != data) {
            boolean resultInController = false;

            if (data.hasExtra(Constants.RESULT_LOCATION))
                resultInController = data.getStringExtra(Constants.RESULT_LOCATION).equals(Constants.RESULT_LOCATION_CONTROLLER);

            if (!resultInController)
                result = ActivityUtils.getActivityResult(data);
            else
                result = controller.getResult();
        }
        return result;
    }

    public void setKeepShowingSuggestionViewEvenLosingFocus(
            boolean keepShowingSuggestionView) {
        this.keepShowingSuggestionView = keepShowingSuggestionView;
    }

    @Override
    public void onSearchInputFocused() {
        // Override me to do your things
        setSuggestionViewVisibility(true);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        hideAd();
    }

    @Override
    public void onSearchInputFocusEscaped() {
        if (!this.keepShowingSuggestionView)
            setSuggestionViewVisibility(false);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // no ops
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        // no pos
    }
}
