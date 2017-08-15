package au.com.tyo.app.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import au.com.tyo.app.CommonAppCompatActivity;
import au.com.tyo.app.CommonExtra;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 17/5/17.
 */

public interface UIPage extends UIEntity {

    boolean isSubpage();

    void setSubpage(boolean subpage);

    boolean isToShowSearchView();

    void setToShowSearchView(boolean toShowSearchView);

    void setMainView(View mainView);

    void assignMainUiContainer(FrameLayout frameLayout);

    void setContentViewResId(int contentViewResId);

    void onPause(Context context);

    void onResume(Context context);

    void onStop(Context currentActivity);

    void initializeUi(View v);

    void initializeUi();

    void hideSearchBar();

    void showSearchBar();

    SearchInputView getSearchInputView();

    void setSuggestionViewVisibility(boolean b);

    SuggestionView getSuggestionView();

    void onSearchInputFocusStatus(boolean focused);

    void onConfigurationChanged(Configuration newConfig);

    void setupComponents();

    ViewGroup getBodyView();

    void hideAd();

    void showAd();

    View getMainView();

    void onAdLoaded();

    void hideProgressBar();

    void hideSuggestionView();

    boolean onBackPressed();

    void showProgressBar();

    void initialiseComponents();

    void onNetworkDisonnected();

    void onNetworkConnected();

    ActionBarMenu getActionBarMenu();

    void showHeader();

    void hideHeader();

    View getContentView();

    void startActivity(Class cls);

    void startActivity(CommonExtra extra);

    void startActivity(Class cls, int flags, String key, Object data, View view, int requestCode);

    int getContentViewResId();

    void bindData(Intent intent);

    void onFinish();

    void bindData();

    boolean onOptionsItemSelected(MenuItem item);

    void setResult(Object data);

    void addFragmentToList(Fragment fragment);

    Object setupActionBar();

    boolean onActivityResult(int requestCode, int requestCode1, Intent data);
}
