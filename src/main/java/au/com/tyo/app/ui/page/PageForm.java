/*
 * Copyright (c) 2017 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package au.com.tyo.app.ui.page;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import au.com.tyo.app.CommonLog;
import google.json.JSONArray;
import google.json.JSONException;
import google.json.JSONObject;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import au.com.tyo.android.utils.ResourceUtils;
import au.com.tyo.android.utils.SimpleDateUtils;
import au.com.tyo.app.CommonAppData;
import au.com.tyo.app.Constants;
import au.com.tyo.app.Controller;
import au.com.tyo.app.R;
import au.com.tyo.app.api.JSON;
import au.com.tyo.json.android.fragments.FormFragment;
import au.com.tyo.json.android.fragments.JsonFormFragment;
import au.com.tyo.json.android.interfaces.JsonApi;
import au.com.tyo.json.android.utils.FormHelper;
import au.com.tyo.json.form.FieldValue;
import au.com.tyo.json.form.FormItem;
import au.com.tyo.json.form.FormMetaData;
import au.com.tyo.json.form.FormState;
import au.com.tyo.json.jsonform.JsonForm;
import au.com.tyo.json.util.TitleKeyConverter;
import au.com.tyo.json.validator.Validator;

import static au.com.tyo.json.android.constants.JsonFormConstants.FIRST_STEP_NAME;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 20/12/17.
 */

public abstract class PageForm<T extends Controller> extends Page<T>  implements JsonApi, TitleKeyConverter {

    private static final    String              TAG = "PageForm";

    protected               String              json;

    /**
     * Entire form json object
     */
    private                 JSONObject          mJSONObject;

    /**
     * The steps json array
     */
    private                 JSONArray           jsonSteps;

    /**
     * The form container id
     */
    private                 int                 formContainerId;

    /**
     * Form editable if conditions satisfied
     */
    private                 boolean             editable;

    /**
     * Form locked, so it is unconditional uneditable
     */
    protected               boolean             locked;

    /**
     * Is the form data gets changed
     */
    private                 boolean             dirty;

    /**
     * If form validation is required
     */
    private                 boolean             formValidationRequired;

    /**
     * By default we save data in the background thread, so we can't exit activity
     * until data is saved
     */
    private                 boolean             exitAfterSaveAction = false;
    private                 boolean             errorHandled = false;
    protected               boolean             sortFormNeeded = false;

    /**
     * could be Data Map
     */
    private                 Object              form;

    /**
     * For form transforming to json
     */
    protected               JsonForm            jsonForm;

    /**
     * Metadata map for form
     */
    protected               FormMetaData        formMetaData;
    private                 String              formMetaAssetJsonFile;

    private                 boolean             menuEditRequired;

    /**
     * The name of current step
     */
    private                 String              currentStep;

    /**
     *
     * @param controller
     * @param activity
     */
    public PageForm(T controller, Activity activity) {
        super(controller, activity);

        setFormContainerId(au.com.tyo.app.R.id.content_view);
        this.form = null;
        this.formMetaData = null;
        this.formMetaAssetJsonFile = null;

        // by default, if it is editable form, we don't need to show the edit menu unless you specially want it
        this.menuEditRequired = false;
        this.formValidationRequired = true;
    }

    /**
     * load the form meta data from asset file from
     *
     * @param context
     */
    public void loadFormMetaData(Context context) {
        if (formMetaAssetJsonFile != null) {
            //throw new IllegalStateException("Unknown form meta data file in assets folder: jflib");

            try {
                String metaDataJson = CommonAppData.assetToString(context, "jflib" + File.separator + formMetaAssetJsonFile);
                if (!TextUtils.isEmpty(metaDataJson))
                    formMetaData = JSON.getGson().fromJson(metaDataJson, FormMetaData.class);
            }
            catch (Exception ex) {
                Log.e(TAG, "loading json form metadata error: " + formMetaAssetJsonFile, ex);
            }
        }
    }

    public boolean isMenuEditRequired() {
        return menuEditRequired;
    }

    public void setMenuEditRequired(boolean menuEditRequired) {
        this.menuEditRequired = menuEditRequired;
    }

    public boolean isFormValidationRequired() {
        return formValidationRequired;
    }

    public void setFormValidationRequired(boolean formValidationRequired) {
        this.formValidationRequired = formValidationRequired;
    }

    public Object getForm() {
        return form;
    }

    public void setForm(Object form) {
        this.form = form;
    }

