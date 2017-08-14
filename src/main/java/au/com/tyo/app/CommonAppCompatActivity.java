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
public class CommonAppCompatActivity extends AppCompatActivity implements UIActivity, CommonActivityAgent.ActivityActionListener {

	private static final String LOG_TAG = CommonAppCompatActivity.class.getSimpleName();

    private static String pagesPackage;

	private Class pageClass = null;

    protected CommonActivityAgent agent;

	private Controller controller;

	protected UIPage page;

    public static void setPagesPackage(String pagesPackage) {
        CommonAppCompatActivity.pagesPackage = pagesPackage;
    }

    public void setPageClass(Class pageClass) {
		this.pageClass = pageClass;
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

        loadPageClass();
		createPage();
        onPageCreated();

        agent.preInitialize(savedInstanceState, page);

        super.onCreate(savedInstanceState);

        agent.onCreate(savedInstanceState);

	}

    /**
     * For overriding
     */
    protected void loadPageClass() {
        // do nothing
    }

    /**
	 * Create an assoicated page that contains all the widgets / controls
	 *
	 * if a custom page is needed just override this method to create a different page setting
	 */
	protected void createPage() {
        if (null == page) {

            if (null == pageClass) {
                if (pagesPackage == null)
                    pagesPackage = AndroidUtils.getPackageName(this);

                try {
                    String extName = this.getClass().getName().substring("Activity".length() - 1);
                    String pageClassName = pagesPackage + ".Page" + extName;
                    pageClass = Class.forName(pageClassName);
                }
                catch (Exception ex) {

                }
            }

			if (pageClass != null) {
				try {
                    Constructor ctor = null;
					ctor = pageClass.getConstructor(controller.getClass(), Activity.class);
					page = (UIPage) ctor.newInstance(new Object[]{controller, this});
				} catch (NoSuchMethodException e) {
					Log.e(LOG_TAG, StringUtils.exceptionStackTraceToString(e));
				} catch (IllegalAccessException e) {
					Log.e(LOG_TAG, StringUtils.exceptionStackTraceToString(e));
				} catch (InstantiationException e) {
					Log.e(LOG_TAG, StringUtils.exceptionStackTraceToString(e));
				} catch (InvocationTargetException e) {
					Log.e(LOG_TAG, StringUtils.exceptionStackTraceToString(e));
				}
			}
		}

		if (null == page)
			page = new Page(controller, this);
	}

	/**
	 *
	 */
    protected void onPageCreated() {
        if (page.getContentViewResId() <= 0)
            page.setContentViewResId(R.layout.content);
    }

    @Override
    public UIPage getPage() {
        return page;
    }

    public void setPage(UIPage page) {
        this.page = page;
    }

	@Override
	public void bindData() {
		page.bindData();
	}

	@Override
    public void bindData(Intent intent) {
		page.bindData(intent);
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
		if (!page.onOptionsItemSelected(item)) {
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
		controller.onPrepareOptionsMenu(getSupportActionBar(), menu);
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!page.onActivityResult(requestCode, requestCode, data))
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
		page.onFinish();

		super.finish();
	}

}
