/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD.
 *
 */
package au.com.tyo.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import au.com.tyo.android.AndroidUtils;
import au.com.tyo.app.ui.Page;
import au.com.tyo.app.ui.UIActivity;
import au.com.tyo.app.ui.UIPage;
import au.com.tyo.utils.StringUtils;

/**
 * 
 * @author Eric Tang <eric.tang@tyo.com.au>
 * 
 */
public class CommonAppCompatActivity extends AppCompatActivity implements UIActivity, PageAgent.ActivityActionListener {

	private static final String LOG_TAG = CommonAppCompatActivity.class.getSimpleName();

    protected PageAgent agent;

	private Controller controller;

	@SuppressLint("MissingSuperCall")
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		controller = (Controller) CommonApp.getInstance();
		if (null == controller) {
			controller = CommonAppInitializer.getController(this);
			CommonApp.setInstance(controller);
		}

        agent = new PageAgent(this);

        onCreatePage();

        agent.preInitialize(savedInstanceState, getPage());

        super.onCreate(savedInstanceState);

        agent.onCreate(savedInstanceState);

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
        // no ops
    }

	/**
	 *
	 */
    protected void onPageCreated() {
        UIPage page = getPage();
        if (page.getContentViewResId() <= 0)
            page.setContentViewResId(R.layout.content);
    }

    @Override
    public UIPage getPage() {
        return agent.getPage();
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

    }

    /**
	 * Get the controller instance
	 *
	 * @return
	 */
	public Controller getController() {
		return controller;
	}

	@Override
	protected void onNewIntent(Intent intent) {
  		setIntent(intent); 
  		agent.handleIntent(intent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return controller.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (!getPage().onOptionsItemSelected(item)) {
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
  		
		agent.onResume(getPage());
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
		controller.onPrepareOptionsMenu(getSupportActionBar(), menu);
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!getPage().onActivityResult(requestCode, requestCode, data))
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

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		if (hasFocus)
			controller.onWidowReady();
	}

	@Override
	public boolean onSupportNavigateUp() {
		return controller.onSupportNavigateUp() || super.onSupportNavigateUp();
	}

	@Override
	public void finish() {
		getPage().onFinish();

		super.finish();
	}

}