    public void setJsonForm(JsonForm jsonForm) {
        this.jsonForm = jsonForm;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isSortFormNeeded() {
        return sortFormNeeded;
    }

    public void setSortFormNeeded(boolean sortFormNeeded) {
        this.sortFormNeeded = sortFormNeeded;
    }

    public FormMetaData getFormMetaData() {
        return formMetaData;
    }

    public void setFormMetaData(FormMetaData formMetaData) {
        this.formMetaData = formMetaData;
    }

    public String getFormMetaAssetJsonFile() {
        return formMetaAssetJsonFile;
    }

    public void setFormMetaAssetJsonFile(String formMetaAssetJsonFile) {
        this.formMetaAssetJsonFile = formMetaAssetJsonFile;
    }

    /**
     *
     */
    protected void setContentViewResource() {
        setContentViewResId(R.layout.form_frame);
    }

    public void setFormContainerId(int formContainerId) {
        this.formContainerId = formContainerId;
    }

    public boolean exitAfterSaveAction() {
        return exitAfterSaveAction;
    }

    public void setExitAfterSaveAction(boolean exitAfterSaveAction) {
        this.exitAfterSaveAction = exitAfterSaveAction;
    }

    public boolean isErrorHandled() {
        return errorHandled;
    }

    public void setErrorHandled(boolean errorHandled) {
        this.errorHandled = errorHandled;
    }

    @Override
    public synchronized JSONObject getStep(String name) {
        synchronized (mJSONObject) {
            try {
                return mJSONObject.getJSONObject(name);
            } catch (JSONException e) {
                CommonLog.e(this, "Error in getting step name: " + name, e);
            }
        }
        return null;
    }

    @Override
    public String currentJsonState() {
        synchronized (mJSONObject) {
            return mJSONObject.toString();
        }
    }

    @Override
    public String getCount() {
        return "1";
    }

    @Override
    public void writeValue(String stepName, String key, String value) {
        synchronized (mJSONObject) {
            try {
                JSONObject jsonObject = mJSONObject.getJSONObject(stepName);

                writeValueToField(key, value, jsonObject);

                JSONArray groups = jsonObject.optJSONArray("groups");
                if (null != groups)
                    for (int i = 0; i < groups.length(); i++) {
                        JSONObject item = groups.getJSONObject(i);
                        writeValueToField(key, value, item);
                    }
            }
            catch (Exception e) {
                Log.e(TAG, "Failed to write value to the json object cache for key: " + key, e);
            }
        }
    }

    private void writeValueToField(String key, String value, JSONObject jsonObject) throws JSONException {
        JSONArray fields = jsonObject.optJSONArray("fields");
        if (null != fields)
            for (int i = 0; i < fields.length(); i++) {
                JSONObject item = fields.getJSONObject(i);
                String keyAtIndex = item.getString("key");
                if (key.equals(keyAtIndex)) {
                    getJsonFormFragment().addUserInputValueToMetadata(key, null, value);

                    String oldValue = item.optString("value");

                    checkAndUpdateValue(item, key, null, oldValue, value);
                    return;
                }
            }
    }

    private void checkAndUpdateValue(JSONObject item, String parentKey, String childKey, String oldValue, String value) throws JSONException {
        boolean equal = false;

        if (null == oldValue) {
            if (null == value)
                equal = true;
        }
        else {
            if (null != value) {
                equal = oldValue.equals(value);
            }
        }

        if (!equal) {
            item.put("value", value);
            onFieldDataDirty(parentKey, childKey, value);
        }
    }

    protected void onFieldDataDirty(String key, String childKey, java.lang.Object value) {
        setDirty(true);

        // update the value in metadata for form validation
        getJsonFormFragment().onValueChange(key, childKey, value);

        // update the field value in the form object
        setFieldValue(key, childKey, value);
    }

    public static void setFieldValueInternal(Map map, String key, Object value) {
        Object oldValue = map.get(key);
        if (null != oldValue) {
            // Only worry these two types initially
            if (oldValue instanceof Boolean) {
                Boolean b;
                if (value instanceof String)
                    b = Boolean.parseBoolean((String) value);
                else if (value instanceof Boolean)
                    b = (Boolean) value;
                else
                    b = Boolean.parseBoolean(value.toString());
                map.put(key, b);
            }
            else if (oldValue instanceof String) {
                map.put(key, value.toString());
            }
            else
                map.put(key, value);
        }
    }

    protected void setFieldValue(String key, String childKey, Object value) {
        if (form instanceof Map) {
            Map map = (Map) form;

            if (null != childKey) {
                Map map2 = (Map) map.get(key);
                if (null == map2) {
                    map2 = new HashMap();
                    map.put(key, map2);
                }
                setFieldValueInternal(map2, childKey, value);
            }
            else
                setFieldValueInternal(map, key, value);
        }
        else if (form instanceof FormItem) {
            FormItem formItem = (FormItem) form;
            setFieldValueInternal(formItem, key, value);
        }
    }

    protected Object getFormFieldValueFromMetaData(String key) {
        return getFormFieldValueFromMetaData(key, null);
    }

    protected Object getFormFieldValueFromMetaData(String key, String childKey) {
        return getJsonFormFragment().getValue(key, childKey);
    }

    @Override
    public void writeValue(String stepName, String parentKey, String childObjectKey, String childKey, String value) {
        synchronized (mJSONObject) {
            try {
                JSONObject jsonObject = mJSONObject.getJSONObject(stepName);

                writeValueToField(parentKey, childObjectKey, childKey, value, jsonObject);

                JSONArray groups = jsonObject.optJSONArray("groups");
                if (null != groups)
                    for (int i = 0; i < groups.length(); i++) {
                        JSONObject item = groups.getJSONObject(i);
                        writeValueToField(parentKey, childObjectKey, childKey, value, item);
                    }
            }
            catch (Exception e) {
                Log.e(TAG, "Failed to write value to the json object cache for keys: " + parentKey + '-' + childKey, e);
            }
        }
    }

    private void writeValueToField(String parentKey, String childObjectKey, String childKey, String value, JSONObject jsonObject) throws JSONException {
        JSONArray fields = jsonObject.optJSONArray("fields");
        if (null == fields)
            return;

        for (int i = 0; i < fields.length(); i++) {
            JSONObject item = fields.getJSONObject(i);
            String keyAtIndex = item.getString("key");
            if (parentKey.equals(keyAtIndex)) {
                JSONArray jsonArray = item.getJSONArray(childObjectKey);
                for (int j = 0; j < jsonArray.length(); j++) {
                    JSONObject innerItem = jsonArray.getJSONObject(j);
                    String anotherKeyAtIndex = innerItem.getString("key");
                    if (childKey.equals(anotherKeyAtIndex)) {
                        // add value to the metadata
                        getJsonFormFragment().addUserInputValueToMetadata(parentKey, childKey, value);

                        String oldValue = innerItem.optString("value");

                        checkAndUpdateValue(innerItem, parentKey, childKey, oldValue, value);
                        return;
                    }
                }
            }
        }

    }


    protected void loadFormData(Intent intent) {
        if (null == json)
            json = intent.getStringExtra(Constants.EXTRA_KEY_JSON);
    }

    protected void processData(Intent intent) {
        if (null != jsonForm)
            intent.putExtra(Constants.EXTRA_KEY_JSON, jsonForm.toString());
    }

    @Override
    public void bindData() {
        super.bindData();

        if (getController().getParcel() != null && getForm() == null) {
            if (getController().getParcel() instanceof Map) {
                Map map = (Map) getController().getParcel();
                Map formMap;
                if (map.containsKey(Constants.DATA)) {
                    setForm(formMap = (Map) map.get(Constants.DATA));
                }
                else
                    setForm(formMap = map);

                if (formMap.containsKey(Constants.EXTRA_KEY_EDITABLE))
                    setEditable((boolean) formMap.get(Constants.EXTRA_KEY_EDITABLE));
                setTitle((String) formMap.get(Constants.EXTRA_KEY_TITLE));
            }
            else if (getController().getParcel() instanceof FormItem) {
                FormItem formItem = (FormItem) getController().getParcel();
                setForm(formItem);
                setEditable(formItem.isEditable());
                setTitle(formItem.getTitle());
            }
        }
    }

    @Override
    public void bindData(Intent intent) {
        super.bindData(intent);

        /**
         * If we pass the data via intent we stick with the parcelable object
         */
        if (null == json && intent.hasExtra(Constants.DATA)) {
            setForm(intent.getParcelableExtra(Constants.DATA));

            createJsonForm();

            processData(intent);

            loadFormData(intent);

            if (intent.hasExtra(Constants.EXTRA_KEY_EDITABLE))
                setEditable(intent.getBooleanExtra(Constants.EXTRA_KEY_EDITABLE, true));
        }
    }

    protected void createJsonForm() {

        loadFormMetaData(getActivity());

        formToJsonForm();
    }

    /**
     * Convert form data to json form
     */
    protected void formToJsonForm() {
        if (null != getForm()) {
            if (form instanceof FormItem)
                jsonForm = ((FormItem) form).toJsonForm();
            else if (form instanceof Map)
                jsonForm = FormHelper.createForm((Map) form, !locked,this, formMetaData, sortFormNeeded);
            else
                throw new IllegalStateException("Form data must be derived from a Map class or implemented FormItem interface");
        }
    }

    /**
     * Create form json string, and set the form fragment to the page
     */
    @Override
    public void onDataBound() {
        super.onDataBound();

        if (null == json && null != form)
            createJsonForm();

        if (null != jsonForm)
            json = jsonForm.toString();

        if (null != json) {
            load(json);
            FormFragment jsonFormFragment = createFragmentJsonForm();

            if (editable && getJsonForm().getFormState() == FormState.State.NONA) {
                getJsonForm().setFormState(FormState.State.NEW);
            }

            jsonFormFragment.setEditable(editable);

            // make sure we use the same data pointer when we load the data or save the data
            jsonFormFragment.setForm(getForm());
            jsonFormFragment.setJsonApi(this);

            replaceFragment(formContainerId, jsonFormFragment, FormFragment.FRAGMENT_JSON_FORM_TAG);
        }
    }

    public FormFragment getJsonFormFragment() {
        if (getFragmentCount() == 0)
            createFragmentJsonForm();

        return (FormFragment) getFragment(0);
    }

    private FormFragment createFragmentJsonForm() {
        if (getFragmentCount() > 0)
            removeFragments();

        FormFragment jsonFormFragment = (FormFragment) FormFragment.getFormFragment(FIRST_STEP_NAME);
        jsonFormFragment.setDarkThemeInUse(!getController().getSettings().isLightThemeInUse());
        addFragmentToList(jsonFormFragment);
        return jsonFormFragment;
    }

    public void load(String json) {
        try {
            mJSONObject = new JSONObject(json);
        } catch (JSONException e) {
            Log.e(TAG, "Initialization error. Json passed is invalid : " + e.getMessage());
        }
    }

    public String getCurrentKey() {
        return getJsonFormFragment().getCurrentKey();
    }

    protected boolean checkBeforeSave() {
        if (isFormValidationRequired())
            return getJsonFormFragment().validateForm() && checkOthers();
        return true;
    }

    protected boolean checkOthers() {
        return true;
    }

    protected void goBack() {
        super.onBackPressed();
    }

    @Override
    protected void createMenu(MenuInflater menuInflater, Menu menu) {
       super.createMenu(menuInflater, menu);

       if (isMenuEditRequired())
           createMenuItemEditSave(menuInflater, menu);
    }

    private void createMenuItemEditSave(MenuInflater menuInflater, Menu menu) {
        menuInflater.inflate(R.menu.one_only, menu);
    }

    @Override
    protected boolean onMenuCreated(Menu menu) {
        super.onMenuCreated(menu);

        setEditOrSave();

        return true;
    }

    protected void setEditOrSave() {
         if (editable)
             setMenuItemSave();
         else
             setMenuItemEdit();
    }

    protected void setMenuItemSave() {
         getActionBarMenu().setMenuTitle(R.id.menuItemOne,"Save");
    }

    protected void setMenuItemEdit() {
        getActionBarMenu().setMenuTitle(R.id.menuItemOne, "Edit");
    }

    protected boolean isNewForm() {
        return isNewForm(getJsonForm());
    }

    protected static boolean isNewForm(JsonForm form) {
        return form.getFormState() == FormState.State.NEW
                || form.getFormState() == FormState.State.AUTO_FILLED;
    }

    public void changeFormEditableState() {
        setEditable(!editable);

        getJsonFormFragment().changeFormEditableState(this.editable);
    }

    protected void onFormEditStateChange(boolean editable) {
        if (editable) {
            if (!saveAndFinish())
                return; // if we fail the saving, we are not gonna change the editing state
        }
        else
            getJsonForm().setFormState(FormState.State.UPDATING);

        //
        changeFormEditableState();
    }

    /**
     * can exit the page without filling the data
     *
     * @return true/false
     */
    protected boolean isExitable() {
        return !isNewForm() && !isDirty();
    }

    protected boolean saveAndFinish() {
        if (isExitable()) {
            if (exitAfterSaveAction())
                finish();
            return true;
        }

        // form is dirty
        if (checkBeforeSave()) {
            // getToolBarMenu().setMenuItemOneVisible(false);
            save();

            if (exitAfterSaveAction())
                finish();

            return true;
        }

        onFormCheckFailed();
        return false;
    }

    protected void onFormSaved() {
        // no ops
    }

    protected abstract void onFormCheckFailed();

    protected void saveInBackgroundThread() {
        startBackgroundTask();
    }

    @Override
    public void run() {
        saveInternal();
    }

    @Override
    protected void onPageBackgroundTaskFinished(int id) {
        super.onPageBackgroundTaskFinished(id);

        showDataSavedMessage();

        finish();
    }

    protected void showDataSavedMessage() {
        Toast.makeText(getActivity(), R.string.data_saved, Toast.LENGTH_SHORT);
    }

    protected void save() {
        save(!exitAfterSaveAction());
    }

    protected void save(boolean inBackgroundThread) {
        if (inBackgroundThread)
            saveInBackgroundThread();
        else
            saveInternal();
    }

    private void saveInternal() {
        if (!isNewForm())
            jsonForm.setFormState(FormState.State.UPDATED);

        saveFormData(getForm());

        // need an explanation here
        if (exitAfterSaveAction())
            setResult(getForm());

        setDirty(false);

        onFormSaved();
    }

    protected abstract void saveFormData(Object form);

    @Override
    public boolean isEditable() {
        return editable;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public String getPredefinedValue(String stepName, String key) {
        return null;
    }

    public String getPredefinedValueMax(String stepName, String key) {
        return null;
    }

    public String getPredefinedValueMin(String stepName, String key) {
        return null;
    }

    public abstract void onFormClick(Context context, String key, String text);

    public JsonForm getJsonForm() {
        return jsonForm;
    }

    @Override
    public String formatDateTime(String key, Date date) {
        return SimpleDateUtils.toSlashDelimAussieDate(date);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.menuItemOne) {
            saveAndFinish();
            return true;
        }
        return super.onMenuItemClick(item);
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data != null && requestCode == Constants.REQUEST_FORM_FILLING) {
            String key = getCurrentKey();
            Object result = getActivityResult(data);
            if (null != result) {
                return onResultReceived(key, result);
            }
        }
        /**
         * let the parent method update value in the field
         */
        return super.onActivityResult(requestCode, resultCode, data);
    }

