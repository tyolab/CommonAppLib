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
import android.os.PersistableBundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import au.com.tyo.app.ui.UIActivity;
import au.com.tyo.app.ui.UIPage;

/**
 * 
 * @author Eric Tang <eric.tang@tyo.com.au>
 * 
 */
public class CommonAppCompatActivity<ControllerType extends Controller> extends AppCompatActivity implements UIActivity, PageAgent.PageActionListener {

	private static final String LOG_TAG = CommonAppCompatActivity.class.getSimpleName();

	private Class pageClass;

    protected PageAgent agent;

	private ControllerType controller;

	public void setPageClass(Class pageClass) {
		this.pageClass = pageClass;
	}

	protected void createController() {
		controller = CommonApp.getInstance();
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

		PageAgent agent = getAgent();

		// controller has to be created first
		createController();

		boolean ret = false;
		if (null != controller) {
		    agent.setController(controller);

			ret = (beforeCreateCheck());

			onCreatePage();
		}

        if (null != agent.getPage()) {
        	if (!getPage().isSubpage()) {
        		if (!controller.isMainThreadInitialised())
        			controller.initializeInMainThread(this);

        		if (!controller.isBackgroundThreadInitialised())
        			controller.initializeInBackgroundThread(this);
			}

			if (!ret)
				getPage().onPreCreateCheckFailed();

			agent.preInitialize(savedInstanceState, getPage());

			super.onCreate(savedInstanceState);

			agent.onCreate(savedInstanceState);
		}
		else
			super.onCreate(savedInstanceState);
	}

	protected boolean beforeCreateCheck() {
		// check command
		return true;
	}

	public PageAgent getAgent() {
		if (null == agent)
			agent = new PageAgent(this);
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
    	if (null == getPage())
        	getAgent().createPage();
    }

    /**
     *
     */
    protected void loadPageClass() {
        if (null != pageClass)
        	getAgent().setPageClass(pageClass);
    }

	/**
	 * Things to do after page is created
	 */
	@OverridingMethodsMustInvokeSuper
    protected void onPageCreated() {

		UIPage page = getPage();

		if (null == page) {
			Log.w(LOG_TAG, "The page instance is not initialised, please make sure that you have set up the page class or assign on properly");
			return;
		}
		page.onPostCreate(null);
    }

    @Override
    public UIPage getPage() {
        return agent != null ? agent.getPage() : null;
    }

    public void setPage(UIPage page) {
        getAgent().setPage(page);
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
	public ControllerType getController() {
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
        return (null != getPage() && getPage().onOptionsItemSelected(item)) || controller.onOptionsItemSelected(this, item) || super.onOptionsItemSelected(item);
    }
	
	@Override
	protected void onPause() {
		super.onPause();
		
		agent.onPause();
	}
    
  	@Override
  	protected void onResume() {
  		super.onResume();

        if (null != getPage())
		    agent.onResume(getPage());
  	}

  	@Override
  	protected void onDestroy() {
  		super.onDestroy();

  		agent.onDestroy();
  	}

    /**
     * The key event can be triggered when the App is not active or already exits
     * which means the controller is out of scope, or may be not even initialized
     *
     * @param keyCode
     * @param event
     * @return
     */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return (null != getPage() && getPage().onKeyDown(keyCode, event)) || super.onKeyDown(keyCode, event);
	}

    /**
     * The key event can be triggered when the App is not active or already exits
     * which means the controller is out of scope, or may be not even initialized
     *
     * @param keyCode
     * @param event
     * @return
     */
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return (null != getPage() && getPage().onKeyUp(keyCode, event)) || super.onKeyUp(keyCode, event);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return (null != getPage() && getPage().onPrepareOptionsMenu(getSupportActionBar(), menu)) || super.onPrepareOptionsMenu(menu);
	}

	/**
	 * On activity result happens in a different thread when current page in UI get de-registered
	 *
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (null != getPage()) {
            controller.getUi().setCurrentScreen(getPage());

            if (!getPage().onActivityResult(requestCode, resultCode, data))
                super.onActivityResult(requestCode, resultCode, data);
        }
        else
            super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		return (null != getPage() && getPage().onKeyLongPress(keyCode, event)) || super.onKeyLongPress(keyCode, event);
	}
	
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (null != getPage())
            getPage().onPostCreate(savedInstanceState);
    }
 
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // needs to recreate UI after configuration changed, from portrait to landscape or the other way around
        if (null != controller && null != controller.getUi())
        	controller.getUi().setUiRecreationRequired(true);

        if (null != getPage())
            getPage().onConfigurationChanged(newConfig);
    }
    
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {

        if (null != getPage())
    	    getPage().onSaveInstanceState(savedInstanceState);
        
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		if (null != getPage() && hasFocus && !isFinishing())
			getPage().onWidowReady();
	}

	@Override
	public boolean onSupportNavigateUp() {
		return controller.onSupportNavigateUp() || super.onSupportNavigateUp();
	}

	@Override
	public void finish() {
	    if (null != controller) {
            if (null != controller.getUi())
                controller.getUi().setPreviousPage(getPage());

            if (controller.getCurrentActivity() == this)
                controller.setCurrentActivity(null);
        }

		// page could be null
        if (null != getPage())
		    getPage().onFinish();

		super.finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        if (null != getPage())
		    getPage().onCreateOptionsMenu(getMenuInflater(), menu);
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * The start of every cycle
	 */
	@Override
	protected void onStart() {
		super.onStart();

		if (null != getPage())
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

        if (null != getPage())
		    getPage().onStop();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (null != getPage())
            for (int i = 0; i < permissions.length; ++i) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    getPage().onRequestedPermissionsGranted(permissions[i]);
                else
                    getPage().onRequestedPermissionsDenied(permissions[i]);
            }
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		getPage().onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
		super.onRestoreInstanceState(savedInstanceState, persistentState);

		getPage().onRestoreInstanceState(savedInstanceState, persistentState);
	}

	@Override
	public void onBackPressed() {
		if (null == getPage() || !getPage().onBackPressed())
			super.onBackPressed();
	}
}
