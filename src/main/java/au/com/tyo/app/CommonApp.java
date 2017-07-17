/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD.
 *
 */

package au.com.tyo.app;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.WindowManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;

import au.com.tyo.android.CommonApplicationImpl;
import au.com.tyo.android.CommonInitializer;
import au.com.tyo.android.NetworkMonitor;
import au.com.tyo.android.services.ImageDownloader;
import au.com.tyo.app.data.DisplayItem;
import au.com.tyo.app.data.ImagedSearchableItem;
import au.com.tyo.app.data.Searchable;
import au.com.tyo.app.ui.UI;
import au.com.tyo.app.ui.UIBase;

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
	
	public CommonApp(Context context) {
		super(context);
		init();
	}

	private void init() {
		if (instance == null)
			instance = this;
		
		if (CommonInitializer.mainActivityClass == null)
			setMainActivityClass(CommonAppCompatActivity.class);
		
		if (CommonInitializer.splashScreenClass == null)
			setSplashScreenClass(SplashScreen.class);
		
		if (CommonInitializer.preferenceActivityClass == null)
			setPreferenceActivityClass(SettingsActivity.class);
		
		ui = null;
		
		inputManager = new InputManager();
	}

	/**
	 * Messages are used between threads
	 */
	private class MessageHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			/* could get call from background service */
			if (null == ui)
				return;

			switch (msg.what) {
				case Constants.MESSAGE_BACKGROUND_TASK_STARTED:
					onMessageBackgroundTaskStarted();
					break;
				case Constants.MESSAGE_BACKGROUND_TASK_ENDDED:
					onMessageBackgroundTaskEndded();
					break;
				case Constants.MESSAGE_BACKGROUND_TASK_STAGE_ONE:
					onMessageBackgroundTaskStageOne();
					break;
				case Constants.MESSAGE_BACKGROUND_TASK_STAGE_TWO:
					onMessageBackgroundTaskStageTwo();
					break;
				case Constants.MESSAGE_BACKGROUND_TASK_STAGE_THREE:
					onMessageBackgroundTaskStageThree();
					break;
				case Constants.MESSAGE_BACKGROUND_TASK_STAGE_FOUR:
					onMessageBackgroundTaskStageFour();
					break;
				case Constants.MESSAGE_BACKGROUND_TASK_STAGE_FIVE:
					onMessageBackgroundTaskStageFive();
					break;
				case Constants.MESSAGE_BACKGROUND_TASK_STAGE_SIX:
					onMessageBackgroundTaskStageSix();
					break;
				case Constants.MESSAGE_CUSTOM_ONE:
					onMessageCustomeOne();
					break;
                case Constants.MESSAGE_CUSTOM_TWO:
                    onMessageCustomeTwo();
                    break;
                case Constants.MESSAGE_CUSTOM_THREE:
                    onMessageCustomeOne();
                    break;
                case Constants.MESSAGE_CUSTOM_FOUR:
                    onMessageCustomeFour();
                    break;
                case Constants.MESSAGE_CUSTOM_FIVE:
                    onMessageCustomeFive();
                    break;
                case Constants.MESSAGE_CUSTOM_SIX:
                    onMessageCustomeSix();
                    break;
                case Constants.MESSAGE_CUSTOM_SEVEN:
                    onMessageCustomeSeven();
                    break;
                case Constants.MESSAGE_CUSTOM_EIGHT:
                    onMessageCustomeEight();
                    break;
                case Constants.MESSAGE_CUSTOM_NINE:
                    onMessageCustomeNine();
                    break;
                case Constants.MESSAGE_CUSTOM_TEN:
                    onMessageCustomeTen();
                    break;
			}
		}

		@Override
		public void dispatchMessage(Message msg) {
			super.dispatchMessage(msg);

		}

	}

	@Override
    public void onMessageBackgroundTaskStageTwo() {

    }

    @Override
    public  void onMessageBackgroundTaskStageThree() {

    }

    @Override
    public void onMessageBackgroundTaskStageFour() {

    }

    @Override
    public void onMessageBackgroundTaskStageFive() {

    }

    @Override
    public void onMessageBackgroundTaskStageSix() {

    }

    @Override
    public void onMessageCustomeTwo() {

    }

    @Override
    public void onMessageCustomeOne() {

    }

    @Override
    public void onMessageCustomeFour() {

    }

    @Override
    public void onMessageCustomeFive() {

    }

    @Override
    public void onMessageCustomeSix() {

    }

    @Override
    public void onMessageCustomeSeven() {

    }

    @Override
    public void onMessageCustomeEight() {

    }

    @Override
    public void onMessageCustomeNine() {

    }

    @Override
    public void onMessageCustomeTen() {

    }

    @Override
    public void onMessageBackgroundTaskStageOne() {
    }

    @Override
    public void onMessageBackgroundTaskEndded() {
    }

    @Override
    public void onMessageBackgroundTaskStarted() {
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

			if (null == clsUI && CommonInitializer.clsUi != null)
				clsUI = CommonInitializer.clsUi;

			if (null == clsUI)
				clsUI = UIBase.class;
			
			/* Get Controller Interface Information */
			Class<?> clsController = (null == CommonInitializer.clsControllerInterface) ? Controller.class : CommonInitializer.clsControllerInterface;
			
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

        settings = (CommonAppSettings) CommonInitializer.newSettings(context);

		if (settings == null)
			settings = new CommonAppSettings(context);
		
		watchDog = NetworkMonitor.getInstance(this);
		watchDog.start();

		this.setMessageHandler(new MessageHandler());
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
		ui.hideProgressBar();
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
	public List<?> getSuggestions(String query, boolean hasToBeBestMatch) {
		return getSuggestions(query, "", hasToBeBestMatch);
	}

	@Override
	public List<?> getSuggestions(String query, String extra, boolean hasToBeBestMatch) {
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
		
		getCurrentActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		getUi().hideAd();
	}

	@Override
	public void onSearchInputFocusEscaped() {
    	/*
    	 * we dont hide the suggestion view just if search input focus lose
    	 */
//    	getUI().setSuggestionViewVisibility(false);
    	getCurrentActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
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
	public void onOpenSearchItemClicked(Searchable item) {
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
        ui.onAppStart();
	}

	@Override
	public void onPrepareOptionsMenu(Object actionBar, Menu menu) {
		if (null != ui.getActionBarMenu())
			ui.getActionBarMenu().initializeMenuForActionBar(actionBar, menu);
	}

	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		return false;
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
	}

	@Override
	public void update(Observable observable, Object data) {
	}

	@Override
	public void onResume() {

	}

	@Override
	public void onDestroy() {
	}

	@Override
	public void onPause() {
        if (null != ui)
		    ui.onPause(this.currentActivity);
	}

	@Override
	public void onStop() {
		if (null != ui)
			ui.onStop(this.currentActivity);
	}

	@Override
	public boolean onBackgroundTaskEndded(Activity activity) {
		return false;
	}

	@Override
	public void onBackgroundTaskStarted(Activity activity) {

	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	}

	@Override
	public void onWidowReady() {
		ui.onWidowReady();
	}

	@Override
	public void quitOrRestart(boolean restart) {
		/**
		 * Need to recreate UI if it got started again as the activity finishes
         *
         * ui = null;
         *
         * it seems the above will affect the onPause call
		 */
		ui.setUiRecreationRequierd(true);


		super.quitOrRestart(restart);
	}

	@Override
	public void restart() {
		quitOrRestart(true);
	}

	protected void setThemeUsage(int themeId) {
		settings.updateThemePreference(themeId);

		settings.setLightThemeUsed(themeId == R.style.AppTheme_Light);
	}

	protected void setThemeByIndex(int index) {

		Application application = null;
		if (null != getCurrentActivity())
			application = getCurrentActivity().getApplication();

		if (application != null) {

			int themeId = R.style.AppTheme_Light;

			if (index == 0)
				themeId = R.style.AppTheme_Light;
			else if (index == 1)
				themeId = R.style.AppTheme_Dark;

			setThemeUsage(themeId);

			application.setTheme(themeId); // set the application wise theme

			ui.setUiRecreationRequierd(true);

			this.quitOrRestart(true);
		}
	}

	@Override
	protected void onBackKeyPressed() {
		if (!ui.onBackPressed())
			super.onBackKeyPressed();
	}

    @Override
    public boolean onSupportNavigateUp() {
        return false;
    }
}