    @OverridingMethodsMustInvokeSuper
    protected boolean onResultReceived(String key, Object result) {
        /**
         * @TODO
         *    make the step into array
         */
        String value = objectToString(result);
        writeValue(FIRST_STEP_NAME, key, value);

        getJsonFormFragment().updateForm(key, value);
        return true;
    }

    /**
     * If the object is implemented with FieldValue, we need a bit conversion
     *
     * @param object
     * @return
     */
    protected String objectToString(Object object) {
        if (null == object)
            return null;

        if (object instanceof FieldValue)
            return ((FieldValue) object).getStringValue();
        return object.toString();
    }

    @Override
    public String toKey(String key) {
        return FormHelper.getGeneralTitleKeyConverter().toKey(key);
    }

    /**
     * Can be overridden in the meta json as in assets/jflib/form.xxxx.json
     *
     * @param key
     * @return
     */
    @Override
    public String toTitle(String key) {
        return ResourceUtils.getStringByIdName(getActivity(), key);
    }

    /**
     * Save the data on stop if the edit/save menu item is not visible
     */
    @Override
    public void onStop() {
        super.onStop();

        if (isDirty()) {
            saveAndFinish();
        }
    }

    @Override
    public Object getNullValueReplacement(String keyStr) {
        return null;
    }

