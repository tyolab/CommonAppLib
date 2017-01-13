/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 * 
 */

package au.com.tyo.app.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import au.com.tyo.app.R;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

/**
 * @author Eric Tang <eric.tang@tyo.com.au>
 */


public class Ad {
	
	private AdView banner;
	
	public Ad() {
		banner = null;
	}

	public void loadBannerAd(ViewGroup parent) {
		if (parent == null)
			return;
		
		if (banner != null)	 {
			parent.removeView(banner);
			banner = null;
			banner = (AdView) LayoutInflater.from(parent.getContext()).inflate(R.layout.ad, null);
			parent.addView(banner);
		}
		else
			banner = (AdView) parent.findViewById(R.id.adView);
	    banner.loadAd(new AdRequest.Builder().build());

	    show();
	}

	public void show() {
		if (banner != null)
			banner.setVisibility(View.VISIBLE);
	}

	public void hide() {
		if (banner != null)
			banner.setVisibility(View.GONE);
	}
	
}
