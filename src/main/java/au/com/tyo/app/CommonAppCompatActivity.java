/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD.
 *
 */
package au.com.tyo.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import au.com.tyo.app.ui.UIActivity;
import au.com.tyo.app.ui.UIPage;

/**
 * 
 * @author Eric Tang <eric.tang@tyo.com.au>
 * 
 */
public class CommonAppCompatActivity extends AppCompatActivity implements UIActivity, PageAgent.ActivityActionListener {

	private static final String LOG_TAG = CommonAppCompatActivity.class.getSimpleName();

	private static Class pageClass;

    protected PageAgent agent;

	private Controller controller;

	public static void setPageClass(Class pageClass) {
		CommonAppCompatActivity.pageClass = pageClass;
	}

	protected void createController() {
		controller = (Controller) CommonApp.getInstance();
		if (null == controller) {
			controller = CommonAppInitializer.getController(this);
			CommonApp.setInstance(controller);
		}
		onControllerCreated(controller);
	}

	protected void onControllerCreated(Controller controller) {
		// do nothing
	}

	@SuppressLint("MissingSuperCall")
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		// controller has to be created first
		createController();

		boolean ret = (beforeCreateCheck());

        agent = new PageAgent(this);

        onCreatePage();
        if (!ret)
            getPage().onPreCreateCheckFailed();

        agent.preInitialize(savedInstanceState, getPage());

        super.onCreate(savedInstanceState);

        agent.onCreate(savedInstanceState);
	}

	protected boolean beforeCreateCheck() {
		// check command
		return true;
	}

	public PageAgent getAgent() {
        return agent;
    }

    /**
     * For overriding
     */
    protected void onCreatePage() {
        loadPageClass();
        createPage();
        onPageCreated();
    }

    /**
     * Create page
     */
    protected void createPage() {
        agent.createPage();
    }

    /**
     *
     */
    protected void loadPageClass() {
        if (null != pageClass)
        	agent.setPageClass(pageClass);
    }

	/**
	 *
	 */
    protected void onPageCreated() {

		UIPage page = getPage();

		if (null == page)
			throw new IllegalStateException("The page instance is not initialised, please make sure that you have set up the page class or assign on properly");

		page.onPostCreate();
    }

    @Override
    public UIPage getPage() {
        return agent != null ? agent.getPage() : null;
    }

    public void setPage(UIPage page) {
        agent.setPage(page);
    }

	@Override
	public void bindData() {
		getPage().bindData();
	}

	@Override
    public void bindData(Intent intent) {
		getPage().bindData(intent);
    }

    @Override
    public void onSaveData(Bundle savedInstanceState) {
		getPage().saveState(savedInstanceState);
    }

	@Override
	public void onDataBoundFinished() {
		getPage().onDataBound();
	}

	/**
	 * Get the controller instance
	 *
	 * @return
	 */
	public Controller getController() {
		return controller;
	}

    /**
     * if the activity already created, but we would like to pass on more data
     *er
     * @param intent
     */
	@Override
	protected void onNewIntent(Intent intent) {
  		setIntent(intent); 
  		agent.handleIntent(intent);
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return getPage().onOptionsItemSelected(item) || controller.onOptionsItemSelected(this, item) || super.onOptionsItemSelected(item);
    }
	
	@Override
	protected void onPause() {
		super.onPause();
		
		agent.onPause();
	}
    
  	@Override
  	protected void onResume() {
  		super.onResume();
  		
		agent.onResume(getPage());
  	}

  	@Override
  	protected void onDestroy() {
  		super.onDestroy();

  		if (!getPage().onDestroy() && isFinishing() && !getPage().isSubpage())
  			controller.onDestroy();
  	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return getPage().onKeyDown(keyCode, event) || controller.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return getPage().onKeyUp(keyCode, event) || controller.onKeyUp(keyCode, event) || super.onKeyUp(keyCode, event);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return getPage().onPrepareOptionsMenu(getSupportActionBar(), menu) || super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!getPage().onActivityResult(requestCode, resultCode, data))
		    controller.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		return controller.onKeyLongPress(keyCode, event) || super.onKeyLongPress(keyCode, event);
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
    protected void onSaveInstanceState(Bundle savedInstanceState) {

    	controller.onSaveInstanceState(savedInstanceState);
        
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		if (hasFocus && !isFinishing())
			controller.onWidowReady();
	}

	@Override
	public boolean onSupportNavigateUp() {
		return controller.onSupportNavigateUp() || super.onSupportNavigateUp();
	}

	@Override
	public void finish() {
        if (null != getPage())
		    getPage().onFinish();

		super.finish();

		if (controller.getCurrentActivity() == this)
			controller.setCurrentActivity(null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getPage().onCreateOptionsMenu(getMenuInflater(), menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onStart() {
		super.onStart();

		getPage().onStart();
	}

    @Override
    public void onUiCreated() {
        controller.onUiCreated();
    }

    @Override
    public void onUiReady() {
        controller.onUiReady();
    }

	@Override
	protected void onStop() {
		super.onStop();

		getPage().onStop();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int i = 0; i < permissions.length; ++i) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                getPage().onRequestedPermissionsGranted(permissions[i]);
            else
                getPage().onRequestedPermissionsDenied(permissions[i]);
        }
	}

}
