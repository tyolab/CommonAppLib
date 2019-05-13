package au.com.tyo.app.ui.page;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import au.com.tyo.android.AndroidUtils;
import au.com.tyo.android.NetworkMonitor;
import au.com.tyo.app.CommonLog;
import au.com.tyo.app.Constants;
import au.com.tyo.app.Controller;
import au.com.tyo.app.R;
import au.com.tyo.app.SplashScreenMessageListener;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 2/10/17.
 */

public class PageSplashScreen extends Page implements SplashScreenMessageListener {

    private ProgressBar progressBar;

    private Controller controller;

    private InterstitialAd interstitial;

    private Handler handler;

    private boolean showAd;

    private boolean adLoaded;

    private boolean tasksStarted;

    private View viewOverlay;

    public PageSplashScreen(Controller controller, Activity activity) {
        super(controller, activity);

        this.controller = controller;
        this.hideActionBar = true;

        /**
         * set splash screen layout
         */
        setContentViewResId(R.layout.splash_screen);
    }

    @Override
    public void setupComponents() {
        super.setupComponents();

        Activity activity = getActivity();

        adLoaded = false;

        tasksStarted = false;

        handler = new MessageHandler(this);

        progressBar = (ProgressBar) findViewById(R.id.progress_splash);

        controller.setAdStatus(activity);

        showAd = controller.hasAd()
                && activity.getResources().getBoolean(R.bool.show_ad_splash_screen_interstitial)
                && NetworkMonitor.hasInternet(activity);

        if (showAd) {
            // Create the interstitial.
            interstitial = new InterstitialAd(activity);
            interstitial.setAdUnitId(activity.getResources().getString(R.string.app_ad_unit_id_splash_screen_interstitial));
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

//        if (controller.getContext() == null)
//            controller.initializeInMainThread(activity);

        viewOverlay = findViewById(R.id.splash_screen_overlay);
    }


    @Override
    public void onActivityStart() {
        super.onActivityStart();

        handler.sendEmptyMessageDelayed(Constants.MESSAGE_AD_TIMEUP, 3200);

        /**
         * Start background tasks
         */
        startBackgroundTasks();
    }

    public void startBackgroundTasks() {
        synchronized (this) {
            if (!tasksStarted) {
                new BackgroundTaskRunner().execute();
                tasksStarted = true;
            }
        }
    }

    // Invoke displayInterstitial() when you are ready to display an interstitial.
    private void displayInterstitial() {
        if (interstitial.isLoaded()) {
            interstitial.show();
        }
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

    private class BackgroundTaskRunner extends AsyncTask<Void, Integer, Void> {

        private static final String LOG_TAG = "BackgroundTaskRunner";


        public BackgroundTaskRunner() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            controller.onBackgroundTaskStarted(getActivity());
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            progressBar.setProgress(progress[0]);
        }

        @Override
        protected Void doInBackground(Void... params) {
			/* it has to be initialise in an activity class */
            CommonLog.i(this, "Running in the background for data initialization");

            publishProgress(10);

            controller.createUi();

            handler.sendEmptyMessage(Constants.MESSAGE_APP_INITIALIZED);

            if (null != controller.getUi()) {
                controller.getUi().setupStartupAdView(viewOverlay, PageSplashScreen.this.getActivity());
            }

            controller.initializeInBackgroundThread(PageSplashScreen.this.getActivity());
			/*
			 * Sleep for one second to make sure to the splash screen can be seen
			 */
            publishProgress(100);

            CommonLog.i(this, "App initialized.");

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (adLoaded)
                try {
                    Thread.sleep(AndroidUtils.getAndroidVersion() > 10 ? 4500 : 3000);
                } catch (InterruptedException e) {
                    CommonLog.e(this, "Thread interrupted", e);
                }

            /**
             * if it returns false, it means the function will take care the main activity startup
             */
            if (!controller.onBackgroundTaskEnded(PageSplashScreen.this.getActivity())) {
                controller.startMainActivity();

                // close this activity, called in the above method
                // this activity will be finished when main activity is started in controller
                // finish();
            }
            else
                CommonLog.i(this, "Background task finished, but not gonna starting the main activity");
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

    /**
     * The only way for data passing between other apps and the app with tyodroid framework
     *
     * @param intent
     */
    @Override
    public void bindData(Intent intent) {
        super.bindData(intent);

        if (null != controller)
            controller.bindDataFromOtherApps(intent);
    }
}
