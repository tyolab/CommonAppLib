package au.com.tyo.app.ui.page;

import android.app.Activity;

import au.com.tyo.app.Controller;

public class PageAbout<T extends Controller> extends PageFormEx<T> {

    /**
     * @param controller
     * @param activity
     */
    public PageAbout(T controller, Activity activity) {
        super(controller, activity);

        setEditable(false);
        setMenuEditRequired(false);
    }

}
