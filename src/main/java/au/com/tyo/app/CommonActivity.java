/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD.
 *
 */
package au.com.tyo.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import java.lang.reflect.Field;

import au.com.tyo.android.AndroidUtils;
import au.com.tyo.app.ui.UI;

/**
 * 
 * @author Eric Tang <eric.tang@tyo.com.au>
 * 
 */
public class CommonActivity extends Activity  {
	
	private static final String LOG_TAG = "CommonActivity";
	
	private Controller controller;
	
//	protected boolean startingMainUi;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
        if (controller == null) {
			if (CommonApp.getInstance() == null)
				CommonApp.initializeInstance(CommonApp.class, this);
	        controller = (Controller) CommonApp.getInstance();
        }
        
        
//    	startingMainUi = true;
        
//        boolean hasExtras = checkExtras();
//        
//        if (!hasExtras && (controller.getUi() == null/* || controller.getUi().getMainView() == null)*/) {
//			super.onCreate(savedInstanceState);
//			
//        	controller.startSplashScreenActivity(this);
//        	
////        	startingMainUi = false;
//        	
//        	finish();
//        }
//        else {
        	/*
        	 * Things need to be done before having the UI inflated
        	 */
//        	if (controller.isDataReady() && controller.isDataDownloaded()) {
//                /*
//                 * We have to have this 
//                 */
//        		if (hasExtras) {
//	        		if (controller.isAppReady())
//	        			controller.setContext(this);
//	        		else
//	        			controller.setContext(null);
//        		}
//        		else
//        			controller.setContext(this);
//            	CommonApp.initializeInstance();
                controller.setActivityContext(this);
                controller.setContext(this);
                
        		run(savedInstanceState);
//        	}
//        	else {
//    			super.onCreate(savedInstanceState);
//        		startDataHandlingActivity();
//        	}
//        }
        		
                
        /*
         * after UI initialization, do whatever needs to be done, like setting tup the settings, etc.
         */
        controller.onAppStart();
	}

	protected void startDataHandlingActivity() {
		// do nothing here
	}

	@SuppressWarnings("unused")
	protected void run(Bundle savedInstanceState) {
    	if (savedInstanceState != null)
    		controller.onRestoreInstanceState(savedInstanceState);
    	
    	if (controller.getUi() == null || controller.getUi().recreationRequried())
    		controller.createUi();
    	
//        controller.getSettings().setDefaultLocale();
		int themeId = controller.getSettings().getThemeId();
		if (themeId > 0)
			setTheme(themeId);
		
		super.onCreate(savedInstanceState);
		
		/*
		 * TODO
		 * 
		 * implement this for optimization for tablet
		 * 
		 */
//        if (!AndroidSettings.isTablet(this))
//        	setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
		if (AndroidUtils.getAndroidVersion() < 7)
			setupTitleBar1();
        
//        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        
		if (AndroidUtils.getAndroidVersion() >= 7)
			setupActionBarBar(controller.getUi());
		else
			setupTitleBar2();
		
		/*
		 * now show the overflow menu
		 */
		showOverflowMenu();
        
        if (true) {
			/**
			 * do it in a different way
			 */
//        	setContentView(R.layout.activity_common);
//            FrameLayout frameLayout = (FrameLayout) getWindow()
//                    .getDecorView().findViewById(android.R.id.content);

            controller.getUi().initializeUi(this);
			setContentView(controller.getUi().getMainView());
        }
        else {
            FrameLayout frameLayout = (FrameLayout) getWindow()
                    .getDecorView().findViewById(android.R.id.content);
            
	        frameLayout.removeAllViews();
	        controller.getUi().assignMainUiContainer(frameLayout);        
	        controller.getUi().initializeUi(this);
        }
//        
//        /*
//         * to some ui initial work in case something wrong with VM when this activity class was recreated
//         *  
//         */
//        controller.getUi().onCreateMainActivity(this);
//        
//        controller.onCreateCheck();
//        
//        controller.onFinishActivityCreation();
        
        this.processExtras();
        
        controller.onUiReady();
	}

	private void setupTitleBar1() {
//        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
	}

	private void setupTitleBar2() {
//        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.information_view); 
	}

	@SuppressLint("NewApi")
	protected void setupActionBarBar(UI ui) {
//        getSupportActionBar().setCustomView(R.layout.title);
//        getSupportActionBar().setDisplayShowCustomEnabled(true);
		Object bar = null;
		if (AndroidUtils.getAndroidVersion() >= 11)
			bar = getActionBar();
        ui.setupActionBar(bar);

//        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

//        InformationView customActionBarView = 
//            (InformationView) getLayoutInflater().inflate(R.layout.information_view, null, true);
//        ui.setInformationView(customActionBarView);
//        customActionBarView.setupComponents(controller);
//        
//        ActionBar.LayoutParams lp = 
//            new ActionBar.LayoutParams(LayoutParams.WRAP_CONTENT, 
//                LayoutParams.WRAP_CONTENT);
//
//        lp.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
////        lp.topMargin = -18;
//        
//        bar.setCustomView(customActionBarView, lp);
        /*
        bar.setLogo(R.drawable.logo);
//        bar.setIcon(R.drawable.ic_launcher);
        bar.setDisplayUseLogoEnabled(true);
//        bar.setHomeButtonEnabled(true);
//        bar.setDisplayHomeAsUpEnabled(true);
//        bar.setDisplayShowCustomEnabled(true);
        bar.setDisplayShowHomeEnabled(true);
        bar.setDisplayShowTitleEnabled(false);
        */
	}
	
	protected boolean checkExtras() {
		String action = this.getIntent().getAction();
		if ((action != null && 
				action.equalsIgnoreCase("android.intent.action.ASSIST")) ||
				getIntent().getExtras() != null || 
				(getIntent().getDataString() != null && 
					getIntent().getDataString().length() > 0))
			return true;
		return false;
	}
	
	protected void processExtras() {
		Intent intent = this.getIntent();
		String action =intent.getAction();
		// to see where it is from
		if (action != null && action.equalsIgnoreCase("android.intent.action.ASSIST")) {
			Log.d(LOG_TAG, "starting native voice recognizer from main activity");
			getIntent().setAction("");
		}
		else {
//			Bundle extras =intent.getExtras();
			String url = intent.getDataString();
			if (url != null && url.length() > 0) {
//				controller.processUrl(url);
				intent.setData(null);
			}
			
//			if (extras != null) {
				intent.replaceExtras((Bundle) null);
	//			if (indexStr != null) {
	//				int index = Integer.parseInt(indexStr);
	//				controller.setCurrentDoodle(index);
	//			}
//			}
		}
	}

  	@Override
	protected void onNewIntent(Intent intent) {
  		setIntent(intent); 
  		handleIntent(intent); 
	}

	private void handleIntent(Intent intent) {
	      if (Intent.ACTION_SEARCH.equals(intent.getAction())) { 
	         String query = intent.getStringExtra(SearchManager.QUERY); 
//	         controller.search(query); 
	      } 
	}
	
	protected void setController(Controller controller) {
		this.controller = controller;
	}
      
