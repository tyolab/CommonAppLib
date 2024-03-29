package au.com.tyo.app.ui.page;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import java.util.List;
import java.util.Map;

import au.com.tyo.app.CommonLog;
import au.com.tyo.app.Constants;
import au.com.tyo.app.Controller;
import au.com.tyo.app.R;
import au.com.tyo.app.api.JSON;
import au.com.tyo.json.android.interfaces.CommonListener;
import au.com.tyo.json.android.interfaces.FieldItem;
import au.com.tyo.json.android.interfaces.JsonApi;
import au.com.tyo.json.android.utils.FormHelper;
import au.com.tyo.json.form.DataFormEx;
import au.com.tyo.json.form.FormField;
import au.com.tyo.json.form.FormGroup;
import au.com.tyo.json.util.OrderedDataMap;

public class PageFormEx<T extends Controller> extends PageForm<T> {

    private DataFormEx dataFormEx;

    private FormHandler formHandler;

    private String formId;

    public interface FormHandler {

        void attachAPI(String formId, JsonApi api);

        void handleFormValidationFailure(String formId);

        void saveForm(String formId, Object form);

        void onClick(String formId, String key, String text);

        void onFieldClick(String formId, String key, String type);

        void onFieldValueClear(String formId, String key);

        void getFormOnClickListenerByName(String formId, String listenerMethodStr);

        void getFormOnClickListenerByKey(String formId, String key, String text);

        String getFormTitleByKey(String key);

        void initializeForm(String formId, DataFormEx dataFormEx);

        String toKey(String formId, String key);

        String toTitle(String formId, String key);

        DataFormEx getForm(String formId);

        void onFormResume(String formId);

        void onValueUpdated(String formId, String key, Object value);

        void setupFormHeader(String formId, View view);

        void setupFormFooter(String formId, View view);

        void onFormInitialized(String formId);

        boolean onReceiveBroadcastMessage(String formId, int messageId, Object msgObj);

        // boolean validate(String key, String text);
        //
        // boolean hasValidator(String keyStr);

        // void installValidator(String keyStr, EditText editText);
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

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public FormHandler getFormHandler() {
        return formHandler;
    }

    public void setFormHandler(FormHandler formHandler) {
        this.formHandler = formHandler;
    }

    @Override
    public void bindData(Intent intent) {
        super.bindData(intent);

        if (intent.hasExtra(Constants.EXTRA_KEY_FORM_ID))
            formId = intent.getStringExtra(Constants.EXTRA_KEY_FORM_ID);
    }

