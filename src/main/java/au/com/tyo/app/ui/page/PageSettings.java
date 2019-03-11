package au.com.tyo.app.ui.page;

import android.app.Activity;

import java.util.Map;

import au.com.tyo.app.CommonAppSettings;
import au.com.tyo.app.Controller;

public class PageSettings <T extends Controller> extends PageFormEx<T> {

    public static final String FORM_SETTINGS_ID = "settings";

    private CommonAppSettings settings;

    /**
     * @param controller
     * @param activity
     */
    public PageSettings(T controller, Activity activity) {
        super(controller, activity);

        settings = controller.getSettings();
        setEditable(true);
        setMenuEditRequired(false);
    }

    @Override
    public void onDataBound() {
        if (null == getForm()) {
            setForm(settings.getSettingsCache());
            setEditable(true);
        }

        super.onDataBound();
    }

    @Override
    protected void saveFormData(Object form) {
        // reload settings because each setting is stored separately in different variable
        settings.loadSettingsIntoMemory();

        new Thread(new Runnable() {
            @Override
            public void run() {
                getController().onSettingsUpdated();
                settings.commit();
            }
        }).start();
    }

    @Override
    protected void onFieldDataDirty(String key, String childKey, Object value) {
        super.onFieldDataDirty(key, childKey, value);

        if (null == childKey)
            settings.updateSetting(key, value);
        else {
            Map subsettings = (Map) settings.getSettingsCache().get(key);
            subsettings.put(childKey, value);
        }
    }
}
