package au.com.tyo.app.ui.page;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.util.Map;

import au.com.tyo.app.Controller;
import au.com.tyo.json.android.interfaces.CommonListener;
import au.com.tyo.json.util.DataFormEx;

public class PageFormEx<T extends Controller> extends PageForm<T> {

    private DataFormEx dataFormEx;

    private FormHandler formHandler;

    public interface FormHandler {

        void handleFormValidationFailure(String formId);

        void saveForm(String formId, Object form);

        void onClick(String formId, String key, String text);

        void onFieldClick(String formId, String key, String type);

        void onFieldValueClear(String formId, String key);

        void getFormOnClickListenerByName(String formId, String listenerMethodStr);

        void getFormOnClickListenerByKey(String formId, String key, String text);

        String getFormTitleFromKey(String key);
    }

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

    public FormHandler getFormHandler() {
        return formHandler;
    }

    public void setFormHandler(FormHandler formHandler) {
        this.formHandler = formHandler;
    }

    @Override
    public void onDataBound() {

        if (getForm() instanceof DataFormEx)
            dataFormEx = (DataFormEx) getForm();
        else {
            if (getForm() instanceof Map) {

            }
        }

        super.onDataBound();

        /**
         * Normally, we use controller to handle the form
         */
        if (formHandler == null && getController() instanceof FormHandler)
            setFormHandler((FormHandler) getController());
    }

    @Override
    protected void onFormCheckFailed() {
        if (null != formHandler)
            formHandler.handleFormValidationFailure(dataFormEx.getFormId());
    }

    @Override
    protected void saveFormData(Object form) {
        if (null != formHandler)
            formHandler.saveForm(dataFormEx.getFormId(), form);
    }

    @Override
    public void onFormClick(Context context, String key, String text) {
        if (null != formHandler)
            formHandler.onClick(dataFormEx.getFormId(), key, text);
    }


    @Override
    public void onFieldClick(String key, String type) {
        if (null != formHandler)
            formHandler.onFieldClick(dataFormEx.getFormId(), key, type);
    }

    @Override
    public void onFieldValueClear(String key) {
        if (null != formHandler)
            formHandler.onFieldValueClear(dataFormEx.getFormId(), key);
    }

    @Override
    public CommonListener getFormOnClickListenerByName(String listenerMethodStr) {
        if (null != formHandler)
            formHandler.getFormOnClickListenerByName(dataFormEx.getFormId(), listenerMethodStr);
        return null;
    }

    @Override
    public CommonListener getFormOnClickListenerByKey(String key, String text) {
        if (null != formHandler)
            formHandler.getFormOnClickListenerByKey(dataFormEx.getFormId(), key, text);
        return null;
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {

        return super.onActivityResult(requestCode, resultCode, data);
    }


}