    @Override
    public void onDataBound() {

        /**
         * Normally, we use controller to handle the form
         */
        if (formHandler == null && null != getController().getFormHandler())
            setFormHandler(getController().getFormHandler());

        if (null == dataFormEx) {
            if (null != formHandler && null != formId)
                dataFormEx = formHandler.getForm(formId);

            if (null == dataFormEx) {
                if (null != getForm()) {
                    // if we can get the form which means we don't need to do the initialization
                    if (getForm() instanceof DataFormEx)
                        dataFormEx = (DataFormEx) getForm();
                    else if (getForm() instanceof OrderedDataMap) {
                        dataFormEx = new DataFormEx((OrderedDataMap) getForm());
                    } else if (getForm() instanceof Map) {
                        dataFormEx = new DataFormEx();
                        dataFormEx.putAll((Map<? extends String, ?>) getForm());
                    }
                }
                else {
                    if (null == formId)
                        throw new IllegalStateException(getClass().getName() + ": Both form id and data form object are not set.");
                    else {
                        dataFormEx = new DataFormEx();
                    }
                }
            }

            dataFormEx.setFormId(formId);
        }
        else {
            if (null != formId)
                dataFormEx.setFormId(formId);
        }

        /**
         * If form has built-in initialization
         */
        dataFormEx.setInitialized(false);
        dataFormEx.initializeForm();

        /**
         * In case, the form needs to be initialized in the form handler
         */
        if (null != formHandler) {
            formHandler.attachAPI(formId, this);
            formHandler.initializeForm(formId, dataFormEx);
        }

        if (!dataFormEx.hasFooter())
            dataFormEx.setFooter(R.layout.form_footer);
        if (!dataFormEx.hasHeader())
            dataFormEx.setHeader(R.layout.form_header);

        if (null != dataFormEx.getFormData())
            setForm(dataFormEx.getFormData());
        else
            setForm(dataFormEx);

        super.onDataBound();

        if (null != formHandler)
            formHandler.onFormInitialized(formId);
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
            formHandler.onFieldClick(getFormId() != null ? getFormId() : dataFormEx.getFormId(), key, type);
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

    // don't need this
    // we have no problems on how title gets converted to key
    // @Override
    // public String toKey(String key) {
    //     if (null != formHandler)
    //         return formHandler.toKey(dataFormEx.getFormId(), key);
    //     return super.toKey(key);
    // }

    @Override
    public String toTitle(String key) {
        String title = null;
        if (null != formHandler) {
            title = formHandler.toTitle(dataFormEx.getFormId(), key);
        }
        if (null == title)
            title = super.toTitle(key);
        return title;
    }

    @Override
    public void loadFormFieldImage(String keyStr, ImageView imageView) {
        // it is not very efficient but does the job
        List<FormGroup> list = dataFormEx.getGroups();
        for (int i = 0; i < list.size(); ++i) {
            FormGroup formGroup = list.get(i);
            Object obj = formGroup.get(keyStr);
            Drawable drawable = null;
            if (null != obj) {
                FieldItem item = null;
                if (obj instanceof FieldItem) {
                    item = (FieldItem) obj;
                    drawable = item.getImageDrawable();
                }
                else if (obj instanceof FormField) {
                    FormField formField = (FormField) obj;
                    if ((formField).getValue() instanceof FieldItem) {
                        item = (FieldItem) ((FormField) obj).getValue();
                        drawable = item.getImageDrawable();
                    }
                    else if (formField.getValue() instanceof Drawable)
                        drawable = (Drawable) formField.getValue();
                }
                if (null != drawable)
                    imageView.setImageDrawable(drawable);
                break;
            }

        }
    }

    @Override
    protected void formToJsonForm() {
        if (null == dataFormEx)
            throw new IllegalStateException("The form data (DataFormEx) cannot be null.");

        jsonForm = FormHelper.createForm((Map) dataFormEx, !locked,this, formMetaData, sortFormNeeded);
    }

    private String getFormIdInternal() {
        return TextUtils.isEmpty(formId) ? dataFormEx.getFormId() : formId;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (null != formHandler)
            formHandler.onFormResume(getFormIdInternal());
    }

    @Override
    public boolean writeValue(String stepName, String key, String value) {
        super.writeValue(stepName, key, value);
        
        if (null != formHandler)
            formHandler.onValueUpdated(formId, key, value);
        return false;
    }

    @Override
    public void setupFormHeader(View view) {
        if (null != formHandler)
            formHandler.setupFormHeader(formId, view);
    }

    @Override
    public void setupFormFooter(View view) {
        if (null != formHandler)
            formHandler.setupFormFooter(formId, view);
    }

    /**
     *
     * @return the ordered list/map for key and value
     */
    @Override
    public OrderedDataMap getOrderedDataMap() {
        return null;
    }

    /**
     *
     * @return the form groups
     */
    @Override
    public List getGroups() {
        return null;
    }

    /**
     *
     * @return the fields if there are no groups in the form
     */
    @Override
    public List getFields() {
        return null;
    }

    @Override
    protected void onFormValueUpdated(String key, Object o) {
        if (null != formHandler)
            formHandler.onValueUpdated(formId, key, o);
        super.onFormValueUpdated(key, o);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        String json = JSON.toJson(dataFormEx);
        savedInstanceState.putString(Constants.EXTRA_KEY_FORM_EX, json);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        String json = savedInstanceState.getString(Constants.EXTRA_KEY_FORM_EX);
        try {
            dataFormEx = JSON.parse(json, DataFormEx.class);
        }
        catch (Exception ex) {
            CommonLog.e(this, "failed to restore data form ex", ex);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected boolean handleBroadcastMessage(Message msg) {
        super.handleBroadcastMessage(msg);

        if (null != formHandler) {
            return formHandler.onReceiveBroadcastMessage(formId, msg.what, msg.obj);
        }
        return true;
    }
}
