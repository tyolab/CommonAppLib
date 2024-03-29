/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD.
 *
 */

package au.com.tyo.app;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Observable;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import au.com.tyo.android.AndroidUtils;
import au.com.tyo.android.CommonApplicationImpl;
import au.com.tyo.android.CommonInitializer;
import au.com.tyo.android.DialogFactory;
import au.com.tyo.android.NetworkMonitor;
import au.com.tyo.android.services.HttpAndroid;
import au.com.tyo.android.services.ImageDownloader;
import au.com.tyo.android.services.ServiceRunner;
import au.com.tyo.app.model.DisplayItem;
import au.com.tyo.app.model.ImagedSearchableItem;
import au.com.tyo.app.model.Searchable;
import au.com.tyo.app.ui.UI;
import au.com.tyo.app.ui.UIBase;
import au.com.tyo.app.ui.form.FormAbout;
import au.com.tyo.app.ui.page.Page;
import au.com.tyo.json.form.DataFormEx;
import au.com.tyo.json.form.FormGroup;
import au.com.tyo.services.HttpPool;
import au.com.tyo.utils.StringUtils;

import static au.com.tyo.app.Constants.MESSAGE_BROADCAST_BACKGROUND_TASK_DONE;
import static au.com.tyo.app.Constants.MESSAGE_BROADCAST_BACKGROUND_TASK_RESULT;
import static au.com.tyo.app.Constants.REQUEST_NONE;

/**
 * @author Eric Tang <eric.tang@tyo.com.au>
 */

