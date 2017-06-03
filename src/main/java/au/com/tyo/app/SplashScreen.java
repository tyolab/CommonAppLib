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
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import au.com.tyo.android.AndroidUtils;
import au.com.tyo.android.CommonInitializer;
import au.com.tyo.android.NetworkMonitor;

/**
 * @author Eric Tang <eric.tang@tyo.com.au>
 */
public class SplashScreen extends Activity implements SplashScreenMessageListener {
	
	private ProgressBar progressBar;
	
	private Controller controller;
	
	private InterstitialAd interstitial;
	
	private Handler handler;
	
	private boolean showAd;
	
	private boolean adLoaded;
	
	private boolean tasksStarted;

	private View viewOverlay;
	
	public SplashScreen() {
		super();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/**
		 * Controller initialisation
		 */
		if (controller == null) {
			if (CommonApp.getInstance() == null)
				CommonApp.setInstance(CommonInitializer.initializeInstance(CommonApp.class, this));
			controller = (Controller) CommonApp.getInstance();
		}

		controller.setCurrentActivity(this);
		controller.setContext(this);

		/**
		 * set splash screen layout
		 */
		setContentView(R.layout.splash_screen);
		
		adLoaded = false;
		
		tasksStarted = false;
		
		handler = new MessageHandler(this);
		
		progressBar = (ProgressBar) findViewById(R.id.progress_splash);
		
		controller.setAdStatus(this);
		
		showAd = controller.hasAd() 
				&& this.getResources().getBoolean(R.bool.show_ad_splash_screen_interstitial) 
				&& NetworkMonitor.hasInternet(this);
		
		if (showAd) {
			 // Create the interstitial.
		    interstitial = new InterstitialAd(this);
		    interstitial.setAdUnitId(this.getResources().getString(R.string.app_ad_unit_id_splash_screen_interstitial));
	        interstitial.setAdListener(new AdListener() {
	            @Override
	            public void onAdLoaded() {
	                super.onAdLoaded();
			        Message msg = Message.obtain();
			        msg.what = Constants.MESSAGE_AD_LOADED;
			        handler.sendMessage(msg);
	            }

	            @Override
	            public void onAdFailedToLoad(int errorCode) {
	                super.onAdFailedToLoad(errorCode);
			        Message msg = Message.obtain();
			        msg.what = Constants.MESSAGE_AD_FAILED;
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
		else /*if (!(controller.getContext() instanceof Activity))*/
        	controller.setContext(SplashScreen.this);

		viewOverlay = findViewById(R.id.splash_screen_overlay);
		
		handler.sendEmptyMessageDelayed(Constants.MESSAGE_AD_TIMEUP, 12000);

		/**
		 * Start background tasks
		 */
		startBackgroundTasks();
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
			if (msg.what == Constants.MESSAGE_APP_INITIALIZED)
			    listener.onAppInitialized();
			else {
				if (msg.what == Constants.MESSAGE_AD_LOADED)
					listener.onAdLoaded();
					
				listener.startBackgroundTasks();
			}
		}
	}

	private class AppInitializer extends AsyncTask<Void, Integer, Void> {
		
		private static final String LOG_TAG = "AppInitializer";
		

		public AppInitializer() {
		}
		
        @Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			Thread.currentThread().setName("AppInitializer");

			controller.onBackgroundTaskStarted(SplashScreen.this);
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			progressBar.setProgress(progress[0]);
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			/* it has to be initialise in an activity class */
			try {
				Thread.sleep(AndroidUtils.getAndroidVersion() > 10 ? 500 : 0);
			} catch (InterruptedException e) {
			}

			publishProgress(10);
			
			controller.createUi();

			handler.sendEmptyMessage(Constants.MESSAGE_APP_INITIALIZED);

			if (null != controller.getUi()) {
				controller.getUi().setupStartupAdView(viewOverlay, SplashScreen.this);
			}

			controller.initializeInBackgroundThread(SplashScreen.this);
			/*
			 * Sleep for one second to make sure to the splash screen can be seen
			 */
			publishProgress(100);

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

			/**
			 * if it returns false, it means the function will take care the main activity startup
			 */
			if (!controller.onBackgroundTaskEndded(SplashScreen.this)) {
				controller.startMainActivity();

				// close this activity
				finish();
			}
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
