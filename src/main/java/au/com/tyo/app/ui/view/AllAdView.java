/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD.
 *
 */

package au.com.tyo.app.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import au.com.tyo.app.Controller;
import au.com.tyo.app.R;

/**
 * @author Eric Tang <eric.tang@tyo.com.au>
 *
 * AdMob (Google Mobile Ads) banner view. The former Amazon Mobile Ads path was
 * removed because Amazon discontinued its Mobile Ads SDK in 2022.
 */
public class AllAdView extends FrameLayout {

	public static final int AD_STATE_NONE = 0;
	public static final int AD_STATE_LOADING = 99;
	public static final int AD_STATE_LOADED = 1;
	public static final int AD_STATE_FAILED = -1;

    private static final String LOG_TAG = "AllAdView";

	private AdView admobAdBanner;

	private View banner;

	private Controller controller;

	private ViewGroup parent;

	private int state;

	private boolean adSdkInitialized;

	public AllAdView(Context context) {
		super(context);
		init();
	}

	public AllAdView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public AllAdView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public AllAdView(Context context, AttributeSet attrs, int defStyleAttr,
			int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init();
	}

    private void init() {
    	banner = null;
    	state = AD_STATE_NONE;
	}

	public boolean isAdSdkInitialized() {
		return adSdkInitialized;
	}

	public void setAdSdkInitialized(boolean adSdkInitialized) {
		this.adSdkInitialized = adSdkInitialized;
	}

	/** Retained for source compatibility. Amazon Mobile Ads was discontinued; this is a no-op. */
	public static void setIsAmazonAd(boolean isAmazonAd) {
	}

	public void loadBannerAd() {
		if (banner != null)
			this.removeView(banner);

		if (isAdSdkInitialized()) {
			 MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
				 @Override
				 public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
				 }
			 });
		}

		initializeAd();

		loadAd();

		hide();

		state = AD_STATE_LOADING;
	}

	private void loadAd() {
		 admobAdBanner.loadAd(new AdRequest.Builder().build());
	}

	public void show() {
		if (state != AD_STATE_LOADED)
			return;

		if (null != parent) {
			if (null == controller.getNetworkMonitor() || controller.getNetworkMonitor().hasInternet())
				parent.setVisibility(View.VISIBLE);
		}
	}

	public void hide() {
		if (null != parent)
			parent.setVisibility(View.GONE);
	}

	public void initialize(Controller controller, ViewGroup parent) {
    	this.controller = controller;
    	this.parent = null == parent ? this : parent;
	}

	private void initializeAd() {
		initializeAdmobBanner();
    	this.addView(banner);
	}

	public void initializeAdmobBanner() {
		 admobAdBanner = (AdView) LayoutInflater.from(this.getContext()).inflate(R.layout.admob, null);
		 admobAdBanner.setAdListener(new AdmobAdListener());
		 banner = admobAdBanner;
	}

     public class AdmobAdListener extends com.google.android.gms.ads.AdListener {

	 	@Override
	 	public void onAdLoaded() {
	 		super.onAdLoaded();

	 		state = AD_STATE_LOADED;
	 		controller.getUi().getCurrentPage().onAdLoaded();
	 	}

		@Override
		public void onAdFailedToLoad(@NonNull com.google.android.gms.ads.LoadAdError error) {
			super.onAdFailedToLoad(error);

			state = AD_STATE_FAILED;
			android.util.Log.w(LOG_TAG, "onAdFailedToLoad: code=" + error.getCode() + " msg=" + error.getMessage());
		}

     }
}
