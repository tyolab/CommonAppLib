package au.com.tyo.app.ui.activity;

import au.com.tyo.app.CommonActivity;
import au.com.tyo.app.Controller;
import au.com.tyo.app.ui.page.PageSettings;

public class CommonActivitySettings extends CommonActivity {

    private static Class activitySettingsClass;
    private static Class pageSettingsClass;

    public static void setActivitySettingsClass(Class activitySettingsClass) {
        CommonActivitySettings.activitySettingsClass = activitySettingsClass;
    }

    public static Class getActivitySettingsClass() {
        if (null == activitySettingsClass)
            return CommonActivitySettings.class;
        return activitySettingsClass;
    }

    public static Class getPageSettingsClass() {
        if (null == pageSettingsClass)
            return PageSettings.class;
        return pageSettingsClass;
    }

    public static void setPageSettingsClass(Class pageSettingsClass) {
        CommonActivitySettings.pageSettingsClass = pageSettingsClass;
    }

    @Override
    protected void onCreatePage() {
        if (null == getPage())
            setPage(new PageSettings<>(getController(), this));
        super.onCreatePage();
    }

}
