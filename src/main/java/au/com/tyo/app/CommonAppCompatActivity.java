/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD.
 *
 */
package au.com.tyo.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

/**
 * 
 * @author Eric Tang <eric.tang@tyo.com.au>
 * 
 */
public class CommonAppCompatActivity extends AppCompatActivity {

    protected CommonActivityAgent agent;

	protected Controller controller;

	@SuppressLint("MissingSuperCall")
    @Override
	protected void onCreate(Bundle savedInstanceState) {
        agent = new CommonActivityAgent(this);
        agent.preInitialize(savedInstanceState);

        super.onCreate(savedInstanceState);

        agent.onCreate(savedInstanceState);

		controller = (Controller) CommonApp.getInstance();
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
  		
		agent.onResume();
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

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		if (hasFocus)
			controller.onWidowReady();
	}
}