public abstract class CommonApp<UIType extends UI,
								ControllerType extends Controller,
								SettingsType extends CommonAppSettings>
        extends CommonApplicationImpl<ControllerType>
        implements Controller<UIType>, ServiceRunner.MessageHandler {

	private static final String TAG = "CommonApp";
	
	/* Common App MVC, Settings */
	
	private UIType ui;
	
	private SettingsType settings;

	/* other stuff */
	
	protected InputManager inputManager;

	protected NetworkMonitor watchDog;
	
	protected List<String> queries;

	private boolean appQuit;

	private Object parcel;

	private Object result;

	private List<ThemeInfo> availableThemes;

	private boolean mainThreadInitialised;
	private boolean backgroundThreadInitialised;

	private FormAbout formAbout;

	/**
	 * Service Runner for data processing
	 */
	protected ServiceRunner dpServiceRunner = null;

    public CommonApp() {
		super();
	}

	public static class ThemeInfo {
		int themeId;
		String name;

        public ThemeInfo(int id, String string) {
            themeId = id;
            name = string;
        }
    }
	
	public CommonApp(Context context) {
		super(context);
		init();
	}

	private void init() {
		if (CommonInitializer.classMainActivity == null)
			setMainActivityClass(CommonAppCompatActivity.class);
		
		if (CommonInitializer.splashScreenClass == null)
			setSplashScreenClass(SplashScreen.class);
		
		if (CommonInitializer.preferenceActivityClass == null)
			setPreferenceActivityClass(SettingsActivity.class);
		
		ui = null;
		
		inputManager = new InputManager();

		setMainThreadInitialised(false);
		setBackgroundThreadInitialised(false);
	}

	@Override
	public boolean isMainThreadInitialised() {
		return mainThreadInitialised;
	}

	public void setMainThreadInitialised(boolean mainThreadInitialised) {
		this.mainThreadInitialised = mainThreadInitialised;
	}

	@Override
	public boolean isBackgroundThreadInitialised() {
		return backgroundThreadInitialised;
	}

	public void setBackgroundThreadInitialised(boolean backgroundThreadInitialised) {
		this.backgroundThreadInitialised = backgroundThreadInitialised;
	}

	public void setAvailableThemes(List availableThemes) {
		this.availableThemes = availableThemes;
	}

	@Override
	public Object getParcel() {
		return parcel;
	}

	@Override
	public void setParcel(Object parcel) {
		this.parcel = parcel;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	@Override
	public boolean isAppQuit() {
		return appQuit;
	}

	@Override
	public void setAppQuit(boolean appQuit) {
		this.appQuit = appQuit;
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
				case Constants.MESSAGE_BACKGROUND_TASK_FINISHED:
					onMessageBackgroundTaskEnded();
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
					onMessageCustomOne();
					break;
                case Constants.MESSAGE_CUSTOM_TWO:
                    onMessageCustomTwo();
                    break;
                case Constants.MESSAGE_CUSTOM_THREE:
                    onMessageCustomThree();
                    break;
                case Constants.MESSAGE_CUSTOM_FOUR:
                    onMessageCustomFour();
                    break;
                case Constants.MESSAGE_CUSTOM_FIVE:
                    onMessageCustomFive();
                    break;
                case Constants.MESSAGE_CUSTOM_SIX:
                    onMessageCustomSix();
                    break;
                case Constants.MESSAGE_CUSTOM_SEVEN:
                    onMessageCustomSeven();
                    break;
                case Constants.MESSAGE_CUSTOM_EIGHT:
                    onMessageCustomEight();
                    break;
                case Constants.MESSAGE_CUSTOM_NINE:
                    onMessageCustomNine();
                    break;
                case Constants.MESSAGE_CUSTOM_TEN:
                    onMessageCustomTen();
                    break;
                case Constants.MESSAGE_NETWORK_READY:
                    onMessageNetworkReady();
                    break;
				default:
					onMessage(msg);
					break;
			}
		}

		@Override
		public void dispatchMessage(Message msg) {
			super.dispatchMessage(msg);

		}

	}

    /**
     *
     */
    protected void onMessageNetworkReady() {
        // no ops yet
    }

    /**
	 * Other messages that need to be dealt with
	 *
	 * @param msg
	 */
	protected void onMessage(Message msg) {
		// no ops yet
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
	public void onMessageCustomThree() {

	}

    @Override
    public void onMessageCustomTwo() {

    }

    @Override
    public void onMessageCustomOne() {

    }

    @Override
    public void onMessageCustomFour() {

    }

    @Override
    public void onMessageCustomFive() {

    }

    @Override
    public void onMessageCustomSix() {

    }

    @Override
    public void onMessageCustomSeven() {

    }

    @Override
    public void onMessageCustomEight() {

    }

    @Override
    public void onMessageCustomNine() {

    }

    @Override
    public void onMessageCustomTen() {

    }

    @Override
    public void onMessageBackgroundTaskStageOne() {
    }

    @Override
    public void onMessageBackgroundTaskEnded() {
    }

    @Override
    public void onMessageBackgroundTaskStarted() {
    }

    @Override
	public UIType getUi() {
		return ui;
	}

	@Override
	public void setUi(UIType ui) {
		this.ui = ui;
	}

	@Override
	public void createUi() {
		setAppQuit(false);

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
				ui = (UIType) ctor.newInstance(new Object[] { clsController.cast(instance) });
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
				ui = (UIType) new UIBase(this);
		}
	}
	
	public void setSettings(SettingsType settings) {
		this.settings = settings;
	}
	
	@Override
	public SettingsType getSettings() {
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

	@OverridingMethodsMustInvokeSuper
	@Override
	public void initializeInMainThread(Context context) {
		super.initializeInMainThread(context);

		if (null == settings)
        	settings = (SettingsType) CommonInitializer.newSettings(context);

        /** maybe if the previous failed we are not creating the default one */
		if (settings == null)
			Log.w(TAG, "Warning: the settings instance is null");
			// settings = (SettingsType) new CommonAppSettings(context) {
			//
            // };
		
		watchDog = NetworkMonitor.getInstance(this);
		watchDog.start();

		this.setMessageHandler(new MessageHandler());

		setMainThreadInitialised(true);
	}

	@OverridingMethodsMustInvokeSuper
	@Override
	public void initializeInBackgroundThread(Context context) {
		super.initializeInBackgroundThread(context);

        if (null != settings) {
			settings.loadPreferences();
		}

		setBackgroundThreadInitialised(true);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		/*
		 * this can be called even the UI is not created yet
		 */
		if (ui != null)
			ui.getCurrentPage().onConfigurationChanged(newConfig);
	}

	@Override
	public void onUiReady() {
		ui.getCurrentPage().hideProgressBar();
	}

	@Override
	public void onUiCreated() {

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
	public List<?> getSuggestions(int requestFromId, String query, boolean hasToBeBestMatch) {
		return getSuggestions(requestFromId, query, hasToBeBestMatch, "");
	}

	@Override
	public List<?> getSuggestions(String query, boolean hasToBeBestMatch) {
		return getSuggestions(-1, query, hasToBeBestMatch, "");
	}

	@Override
	public List<?> getSuggestions(int fromId, String query, boolean hasToBeBestMatch, String extra) {
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
		getUi().getCurrentPage().setSuggestionViewVisibility(true);
		
		getCurrentActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		getUi().getCurrentPage().hideAd();
	}

	@Override
	public void onSearchInputFocusEscaped() {
    	/*
    	 * we dont hide the suggestion view just if search input focus lose
    	 */
//    	getUI().setSuggestionViewVisibility(false);
    	getCurrentActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    	getUi().getCurrentPage().showAd();
	}

	@Override
	public void loadHistory() {
	}

	@Override
	public void onHistoryItemClick(ImagedSearchableItem page, int fromHistory,
			boolean b) {
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
	public void onCurrentActivityStart() {

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
	public boolean onBackgroundTaskEnded(Activity activity) {
		return false;
	}

	@Override
	public void onBackgroundTaskStarted(Activity activity) {

	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		setAppQuit(true);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
	}

	public void sendMessage(Message msg) {
		getMessageHandler().sendMessage(msg);
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
		ui.setUiRecreationRequired(true);

		super.quitOrRestart(restart);
	}

	@Override
	public void restart() {
		quitOrRestart(true);
	}

	protected void checkLightDarkThemeUsage(int themeId) {
		// settings.updateThemePreference(themeId);

		/**
		 * @// TODO: 19/04/18
		 *
		 * use a simple approach, checking the class parent of instance may be a better solution
		 */
		String name = AndroidUtils.getApplicationThemeName(getContext());
		boolean usingLight = true;
		if (null != name) {
		    name = name.toLowerCase();
		    if (name.contains("dark"))
		        usingLight = false;
        }

		settings.setLightThemeInUse(usingLight);
	}

	protected void setThemeByIndex(int index) {
	    if (null == availableThemes)
	        return;

	    if (index < 0 || index >= availableThemes.size())
	        return;

		int themeId = availableThemes.get(index).themeId;

		int oldTheme = getSettings().getThemeId();
        getSettings().updateThemePreference(themeId);

		checkLightDarkThemeUsage(themeId);

		if (oldTheme != themeId)
			updateThemeNow(themeId);
	}

	public void updateTheme(int themeId) {
		Application application = null;
		if (null != getCurrentActivity())
			application = getCurrentActivity().getApplication();

		if (application != null) {
			int oldId = -1;
            // this the theme id defined in the manifest
            if (null != getCurrentActivity())
                oldId = AndroidUtils.getActivityThemeId(getCurrentActivity());

            if (-1 == oldId)
                oldId = AndroidUtils.getApplicationThemeId(context);

            if (oldId != themeId) {
				updateThemeNow(themeId);
			}
		}
	}

	public void updateThemeNow(int themeId) {
		application.setTheme(themeId); // set the application wise theme

		ui.setUiRecreationRequired(true);

		this.quitOrRestart(true);
	}

	protected void showConfirmQuitDialog() {
		Dialog dialog = DialogFactory.createExitPromptDialog(context, this.getAppName(),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						backKeyCount = 0;
						quitOrRestart(false);
					}

				}, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						backKeyCount = 0;
					}

				});
		getUi().showDialog(dialog);
	}

	protected void onBackKeyPressed() {
		if (!ui.onBackPressed())
			showConfirmQuitDialog();
	}

    @Override
    public boolean onSupportNavigateUp() {
        return false;
    }

	@Override
	public boolean onOptionsItemSelected(Activity activity, android.view.MenuItem item) {
		int itemId = item.getItemId();

		if (itemId == au.com.tyo.android.R.id.menuItemAbout) {
			getUi().showInfo(context.getResources().getBoolean(R.bool.showAcknowledgement)
					|| context.getResources().getString(R.string.app_acknowledgement).length() > 0);
			return true;
		}

		return false;
	}

	@Override
	public void initializeOnce() {
		initializeHttpConnectionPool();
	}

	protected void initializeHttpConnectionPool() {
        if (!HttpPool.hasSetHttpConnectionClass())
            HttpPool.setHttpConnectionClass(HttpAndroid.class);
        try {
            HttpPool.initialize();
        } catch (IllegalAccessException e) {
            Log.e(TAG, StringUtils.exceptionStackTraceToString(e));
        } catch (InstantiationException e) {
            Log.e(TAG, StringUtils.exceptionStackTraceToString(e));
        }
	}

	@Override
	public void onDeleteFromList(int listId, Object data) {
		// to be implemented if a simple list is used
	}

	public boolean onSuggestionItemClick(String listKey, int listId, Object obj) {
		// to be implemented if a simple list is used
		return false;
	}

    public void startActivity(Class cls, boolean mainActivity) {
		getUi().startActivity((Page) getUi().getCurrentPage(), cls, -1, null, null, null, REQUEST_NONE, mainActivity);
	}

	@Override
	public void startSplashScreenActivity(Context context) {
		getUi().startActivity(context, getSplashScreenClass(), false);
	}

	@Override
	public void startMainActivity() {
		Activity activity = getCurrentActivity();
		startActivity(getMainActivityClass(), true);
		if (activity != null)
			activity.finish();
	}

	@Override
	public void onTerminate() {

	}

    @Override
    public void broadcastMessage(int messageId, Object object) {
        Message message = new Message();
        message.what = messageId;
        message.obj = object;
        broadcastMessage(message);
    }

	@Override
	public void broadcastMessage(int messageId) {
        broadcastMessage(messageId, null);
	}

	@Override
	public void broadcastMessage(Message message) {
		broadcastMessage(Constants.DATA_MESSAGE_BROADCAST, message);
	}

	@Override
	public void broadcastMessage(String key, Object data) {
		Intent intent = new Intent(Constants.ACTION_MESSAGE_RECEIVER);
		CommonExtra.putExtra(intent, key, data);

		Context context = getContext();
		if (null == context)
			context = getApplicationContext();

		if (null != context)
			LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
		else
			CommonLog.e(this, "Empty context: BUG!!!!!!, check your logic, the App class wasn't finished initialisation with context yet");
	}

	@Override
	public void broadcastMessageBackgroundTaskProgress(int progress) {
		broadcastMessage(Constants.MESSAGE_BROADCAST_BACKGROUND_PROGRESS, progress);
	}

    @Override
    public void broadcastMessageBackgroundTaskDone() {
        broadcastMessage(Constants.MESSAGE_BROADCAST_BACKGROUND_TASK_DONE);
    }

	/**
	 *
	 * @param obj
	 */
	@Override
    public void onDataProcessingResultReceived(Object obj) {
        // override this, do things like stopping the DP service
    }

	/**
	 * For the background service task
	 *
	 * @param taskId
	 */
	@Override
	public void onBackgroundTaskFinished(int taskId) {
		// no op yet
		Log.i(TAG, "Background task handled by the service finished");
	}

	@Override
	public void bindDataFromOtherApps(Intent intent) {
		// Override me to process data passed by other Apps
		Log.i(TAG, "Receive data from other apps");
	}

	/**
	 * Searchable item set to requiresFurtherProcess()
	 *
	 * @param item
	 */
	@Override
	public void processSearchableItem(Searchable item) {
		Log.d(TAG, "Process data from other apps");
	}

	@OverridingMethodsMustInvokeSuper
	@Override
	public void onSettingsUpdated() {
		broadcastMessage(Constants.MESSAGE_BROADCAST_SETTINGS_UPDATED);
	}

	@Override
	public void search(String query) {
		// Override me to implement your own search function
	}

	@Override
	public ServiceRunner getDpServiceRunner() {
		return dpServiceRunner;
	}

    /**
     *
     *
     *
     * @param dpServiceRunner
     */
	public void installDataProcessingServiceRunner(ServiceRunner dpServiceRunner) {
		this.dpServiceRunner = dpServiceRunner;

		dpServiceRunner.setMessageHandler(this);
	}

    @Override
    public void handleMessageFromService(String serviceName, Message msg) {
        if (msg.what == Constants.MESSAGE_CLIENT_TASK_RESULT) {
            onDataProcessingResultReceived(msg.obj);
            broadcastMessage(MESSAGE_BROADCAST_BACKGROUND_TASK_RESULT, msg.obj);
        }
        else if (msg.what == Constants.MESSAGE_CLIENT_TASK_FINISHED) {
            broadcastMessage(MESSAGE_BROADCAST_BACKGROUND_TASK_DONE);
        }
    }

    @Override
    public FormAbout getFormAbout() {
        return formAbout;
    }

    public void setFormAbout(FormAbout formAbout) {
        this.formAbout = formAbout;
    }

	@Override
	public boolean onMultipleListItemsSelected(int listId, Collection selected) {
		return false;
	}
}
