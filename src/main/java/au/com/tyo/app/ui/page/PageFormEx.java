package au.com.tyo.app.ui.page;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import au.com.tyo.app.Controller;
import au.com.tyo.json.android.interfaces.CommonListener;

public class PageFormEx<T extends Controller> extends PageForm<T> {
    /**
     * @param controller
     * @param activity
     */
    public PageFormEx(T controller, Activity activity) {
        super(controller, activity);
    }

    @Override
    protected void onFormCheckFailed() {

    }

    @Override
    protected void saveFormData(Object form) {

    }

    @Override
    public void onFormClick(Context context, String key, String text) {

    }

    @Override
    public void onFieldClick(View v) {

    }

    @Override
    public void onFieldValueClear(String key) {

    }

    @Override
    public CommonListener getFormOnClickListenerByName(String listenerMethodStr) {
        return null;
    }

    @Override
    public CommonListener getFormOnClickListenerByKey(String key, String text) {
        return null;
    }
}
