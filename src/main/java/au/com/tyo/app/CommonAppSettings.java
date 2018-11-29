/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD.
 *
 */

package au.com.tyo.app;

import android.content.Context;

import java.util.Map;

import au.com.tyo.android.AndroidSettings;
import au.com.tyo.app.api.JSON;
import au.com.tyo.json.util.DataJson;

/**
 * @author Eric Tang <eric.tang@tyo.com.au>
 */

public abstract class CommonAppSettings<T1 extends Map, T2 extends Map> extends AndroidSettings {
	
	public static final String PREF_SHOW_SEARCH_BAR = "pref_show_search_bar";

    /**
     * App Data in json string format saved in system built-in shared preference
     */
	public static final String PREF_APP_DATA = "pref_app_data";

    /**
     * App Settings in json string format saved in system built-in shared preference
     */
	public static final String PREF_APP_SETTINGS = "pref_app_settings";
	
	protected boolean alwaysShowSearchBar;

	protected T2 appSettings;
	protected T1 appData;

	public CommonAppSettings(Context context) {
		super(context);
		
		alwaysShowSearchBar = true;

        /**
         * Load App Settings in a background thread such as where the the splash screen is showed
         *
         * loadAppData();
         * loadAppSettings();
         */
	}
	
	/**
	 * leave show search bar by default to the specific App
	 * 
	 * @param b
	 */
	protected void loadShowSearchBarPreference(boolean b) {
		alwaysShowSearchBar = prefs.getBoolean(PREF_SHOW_SEARCH_BAR, b);
	}
	
	public void setShowSearchBar(boolean b) {
		updatePreference(PREF_SHOW_SEARCH_BAR, b);
		
		alwaysShowSearchBar = b;
	}

	/**
	 * The app settings saved in preferences
	 */
	public void loadAppSettings(Class<? extends T2> aClass) {
        appSettings = JSON.parse(prefs.getString(PREF_APP_SETTINGS, "{}"), aClass);

        loadSettingsIntoMemory();
	}

	/**
	 * The app data saved in preferences
	 */
	public void loadAppData(Class<? extends T1> aClass) {
        appData = JSON.parse(prefs.getString(PREF_APP_DATA, "{}"), aClass);
	}

	public void saveAppData() {
        updatePreference(PREF_APP_DATA, JSON.toJson(appData));
    }

    public void save() {
        updatePreference(PREF_APP_SETTINGS, JSON.toJson(appSettings));
    }

	public boolean toShowSearchBar() {
		return alwaysShowSearchBar;
	}

    public T2 getAppSettings() {
        return appSettings;
    }

    public void setAppSettings(T2 appSettings) {
        this.appSettings = appSettings;
    }

    public T1 getAppData() {
        return appData;
    }

    public void setAppData(T1 appData) {
        this.appData = appData;
    }

    /**
     * Specify each setting from the map
     */
    public void loadSettingsIntoMemory() {
        // no ops
    }

    public void updateSetting(String key, Object value) {
    	appSettings.put(key, value);
	}
}
