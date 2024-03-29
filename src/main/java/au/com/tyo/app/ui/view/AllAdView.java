/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD.
 *
 */

package au.com.tyo.app.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.amazon.device.ads.AdError;
import com.amazon.device.ads.AdLayout;
import com.amazon.device.ads.AdProperties;
import com.amazon.device.ads.AdRegistration;
import com.amazon.device.ads.DefaultAdListener;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import au.com.tyo.android.AndroidMarket;
import au.com.tyo.app.Controller;
import au.com.tyo.app.R;

/**
 * @author Eric Tang <eric.tang@tyo.com.au>
 */


public class AllAdView extends FrameLayout {

	public static final int AD_STATE_NONE = 0;
	public static final int AD_STATE_LOADING = 99;
	public static final int AD_STATE_LOADED = 1;
	public static final int AD_STATE_FAILED = -1;
	
    private static final String LOG_TAG = "AllAdView";
	
	 private AdView admobAdBanner;
	
	private AdLayout amazonAdBanner;
	
	private static boolean isAmazonAd;
	
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

	public static void setIsAmazonAd(boolean isAmazonAd) {
		AllAdView.isAmazonAd = isAmazonAd;
	}

	public void loadBannerAd() {
		if (banner != null)	 
			this.removeView(banner);

		if (isAdSdkInitialized()) {
			if (!isAmazonAd) {
				// TODO
				// create a Ad listener
				 MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {

					 @Override
					 public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

					 }
				 });
			}
		}
		
		initializeAd();

		loadAd();

		/**
		 * Google Ad has a listner
		 */
		if (!isAmazonAd)
			hide();

		state = AD_STATE_LOADING;
	}

	private void loadAd() {
		if (isAmazonAd) {
			amazonAdBanner.loadAd();
		}
		else {
			// TODO
			// create a Ad listener
			 admobAdBanner.loadAd(new AdRequest.Builder().build());
		}
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
    	
    	Context context = this.getContext();

    	if (!isAmazonAd)
    	    isAmazonAd = new AndroidMarket(context).isFromAmazonMarket();
    	
    	if (isAmazonAd)
            try {
                AdRegistration.setAppKey(this.getContext().getResources().getString(R.string.amazon_app_key));
            } catch (final Exception e) {
                Log.e(LOG_TAG, "Exception thrown: " + e.toString());
                return;
            }
	}
	
	private void initializeAd() {
    	if (isAmazonAd) {
    		initializeAmazonAdBanner();
    	}
    	else {
    		initializeAdmobBanner();
    	}
    	this.addView(banner);
	}

	public void initializeAmazonAdBanner() {
		amazonAdBanner = (AdLayout) LayoutInflater.from(getContext()).inflate(R.layout.amazon_ad, null);
		amazonAdBanner.setListener(new AmazonAdListener());
		banner = amazonAdBanner;
	}
	
	public void initializeAdmobBanner() {
		// TODO
		// create a Ad listener
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

     }

	/**
     * This class is for an event listener that tracks ad lifecycle events.
     * It extends DefaultAdListener, so you can override only the methods that you need.
     */
    public class AmazonAdListener extends DefaultAdListener
    {

		/**
         * This event is called once an ad loads successfully.
         */
        public void onAdLoaded(final au.com.tyo.app.ui.Ad ad, final AdProperties adProperties) {
            Log.i(LOG_TAG, adProperties.getAdType().toString() + " ad loaded successfully.");
			state = AD_STATE_LOADED;
            controller.getUi().getCurrentPage().onAdLoaded();
        }
        
        /**
         * This event is called if an ad fails to load.
         */
        public void onAdFailedToLoad(final au.com.tyo.app.ui.Ad ad, final AdError error) {
			state = AD_STATE_FAILED;
            Log.w(LOG_TAG, "Ad failed to load. Code: " + error.getCode() + ", Message: " + error.getMessage());
        }
    
        /**
         * This event is called after a rich media ad expands.
         */
        public void onAdExpanded(final au.com.tyo.app.ui.Ad ad) {
            Log.i(LOG_TAG, "Ad expanded.");
            // You may want to pause your activity here.
        }
        
        /**
         * This event is called after a rich media ad has collapsed from an expanded state.
         */
        public void onAdCollapsed(final au.com.tyo.app.ui.Ad ad) {
            Log.i(LOG_TAG, "Ad collapsed.");
            // Resume your activity here, if it was paused in onAdExpanded.
        }
    }
}
