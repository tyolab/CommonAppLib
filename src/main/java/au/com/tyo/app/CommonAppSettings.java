/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD.
 *
 */

package au.com.tyo.app;

import android.Manifest;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import au.com.tyo.android.AndroidSettings;
import au.com.tyo.app.api.JSON;
import au.com.tyo.json.form.DataJson;

/**
 * @author Eric Tang <eric.tang@tyo.com.au>
 */

public abstract class CommonAppSettings<T1 extends Map, T2 extends Map> extends AndroidSettings {

    private static final String LOG_TAG = "CommonAppSettings";
	
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

    protected Class<T2> appSettingsClass;
    protected Class<T1> appDataClass;

	protected T2 appSettings;
	protected T1 appData;

	/**
	 * Collection of permissions granted
	 */
	private Set<String> permissionsGranted;

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
	public void loadAppSettings(Class<T2> aClass) {
		appSettingsClass = aClass;
        appSettings = JSON.parse(prefs.getString(PREF_APP_SETTINGS, "{}"), aClass);

        loadSettingsIntoMemory();
	}

	/**
	 * The app data saved in preferences
	 */
	public void loadAppData(Class<T1> aClass) {
		appDataClass = aClass;
        appData = JSON.parse(prefs.getString(PREF_APP_DATA, "{}"), aClass);
	}

	public void saveAppData() {
        updatePreference(PREF_APP_DATA, JSON.toJson(appData));
    }

    public void commit() {
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
    	if (null == appSettings)
    		try {
				appSettings = appSettingsClass.newInstance();
			}
    		catch (Exception ex) {
    			Log.e(LOG_TAG, "appSettings is null, and failed to create an instance for it", ex);
			}
    	appSettings.put(key, value);
	}

	public boolean hasPermission(String permission) {
		return null != permissionsGranted && permissionsGranted.contains(permission);
	}
	
	public void grantPermission(String permission) {
		if (permissionsGranted == null)
			permissionsGranted = new HashSet();
		permissionsGranted.add(permission);
	}

	public boolean hasStorageWritePermission() {
        return null != permissionsGranted && permissionsGranted.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @Override
    public String getAppDataSubPath(String subPath) {
        String path = super.getAppDataSubPath(subPath);
        File file = new File(path);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e(LOG_TAG, "Failed to create directory: " + path);

                if (!hasStorageWritePermission())
                    Log.w(LOG_TAG, "Please check if the write permission on the device storage is requested and granted.");
            }
        }
        return path;
    }
}