    @Override
    public boolean onValidateRequiredFormFieldFailed(String key, String errorMessage) {
        Log.e(TAG, "Form validation failed - key:" + key);
        View view = getJsonFormFragment().getInputViewByKey(key);
        if (null != view) {
            if (view instanceof EditText) {
                ((EditText) view).setError(errorMessage);
                return true;
            }
        }
        return false;
    }

    public void enableFormField(String key, boolean enabled) {
        getJsonFormFragment().enableField(key, enabled);
    }

    public void updateFormField(String key, Object obj) {
        getJsonFormFragment().updateForm(key, obj);
    }

    @Override
    public void installValidator(String keyStr, Validator validator) {
        JsonFormFragment.FieldMetadata formFieldMetaData = getJsonFormFragment().getMetadataMap().get(keyStr);
        formFieldMetaData.addValidator(validator);
    }

    @Override
    public boolean onBackPressed() {
        if (isDirty() && isFormValidationRequired()) {
            if (!getJsonFormFragment().validateForm()) {
                getController().getUi().showFormValidationFailureDialog();
                return true;
            }
        }
        return super.onBackPressed();
    }

    /**
     * Validate Edit Text, but don't do it as it could be just in the middle of changing
     *
     * @param key
     * @param text
     * @return
     */
    @Override
    public boolean validate(String key, String text) {
        String errorMessage =
            getJsonFormFragment().validateField(key, text);
        if (null != errorMessage) {
            onValidateRequiredFormFieldFailed(key, errorMessage);
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return TextUtils.isEmpty(json) ? "{}" : json;
    }
}
