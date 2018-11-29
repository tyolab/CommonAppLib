package au.com.tyo.app.ui.page;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import au.com.tyo.app.Controller;
import au.com.tyo.json.android.interfaces.CommonListener;
import au.com.tyo.json.util.DataFormEx;

public class PageFormEx<T extends Controller> extends PageForm<T> {

    private DataFormEx dataFormEx;

    /**
     * @param controller
     * @param activity
     */
    public PageFormEx(T controller, Activity activity) {
        super(controller, activity);
    }

    public DataFormEx getDataFormEx() {
        return dataFormEx;
    }

    public void setDataFormEx(DataFormEx dataFormEx) {
        this.dataFormEx = dataFormEx;
    }

    @Override
    public void onDataBound() {

        if (getForm() instanceof DataFormEx)
            dataFormEx = (DataFormEx) getForm();

        super.onDataBound();
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
    public void onFieldClick(String key, String type) {

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

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {

        return super.onActivityResult(requestCode, resultCode, data);
    }
}
