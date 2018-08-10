/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD.
 *
 */

package au.com.tyo.app;

/**
 * @author Eric Tang <eric.tang@tyo.com.au>
 */

public interface Constants extends au.com.tyo.android.Constants {

	/**
	 * Message Constants
	 */
	int MESSAGE_SUGGESTION_RETURN = 99;

	int MESSAGE_BACKGROUND_TASK_STARTED = 0;

	int MESSAGE_BACKGROUND_TASK_FINISHED = 1;

	int MESSAGE_BACKGROUND_TASK_STAGE_ONE = 11;
	int MESSAGE_BACKGROUND_TASK_STAGE_TWO = 12;
	int MESSAGE_BACKGROUND_TASK_STAGE_THREE = 13;
	int MESSAGE_BACKGROUND_TASK_STAGE_FOUR = 14;
	int MESSAGE_BACKGROUND_TASK_STAGE_FIVE = 15;
	int MESSAGE_BACKGROUND_TASK_STAGE_SIX = 16;

	int MESSAGE_CUSTOM_ONE = 21;
	int MESSAGE_CUSTOM_TWO = 22;
	int MESSAGE_CUSTOM_THREE = 23;
	int MESSAGE_CUSTOM_FOUR = 24;
	int MESSAGE_CUSTOM_FIVE = 25;
	int MESSAGE_CUSTOM_SIX = 26;
	int MESSAGE_CUSTOM_SEVEN = 27;
	int MESSAGE_CUSTOM_EIGHT = 28;
	int MESSAGE_CUSTOM_NINE = 29;
	int MESSAGE_CUSTOM_TEN = 30;

	int MESSAGE_CUSTOM = 100;

	// SPLASH SCREEN
	int MESSAGE_AD_LOADED = 99;

	int MESSAGE_AD_FAILED = -1;

	int MESSAGE_AD_TIMEUP = -2;

	int MESSAGE_APP_INITIALIZED = 1000;

	/**
	 * Activity Communication
	 */
    String RESULT_LOCATION = "TYODROID_RESULT_LOCATION";
    String RESULT_LOCATION_CONTROLLER = "TYODROID_RESULT_LOCATION_CONTROLLER";
    String DATA_LOCATION_CONTROLLER = "TRYODROID_DATA_LOCATION_CONTROLLER";

    String PAGE_TITLE = "TYODROID_PAGE_TITLE";
	String PAGE_REQUEST_CODE = "TYODROID_PAGE_REQUEST_CODE";

	String PAGE_TOOLBAR_COLOR = "TYODROID_PAGE_TOOLBAR_COLOR";
	String PAGE_STATUSBAR_COLOR = "TYODROID_PAGE_STATUSBAR_COLOR";
	String PAGE_BACKGROUND_COLOR = "TYODROID_PAGE_BACKGROUND_COLOR";
	String PAGE_TITLE_FOREGROUND_COLOR = "TYODROID_PAGE_TITLE_FOREGROUND_COLOR";
	String PAGE_IS_MAIN = "TYODROID_PAGE_IS_MAIN";

    String DATA_LIST = "TYODROID_DATA_LIST";
    String DATA_ASSETS_PATH = "TYODROID_ASSETS_PATH";

	String MESSAGE_BROADCAST = "TYODROID_MESSAGE_BROADCAST";

	int MESSAGE_BROADCAST_LOADING_DATA = 77789;
	int MESSAGE_BROADCAST_DATA_LOADED = 77790;
	int MESSAGE_BROADCAST_APP_EXIT = 77791;

    String RESULT_CODE = "TYODROID_RESULT_CODE";

	int REQUEST_CODE = 999;
	int REQUEST_SOMETHING = 9999;
    int REQUEST_NONE = -1;
	int REQUEST_PICK = 1;

    String EXIT_APP = "TYODROID_EXIT_APP";
    String RELOAD = "TYODROID_RESTART";

    String EXTRA_KEY_JSON = "TYODROID_EXTRA_KEY_JSON";
	String EXTRA_KEY_EDITABLE = "TYODROID_EXTRA_KEY_EDITABLE";

	String ACTION_MESSAGE_RECEIVER = "au.com.tyo.app.CommonData";

}