//	private void setupCompoments() {
//	    TextView titleTextView = (TextView) findViewById(R.id.app_title);
//	    TextView langInfoTextView = (TextView) findViewById(R.id.language_info);
//		UI ui = controller.getUi();
//		ui.setTitleTextView(titleTextView);
//		ui.setLangInfoTextView(langInfoTextView);
//	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
//		getSupportMenuInflater().inflate(R.menu.activity_wikie_talkie, menu);
		return controller.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (!controller.onOptionsItemSelected(this, item)) {
			return super.onOptionsItemSelected(item);
		}
		return true;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		controller.onPause();
	}
    
  	@Override
  	protected void onResume() {
  		super.onResume();
  		
        controller.setActivityContext(this);
        controller.setContext(this);
  		
		processExtras();
		
  		controller.onResume();
		
  		/*
  		 * The wiki language info may have been changed
  		 */
//		supportInvalidateOptionsMenu();
  	}

  	@Override
  	protected void onDestroy() {
  		super.onDestroy();
  		
  		if (isFinishing())
  			controller.onDestroy();
  	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return controller.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return controller.onKeyUp(keyCode, event) || super.onKeyUp(keyCode, event);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {	   
		controller.onPrepareOptionsMenu(menu);
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		controller.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		return controller.onKeyLongPress(keyCode, event)  || super.onKeyLongPress(keyCode, event);
	}
	
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
   
        controller.onPostCreate(savedInstanceState);
    }
 
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        controller.onConfigurationChanged(newConfig);
    }
    
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

    	controller.onSaveInstanceState(savedInstanceState);
        
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
    
/*    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);
       
        controller.onRestoreInstanceState(savedInstanceState);
    }*/
    
    /**
     * show the overflow menu (three-dot)
     */
    private void showOverflowMenu() {
        try {
           ViewConfiguration config = ViewConfiguration.get(this);
           Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
           if(menuKeyField != null) {
               menuKeyField.setAccessible(true);
               menuKeyField.setBoolean(config, false);
           }
       } catch (Exception e) {
           e.printStackTrace();
       }
     }
}
