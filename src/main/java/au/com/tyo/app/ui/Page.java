package au.com.tyo.app.ui;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

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
 * === HEADER ===
 *
 *  <Body>
 *
 *   Content
 *
 *   </Body>
 *
 * === FOOTER ===
 *
 */

public class Page implements UIPage, MenuItem.OnMenuItemClickListener {

    private static final String LOG_TAG = "Screen";

    /**
     * Common Widgets
     */
    protected ViewContainerWithProgressBar mainViewContainer;

    protected View pageView;

    protected View pageProgressView;

    protected ViewGroup footerView;

    protected ViewGroup headerView;

    protected AllAdView ad;

    private SuggestionView suggestionView;

    private SearchView searchView;

    private ViewGroup contentView;

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
     * The default content view layout resource id
     */
    private int contentViewResId = -1;

    /**
     * The indicator for whether the page is child page
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

    public int getContentViewResId() {
        return contentViewResId;
    }

    @Override
    public void setContentViewResId(int contentViewResId) {
        this.contentViewResId = contentViewResId;
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
            contentView.setVisibility(View.GONE);
        }
        else {
            ad.show();
            contentView.setVisibility(View.VISIBLE);
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

        /**
         * the root view of body.xml
         */
        bodyView = (BodyView) mainView.findViewById(R.id.body_view);

        /**
         * the root view of content.xml
         */
        contentView = (ViewGroup) mainView.findViewById(R.id.content_view);
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

    protected void loadContentView() {
        if (contentViewResId > -1) {
            contentView.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(activity);
            inflater.inflate(contentViewResId, contentView, true);
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
                    controller.getUi().getCurrentScreen().hideSuggestionView();
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

    public ViewGroup getContentView() {
        return contentView;
    }

    public void setContentView(ViewGroup contentView) {
        this.contentView = contentView;
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
        if (null != pageView)
            pageView.setVisibility(View.GONE);
        if (null != pageProgressView)
            pageProgressView.setVisibility(View.VISIBLE);
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
        startActivity(extra.getActivityClass(), extra.getFlags(), extra.getKey(), extra.getParcel(), extra.getFromView(), extra.getRequestCode());
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

        Bundle options = null;

        if (data != null) {
            if (data instanceof String) {
                String value = (String) data;
                intent.putExtra(key, value);
            }
            else if (data instanceof Boolean) {
                Boolean value = (Boolean) data;
                intent.putExtra(key, value);
            }
            else if (data instanceof Integer) {
                Integer value = (Integer) data;
                intent.putExtra(key, value);
            }
            else if (data instanceof Long) {
                Long value = (Long) data;
                intent.putExtra(key, value);
            }
            else if (data instanceof Parcelable){
                Parcelable value = (Parcelable) data;
                intent.putExtra(key, value);
            }
            else if (data instanceof Bundle) {
                Bundle bundle = (Bundle) data;
                intent.putExtra(key, bundle);
            }
            else if (data instanceof Object[]) {
                Object[] array = (Object[]) data; // ((List) data).toArray();
                intent.putExtra(key, array);
            }
            else if (data instanceof ArrayList) {
                ArrayList list = (ArrayList) data;
                intent.putExtra(key, list);
            }
            else {
                // noting
                throw new IllegalArgumentException("Unsupported data type: " + data.getClass().getSimpleName());
            }
        }

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
        // do nothing yet, as we don't know what data we need to process
    }

    /**
     * Bind data from controller
     */
    @Override
    public void bindData() {
        // do nothing
    }

    public View findViewById(int id) {
        if (contentView != null)
            return contentView.findViewById(id);
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
        replaceFragment(contentViewResId, fragment);
    }

    public void replaceFragment(int fragmentContainerResId, Fragment fragment) {
        ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction()
                .replace(fragmentContainerResId, fragment).commit();
    }

    public void addFragmentToContainer(Fragment fragment) {
        addFragmentToContainer(contentViewResId, fragment);
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

    public void setTitle(String title) {
        if (null != actionBarMenu)
            actionBarMenu.getSupportActionBar().setTitle(title);
    }

    @Override
    public void onActivityStart() {
        // do nothing
    }

    public FragmentManager getSupportFragmentManager() {
        return ((AppCompatActivity) activity).getSupportFragmentManager();
    }

    @Override
    public boolean onActivityResult(int requestCode, int requestCode1, Intent data) {
        if (null != fragments) {
            for (Fragment fragment : fragments)
                fragment.onActivityResult(requestCode, requestCode1, data);

            return true; // by default page will handle the activity result
        }
        return false;
    }
}