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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;

import au.com.tyo.android.AndroidUtils;
import au.com.tyo.android.CommonApplicationImpl;
import au.com.tyo.android.CommonInitializer;
import au.com.tyo.android.DialogFactory;
import au.com.tyo.android.NetworkMonitor;
import au.com.tyo.android.services.ImageDownloader;
import au.com.tyo.app.model.DisplayItem;
import au.com.tyo.app.model.ImagedSearchableItem;
import au.com.tyo.app.model.Searchable;
import au.com.tyo.app.ui.UI;
import au.com.tyo.app.ui.UIBase;

/**
 * @author Eric Tang <eric.tang@tyo.com.au>
 */

public class CommonApp<UIType extends UI, ControllerType extends Controller>
        extends CommonApplicationImpl<ControllerType>
        implements Controller<UIType> {
	
	/* Common App MVC, Settings */
	
	private UIType ui;
	
	private CommonAppSettings settings;

	/* other stuff */
	
	protected InputManager inputManager;

	protected NetworkMonitor watchDog;
	
	protected List<String> queries;

	private boolean appQuit;

	private Object parcel;
	
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

	@Override
	public Object getParcel() {
		return parcel;
	}


	@Override
	public void setParcel(Object parcel) {
		this.parcel = parcel;
	}

	@Override
	public boolean isAppQuit() {
		return appQuit;
	}

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
				case Constants.MESSAGE_BACKGROUND_TASK_ENDDED:
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
                    onMessageCustomOne();
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
	public void setUi(UI ui) {
		this.ui = (UIType) ui;
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

        if (null != settings)
            settings.loadPreferences();
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

	protected void setThemeUsage(int themeId) {
		settings.updateThemePreference(themeId);

		settings.setLightThemeUsed(themeId == R.style.CommonAppTheme_Light);
	}

	protected void setThemeByIndex(int index) {

			int themeId = R.style.CommonAppTheme_Light;

			if (index == 0)
				themeId = R.style.CommonAppTheme_Light;
			else if (index == 1)
				themeId = R.style.CommonAppTheme_Dark;

			setThemeUsage(themeId);


		setThemeById(themeId);
	}

	public void setThemeById(int themeId) {
		Application application = null;
		if (null != getCurrentActivity())
			application = getCurrentActivity().getApplication();

		if (application != null) {
			int oldId = -1;
			try {
				oldId = AndroidUtils.getPredefinedApplicationThemeId(context);
				//oldId = AndroidUtils.getApplicationThemeId(context, application.getTheme());
			} catch (PackageManager.NameNotFoundException e) {

			}
			if (oldId != themeId) {
				application.setTheme(themeId); // set the application wise theme

				ui.setUiRecreationRequired(true);

				this.quitOrRestart(true);
			}
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

    @Override
	protected void showInfo(boolean showAcknowledgement) {
		// Inflate the about message contents
		View messageView = ((Activity) context).getLayoutInflater().inflate(au.com.tyo.android.R.layout.info_dialog, null, false);
		View acknowledgement = messageView.findViewById(au.com.tyo.android.R.id.acknowledge_view);
		if (showAcknowledgement) {
			acknowledgement.setVisibility(View.VISIBLE);

			if (null != acknowledgementTitle) {
				TextView tv = (TextView) acknowledgement.findViewById(au.com.tyo.android.R.id.tv_acknowledgement_title);
				tv.setText(acknowledgementTitle);
			}

			if (null != acknowledgementInfo) {
				TextView tv = (TextView) acknowledgement.findViewById(au.com.tyo.android.R.id.info_acknowledgement);
				tv.setText(acknowledgementInfo);
			}
		}


		String appDesc = getAppNameWithVersion();

		AlertDialog.Builder builder = DialogFactory.getBuilder(context, R.style.CommonAlertDialog_Light);
		builder.setIcon(logoResId);
		builder.setTitle(appDesc);
		builder.setView(messageView);
		Dialog dialog = builder.create();
		showDialog(dialog);
	}

	@Override
	public void initializeOnce() {
		// do nothing
	}

	@Override
	public void openUrl(String url) {
		Intent intent = new Intent();
		if (url.startsWith("mailto"))
			intent.setAction(Intent.ACTION_SENDTO);
		else
			intent.setAction(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(url));
		getCurrentActivity().startActivity(intent);
	}

	@Override
	public void onDeleteFromList(Object data) {
		// to be implemented if a simple list is used
	}

	@Override
	public void onListItemClick(Object obj) {
		// to be implemented if a simple list is used
	}
}
