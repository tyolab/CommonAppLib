/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD.
 *
 */
package au.com.tyo.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import au.com.tyo.app.ui.Page;
import au.com.tyo.app.ui.UIActivity;
import au.com.tyo.app.ui.UIPage;

/**
 * 
 * @author Eric Tang <eric.tang@tyo.com.au>
 * 
 */
public class CommonFragmentActivity extends FragmentActivity implements UIActivity, CommonActivityAgent.ActivityActionListener {

    protected CommonActivityAgent agent;

	protected Controller controller;

	protected UIPage page;

	@Override
	public UIPage getPage() {
		return page;
	}

	@SuppressLint("MissingSuperCall")
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		controller = (Controller) CommonApp.getInstance();
		if (null == controller) {
			controller = CommonAppInitializer.getController(this);
			CommonApp.setInstance(controller);
		}

		agent = new CommonActivityAgent(this);

		createPage();
		onPageCreated();

        agent.preInitialize(savedInstanceState, page);

        super.onCreate(savedInstanceState);

        agent.onCreate(savedInstanceState);

		controller = (Controller) CommonApp.getInstance();
	}

	/**
	 * Create an assoicated page that contains all the widgets / controls
	 *
	 * if an custom page is needed just override this method to create a different page setting
	 */
	protected void createPage() {
		page = new Page(controller, this);
		page.setContentViewResId(R.layout.content);
	}

	/**
	 *
	 */
	protected void onPageCreated() {
	}


	@Override
	public void bindData(Intent intent) {
        page.bindData(intent);
	}

    @Override
    public void bindData() {
        page.bindData();
    }

    @Override
	public void onSaveData(Bundle savedInstanceState) {

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
  		
		agent.onResume(page);
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
		controller.onPrepareOptionsMenu(getActionBar(), menu);
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

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		if (hasFocus)
			controller.onWidowReady();
	}

	@Override
	public void finish() {
		page.onFinish();

		super.finish();
	}
}
