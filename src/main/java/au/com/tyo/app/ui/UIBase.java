/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD.
 *
 */

package au.com.tyo.app.ui;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import au.com.tyo.android.AndroidUtils;
import au.com.tyo.app.Controller;
import au.com.tyo.app.R;

public class UIBase implements UI {
	
	private static final String LOG_TAG = "GUI";

	protected View mainView;

	protected ViewGroup footerView;
	
	protected ViewGroup headerView;
	
	protected AllAdView ad;
	
	private Controller controller;
	
	private SuggestionView suggestionView;
	
	private SearchView searchView;
	
	private ViewGroup contentView;
	
	private ViewGroup bodyView;
	
	private boolean alwaysShowSearchView;
	
	protected Context context;
	
	protected boolean hideActionBar = false;

	protected boolean uiRecreationRequierd = false;

	protected int mainUiResId = -1;

	private View splashScreenOverlayView;

	public UIBase(Controller controller) {
		this.controller = controller;
		alwaysShowSearchView = true;
	}

	public int getMainUiResId () {
		return  mainUiResId;
	}

	@Override
	public boolean recreationRequried() {
		return uiRecreationRequierd;
	}

	@Override
	public void setUiRecreationRequierd(boolean value) {
		uiRecreationRequierd = value;
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
	public void setSplashScreenOverlayView(View viewOverlay) {
		this.splashScreenOverlayView = viewOverlay;
	}

	@Override
	public void setupStartupAdView(View viewOverlay, Activity splashScreen) {

	}

	@Override
	public void setMainView(View mainView) {
		this.mainView = mainView;
	}

	/**
	 * When the window is create, all layout / elements / components are inflated
	 *
	 */
	@Override
	public void onWidowReady() {
		initialiseComponents();
	}

	@Override
	public void initialiseComponents() {
	}

	public View getSplashScreenOverlayView() {
		return splashScreenOverlayView;
	}

	@Override
	public void assignMainUiContainer(FrameLayout frameLayout) {
		if (mainView.getParent() != null)
			((ViewGroup) mainView.getParent()).removeView(mainView);
		mainView.setVisibility(View.VISIBLE);
        frameLayout.addView(mainView);
	}
	
	public Controller getController() {
		return controller;
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}

	@Override
	public void initializeUi(View v) {
		mainView = v;
		initializeUi(v.getContext());
	}

	@Override
	public void initializeUi(Context context) {
		if (null == mainView) {
			if (mainUiResId == -1) {
				mainUiResId = R.layout.activity_common;
			}
			mainView = LayoutInflater.from(context).inflate(mainUiResId, null);
		}
        this.context = context;

        setupComponents();

        if (!alwaysShowSearchView)
            hideSearchView();
	}

	private void hideSearchView() {
		if (null != searchView)
			searchView.setVisibility(View.GONE);
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
         */
        if (mainUiResId == R.layout.activity_common) {
            bodyView = (ViewGroup) mainView.findViewById(R.id.body_view);

            footerView = (ViewGroup) mainView.findViewById(R.id.footer_view);
            headerView = (ViewGroup) mainView.findViewById(R.id.header_view);

            if (hasSearchBar())
                setupSearchView();

            ad = (AllAdView) mainView.findViewById(R.id.all_ad_view);
            if (null != ad) {
                addAdView();

                if (controller.getSettings().hasAd())
                    ad.loadBannerAd();
            }

            if (!controller.getNetworkMonitor().hasInternet())
                onNetworkDisonnected();
        }
		
	}
	
	private boolean hasSearchBar() {
		return null != mainView.findViewById(R.id.search_nav_bar);
	}
	
	/* 
	 * not all the need search function 
	 * keep these line for future use
	 */
	public void setupSearchView() {
		contentView = (ViewGroup) mainView.findViewById(R.id.content_view);
		
		searchView = (SearchView) mainView.findViewById(R.id.search_nav_bar);
	    searchView.setupComponents(controller);
		
		suggestionView = (SuggestionView) mainView.findViewById(R.id.suggestion_view);
		suggestionView.setupComponents(controller);
	}
	
	public void showFooterView() {
		footerView.setVisibility(View.VISIBLE);
	}
	
	public void hideFooterView() {
		footerView.setVisibility(View.GONE);
	}

	public ViewGroup getFooterView() {
		return footerView;
	}
	
	public void showHeaderView() {
		headerView.setVisibility(View.VISIBLE);
	}
	
	public void hideHeaderView() {
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

	public ViewGroup getBodyView() {
		return bodyView;
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

	@Override
	public void onNetworkDisonnected() {
//		footerView.setVisibility(View.GONE);
		hideAd();
	}

	@Override
	public void onNetworkConnected() {
//		footerView.setVisibility(View.VISIBLE);
		showAd();
	}

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
	public void setupActionBar(Object barObj) {
		
		if (barObj != null) {
			if (barObj instanceof android.app.ActionBar) {
				android.app.ActionBar bar = (ActionBar) barObj;
				if (hideActionBar) {
					bar.hide();
				}
				else {
					
					if (AndroidUtils.getAndroidVersion() >= 14) {
						bar.setLogo(R.drawable.ic_logo);
					}
					
					if (AndroidUtils.getAndroidVersion() >= 11) {
						bar.setDisplayUseLogoEnabled(true);
					    bar.setDisplayShowHomeEnabled(true);
					}
					
				    bar.setDisplayShowTitleEnabled(false);
				}
			}
			else if (barObj instanceof android.support.v7.app.ActionBar) {
				android.support.v7.app.ActionBar bar = (android.support.v7.app.ActionBar) barObj;
				
				if (hideActionBar) {
					bar.hide();
				}
				else {
			        bar.setLogo(R.drawable.ic_logo);
			//      bar.setIcon(R.drawable.ic_launcher);
			        bar.setDisplayUseLogoEnabled(true);
			//      bar.setHomeButtonEnabled(true);
			//      bar.setDisplayHomeAsUpEnabled(true);
			//      bar.setDisplayShowCustomEnabled(true);
			        bar.setDisplayShowHomeEnabled(true);
			        bar.setDisplayShowTitleEnabled(false);
				}
			}
		}
	}

	@Override
	public void hideMainProgressBar() {
	}

	@Override
	public void hideSuggestionView() {
		searchView.requestFocusForSearchButton();
		setSuggestionViewVisibility(false);
	}
}
