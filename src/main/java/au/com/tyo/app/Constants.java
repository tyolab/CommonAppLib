/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD.
 *
 */

package au.com.tyo.app;

import au.com.tyo.json.android.constants.JsonFormConstants;
import au.com.tyo.json.form.DataFormEx;

/**
 * @author Eric Tang <eric.tang@tyo.com.au>
 */

public interface Constants extends au.com.tyo.android.Constants {

	/**
	 * Message Constants
	 */
	int MESSAGE_SUGGESTION_RETURN = 10991;

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

	String DATA_MESSAGE_BROADCAST = "TYODROID_MESSAGE_BROADCAST";

	int MESSAGE_BROADCAST_LOADING_DATA = 77789;
	int MESSAGE_BROADCAST_DATA_LOADED = 77790;
	int MESSAGE_BROADCAST_APP_EXIT = 77791;
	int MESSAGE_BROADCAST_BACKGROUND_PROGRESS = 77792;
	int MESSAGE_BROADCAST_BACKGROUND_TASK_RESULT = 77793;
    int MESSAGE_BROADCAST_BACKGROUND_TASK_DONE = 77794;
	int MESSAGE_BROADCAST_BACKGROUND_PAGE_HIDING = 77794;
	int MESSAGE_BROADCAST_SETTINGS_UPDATED = 77795;

	int MESSAGE_BROADCAST_FORM_VALUE_UPDATED = 77796;
	int MESSAGE_BROADCAST_FORM_VALUE_UPDATED_WITH_VALUE = 77797;

    String RESULT_CODE = "TYODROID_RESULT_CODE";

	int REQUEST_CODE = 999;
	int REQUEST_SOMETHING = 9999;
    int REQUEST_NONE = -1;
	int REQUEST_PICK = 1009;
	int REQUEST_FORM_FILLING = JsonFormConstants.REQUEST_FORM_FILLING;

	int REQUEST_CODE_DP_RESULT = 7000;

    String EXTRA_KEY_JSON = "TYODROID_EXTRA_KEY_JSON";
	String EXTRA_KEY_FORM_ID = "TYODROID_EXTRA_KEY_FORM_ID";

	String EXTRA_KEY_EDITABLE = DataFormEx.KEY_EDITABLE;
    String EXTRA_KEY_TITLE = DataFormEx.KEY_TITLE;

	String ACTION_MESSAGE_RECEIVER = "au.com.tyo.app.CommonData";

    String DATA_LIST_QUICK_ACCESS_TITLE = "TYODROID_DATA_LIST_QUICK_ACCESS_TITLE";
	String DATA_LIST_QUICK_ACCESS_LIST = "TYODROID_DATA_LIST_QUICK_ACCESS_LIST";
	String DATA_LIST_FULL_LIST_TITLE = "TYODROID_DATA_LIST_FULL_LIST_TITLE";
	String DATA_LIST_FULL_LIST_DATA = "TYODROID_DATA_LIST_FULL_LIST_DATA";
    String DATA_LIST_SELECTED = "TYODROID_DATA_LIST_SELECTED";
    String DATA_LIST_KEY = "TYODROID_DATA_LIST_KEY";
    String DATA_LIST_ID = "TYODROID_DATA_LIST_ID";
	String DATA_LIST_ALLOW_MULTIPLE_SELECTIONS = "TYODROID_DATA_LIST_ALLOW_MULTIPLE_SELECTIONS";

	String DATA_SHOW_SEARCH = "TYODROID_DATA_SHOW_SEARCH";

	String PAGE_RESULT_KEY = "TYODROID_PAGE_RESULT_KEY";

    String DATA_MIME_TYPE = "TYODROID_DATA_MIME_TYPE";
}
