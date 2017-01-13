/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD.
 *
 */

package au.com.tyo.app.ui;

import com.amazon.device.ads.*;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import au.com.tyo.android.AndroidMarket;
import au.com.tyo.app.Controller;
import au.com.tyo.app.R;

/**
 * @author Eric Tang <eric.tang@tyo.com.au>
 */


public class AllAdView extends FrameLayout {
	
    private static final String LOG_TAG = "AllAdView";
	
	private AdView admobAdBanner;
	
	private AdLayout amazonAdBanner;
	
	private boolean isAmazonAd;
	
	private View banner;
	
	private Controller controller;
	
	private ViewGroup parent;

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
	}

	public void loadBannerAd() {
		if (banner != null)	 
			this.removeView(banner);
		
		initializeAd();

		loadAd();
//	    show();
		hide();
	}

	private void loadAd() {
		if (isAmazonAd) {
			amazonAdBanner.loadAd();
		}
		else {
			admobAdBanner.loadAd(new AdRequest.Builder().build());
		}
	}

	public void show() {
		if (controller.getNetworkMonitor().hasInternet())
			parent.setVisibility(View.VISIBLE);
	}

	public void hide() {
		parent.setVisibility(View.GONE);
	}

	public void initialize(Controller controller, ViewGroup parent) {
    	this.controller = controller;
    	this.parent = parent;
    	
    	Context context = this.getContext();
    	
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
		admobAdBanner = (AdView) LayoutInflater.from(this.getContext()).inflate(R.layout.admob, null);
		admobAdBanner.setAdListener(new AdmobAdListener());
		banner = admobAdBanner;
	}
    
    public class AdmobAdListener extends com.google.android.gms.ads.AdListener {

		@Override
		public void onAdLoaded() {
			super.onAdLoaded();
			
			controller.getUi().onAdLoaded();
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
        public void onAdLoaded(final Ad ad, final AdProperties adProperties) {
            Log.i(LOG_TAG, adProperties.getAdType().toString() + " ad loaded successfully.");
            controller.getUi().onAdLoaded();
        }
        
        /**
         * This event is called if an ad fails to load.
         */
        public void onAdFailedToLoad(final Ad ad, final AdError error) {
            Log.w(LOG_TAG, "Ad failed to load. Code: " + error.getCode() + ", Message: " + error.getMessage());
        }
    
        /**
         * This event is called after a rich media ad expands.
         */
        public void onAdExpanded(final Ad ad) {
            Log.i(LOG_TAG, "Ad expanded.");
            // You may want to pause your activity here.
        }
        
        /**
         * This event is called after a rich media ad has collapsed from an expanded state.
         */
        public void onAdCollapsed(final Ad ad) {
            Log.i(LOG_TAG, "Ad collapsed.");
            // Resume your activity here, if it was paused in onAdExpanded.
        }
    }
}
