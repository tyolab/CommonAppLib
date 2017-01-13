/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD.
 *
 */
package au.com.tyo.app;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ProgressBar;

import com.google.android.gms.ads.*;

import au.com.tyo.android.AndroidSettings;
import au.com.tyo.android.AndroidUtils;
import au.com.tyo.app.R;

/**
 * @author Eric Tang <eric.tang@tyo.com.au>
 */
public class SplashScreen extends Activity implements SplashScreenMessageListener {
	
	private static int MESSAGE_AD_LOADED = 99;
	
	private static int MESSAGE_AD_FAILED = -1;
	
	private static int MESSAGE_AD_TIMEUP = -2;
	
	private static int MESSAGE_APP_INITIALIZED = 1000;
	
	private ProgressBar progressBar;
	
	private Controller controller;
	
	private InterstitialAd interstitial;
	
	private Handler handler;
	
	private boolean showAd;
	
	private boolean adLoaded;
	
	private boolean tasksStarted;
	
	public SplashScreen() {
		super();
		
        if (controller == null) {
			if (CommonApp.getInstance() == null)
				CommonApp.initializeInstance(null);
	        controller = (Controller) CommonApp.getInstance();
        }
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		
		setContentView(R.layout.splash_screen);
		
		adLoaded = false;
		
		tasksStarted = false;
		
		handler = new MessageHandler(this);
			
//        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title); 
		
		progressBar = (ProgressBar) findViewById(R.id.progress_splash);
		
		controller.setAdStatus(this);
		
		showAd = controller.hasAd() 
				&& this.getResources().getBoolean(R.bool.show_ad_splash_screen_interstitial) 
				&& NetworkMonitor.checkNetworkState(this);
		
		if (showAd) {
			 // Create the interstitial.
		    interstitial = new InterstitialAd(this);
		    interstitial.setAdUnitId(this.getResources().getString(R.string.app_ad_unit_id_splash_screen_interstitial));
	        interstitial.setAdListener(new AdListener() {
	            @Override
	            public void onAdLoaded() {
	                super.onAdLoaded();
			        Message msg = Message.obtain();
			        msg.what = MESSAGE_AD_LOADED;
			        handler.sendMessage(msg);
	            }

	            @Override
	            public void onAdFailedToLoad(int errorCode) {
	                super.onAdFailedToLoad(errorCode);
			        Message msg = Message.obtain();
			        msg.what = MESSAGE_AD_FAILED;
			        handler.sendMessage(msg);
	            }
	        });
	        
		    // Create ad request.
		    AdRequest adRequest = new AdRequest.Builder().build();

		    // Begin loading your interstitial.
		    interstitial.loadAd(adRequest);
		}
			
		if (controller.getContext() == null)
			controller.initializeInMainThread(this);
        /*
         * We have to have this 
         */
//	        controller.setActivityContext(SplashScreen.this);
		else /*if (!(controller.getContext() instanceof Activity))*/
        	controller.setContext(SplashScreen.this);
		
		if (!showAd)
			startBackgroundTasks();
		
		handler.sendEmptyMessageDelayed(MESSAGE_AD_TIMEUP, 12000);
	}
	
	public void startBackgroundTasks() {  	
		synchronized (this) {
			if (!tasksStarted) {
				new AppInitializer().execute();
			    tasksStarted = true;
			}
		}
	}
	
	// Invoke displayInterstitial() when you are ready to display an interstitial.
	private void displayInterstitial() {
//		if (showAd) {
//			int count = 0;
//			while (count < 5) {
				if (interstitial.isLoaded()) {
					interstitial.show();
//					break;
//				}
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//				}
//				++count;
			} 
//		}
	}
	
	private static class MessageHandler extends Handler {
		
		private SplashScreenMessageListener listener; 
		
		public MessageHandler(SplashScreenMessageListener listener) {
			super();
			this.listener = listener;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {			
			if (msg.what == MESSAGE_APP_INITIALIZED)
			    listener.onAppInitialized();
			else {
				if (msg.what == MESSAGE_AD_LOADED)
					listener.onAdLoaded();
					
				listener.startBackgroundTasks();
			}
		}
	}

	private class AppInitializer extends AsyncTask<Void, Void, Void> {
		
		private static final String LOG_TAG = "AppInitializer";
		

		public AppInitializer() {
		}
		
        @Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			Thread.currentThread().setName("AppInitializer");
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			/* it has to be run in an activity class */
//			KoalaSearchApp.getInstance().initializeVariablesForGUI();
			try {
//				if (/*controller.getUi() == null && */) {
//					if (adLoaded)
//						Thread.sleep(AndroidUtils.getAndroidVersion() > 10 ? 4500 : 3000);
//					else
						Thread.sleep(AndroidUtils.getAndroidVersion() > 10 ? 500 : 0);
//				}
			} catch (InterruptedException e) {
			}
			
			progressBar.setProgress(10);

			controller.initializeInBackgroundThread(SplashScreen.this);
			
			handler.sendEmptyMessage(MESSAGE_APP_INITIALIZED);
			
			controller.createUi();
			
			/*
			 * Sleep for one second to make sure to the splash screen can be seen
			 */
			progressBar.setProgress(100);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			
			if (adLoaded)
				try {
					Thread.sleep(AndroidUtils.getAndroidVersion() > 10 ? 4500 : 3000);
				} catch (InterruptedException e) {
				}
			controller.startMainActivity();
 
            // close this activity
            finish();
		}
	}

	@Override
	public void onAppInitialized() {
		controller.loadHistory();
	}

	@Override
	public void onAdLoaded() {
		displayInterstitial();	
	}
}
