/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD.
 *
 */

package au.com.tyo.app;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Configuration;
import android.view.WindowManager;

import au.com.tyo.android.CommonApplicationImpl;
import au.com.tyo.android.CommonInitializer;
import au.com.tyo.android.services.ImageDownloader;
import au.com.tyo.app.data.DisplayItem;
import au.com.tyo.app.data.ImagedSearchableItem;
import au.com.tyo.app.data.Searchable;
import au.com.tyo.app.ui.UI;
import au.com.tyo.app.ui.UIBase;
import au.com.tyo.app.R;

/**
 * @author Eric Tang <eric.tang@tyo.com.au>
 */

public class CommonApp extends CommonApplicationImpl implements Controller {
	
	/* Common App MVC, Settings */
	
	private UI ui;
	
	private CommonAppSettings settings;

	/* other stuff */
	
	protected InputManager inputManager;

	protected NetworkMonitor watchDog;
	
	protected List<String> queries;
	
	public CommonApp() {
		init();
	}
	
	public CommonApp(Context context) {
		super(context);
		init();
	}

	private void init() {
		if (instance == null)
			instance = this;
		
		if (this.mainActivityClass == null)
			setMainActivityClass(CommonActivity.class);
		
		if (this.splashScreenClass == null)
			setSplashScreenClass(SplashScreen.class);
		
		if (this.preferenceActivityClass == null)
			setPreferenceActivityClass(SettingsActivity.class);
		
		ui = null;
		
		inputManager = new InputManager();
	}

	@Override
	public UI getUi() {
		return ui;
	}
	
	@Override
	public void setUi(UI ui) {
		this.ui = ui;
	}

	@Override
	public void createUi() {
		if (ui == null) {
			
			/* Get UI Class Information */
			
			Class<?> clsUI = null; 
			boolean gotIt = false;
			
			String definedUiClassName = context.getResources().getString(R.string.ui_class);

			if (null != definedUiClassName && definedUiClassName.length() > 0) {
				String uiClassName = definedUiClassName.trim();
		
				gotIt = uiClassName != null && uiClassName.length() > 0;
				
				if (gotIt)
					try {
						clsUI = Class.forName(uiClassName);
					} catch (ClassNotFoundException e) {
						gotIt = false;
					}
			}
			else if (CommonInitializer.clsUi != null) 
				clsUI = CommonInitializer.clsUi;
			else 
				clsUI = UIBase.class;
			
			/* Get Controller Class Information */
			Class<?> clsController = (null == CommonInitializer.clsController) ? Controller.class : CommonInitializer.clsController; 
			
			try {
				Constructor ctor = clsUI.getConstructor(clsController/*Classes.clsController*/);
				ui = (UI) ctor.newInstance(new Object[] { clsController.cast(instance) });
	//			cls.newInstance();
			} catch (InstantiationException e) {
				gotIt = false;
			} catch (IllegalAccessException e) {
				gotIt = false;
			} catch (IllegalArgumentException e) {
				gotIt = false;
			} catch (InvocationTargetException e) {
				gotIt = false;
			} catch (NoSuchMethodException e) {
				gotIt = false;
			}
			
			if (null == ui)
				ui = new UIBase(this);
		}
	}
	
	public void setSettings(CommonAppSettings settings) {
		this.settings = settings;
	}
	
	@Override
	public CommonAppSettings getSettings() {
		return this.settings;
	}

	public InputManager getInputManager() {
		return inputManager;
	}

	public void setInputManager(InputManager inputManager) {
		this.inputManager = inputManager;
	}

	@Override
	public NotificationManager getNotificationManager() {
		return null;
	}

	@Override
	public void initializeInMainThread(Context context) {
		super.initializeInMainThread(context);
		
		if (settings == null)
			settings = new CommonAppSettings(context);
		
		watchDog = NetworkMonitor.getInstance(this);
		watchDog.start();
	}

	@Override
	public void initializeInBackgroundThread(Context context) {
		super.initializeInBackgroundThread(context);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		/*
		 * this can be called even the UI is not created yet
		 */
		if (ui != null)
			ui.onConfigurationChanged(newConfig);
	}

	@Override
	public void onUiReady() {
		ui.hideMainProgressBar();
	}

	@Override
	public ImageDownloader getImageDownloader() {
		return null;
	}

	@Override
	public NetworkMonitor getNetworkMonitor() {
		return watchDog;
	}

	@Override
	public String getTextForSearchResultItem(ImagedSearchableItem ws,
			String query) {
		if (ws.getSnippetHtml() == null)
			ws.buildSnippetHtml(query);
		return ws.getSnippetHtml();
	}
	
	@Override
	public List<?> getSuggesions(String query, boolean hasToBeBestMatch) {
		return getSuggesions(query, "", hasToBeBestMatch);
	}

	@Override
	public List<?> getSuggesions(String query, 	String extra, boolean hasToBeBestMatch) {
		return Arrays.asList(new String[] {"suggestion method is not implemented yet"});
	}

	@Override
	public DisplayItem getItemText(Searchable item) {
		return new DisplayItem(item.getTitle(), item.getSnippet());
	}

	@Override
	public String getTextForSearchResultItem(String text) {
		return text;
	}
	
	@Override
	public void displayHistory() {
	}

	@Override
	public void onSearchInputFocused() {
		getUi().setSuggestionViewVisibility(true);
		
		getActivityContext().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		getUi().hideAd();
	}

	@Override
	public void onSearchInputFocusEscaped() {
    	/*
    	 * we dont hide the suggestion view just if search input focus lose
    	 */
//    	getUI().setSuggestionViewVisibility(false);
    	getActivityContext().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    	getUi().showAd();
	}

	@Override
	public void loadHistory() {
	}

	@Override
	public void onHistoryItemClick(ImagedSearchableItem page, int fromHistory,
			boolean b) {
	}

	@Override
	public void search(String query) {
	}

	@Override
	public void open(Searchable item) {
	}

	@Override
	public void search(Searchable page, int fromHistory, boolean b) {
	}

	@Override
	public List<String> getQueryList() {
		return queries;
	}

	@Override
	public void onAppStart() {
	}
	
}
