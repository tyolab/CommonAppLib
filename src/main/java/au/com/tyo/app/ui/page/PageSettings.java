package au.com.tyo.app.ui.page;

import android.app.Activity;

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
            setForm(settings.getAppSettings());
            setEditable(true);
        }

        super.onDataBound();
    }

    @Override
    protected void saveFormData(Object form) {
        settings.loadSettingsIntoMemory();

        new Thread(new Runnable() {
            @Override
            public void run() {
                settings.save();
            }
        }).start();
    }

}
