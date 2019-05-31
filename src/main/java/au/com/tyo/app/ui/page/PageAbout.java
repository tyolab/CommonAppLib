package au.com.tyo.app.ui.page;

import android.app.Activity;
import android.content.Context;

import au.com.tyo.app.Controller;
import au.com.tyo.app.ui.UI;

import static au.com.tyo.app.ui.form.FormAbout.FORM_KEY_EMAIL;
import static au.com.tyo.app.ui.form.FormAbout.FORM_KEY_WEBSITE;

public class PageAbout<T extends Controller> extends PageFormEx<T> {

    /**
     * @param controller
     * @param activity
     */
    public PageAbout(T controller, Activity activity) {
        super(controller, activity);

        setEditable(false);
        setMenuEditRequired(false);

        setDataFormEx(controller.getFormAbout());
    }

    @Override
    public void onFormClick(Context context, String key, String text) {
        super.onFormClick(context, key, text);
    }

    @Override
    public void onFieldClick(String key, String type) {

        UI ui = getController().getUi();
        Object value = getFormFieldValueFromMetaData(key);

        if (value instanceof String) {
            String url = value.toString();

            if (key.equals(FORM_KEY_WEBSITE)) {
                ui.openUrl(url);
            }
            else if (key.equals(FORM_KEY_EMAIL)) {
                ui.openUrl("mailto:" + url + "?subject=");
            }
            else if (url.startsWith("http"))
                ui.openUrl(url);
        }
        super.onFieldClick(key, type);
    }
}
