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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import au.com.tyo.android.utils.ResourceUtils;
import au.com.tyo.android.utils.SimpleDateUtils;
import au.com.tyo.app.CommonAppData;
import au.com.tyo.app.Constants;
import au.com.tyo.app.Controller;
import au.com.tyo.app.api.JSON;
import au.com.tyo.json.FormItem;
import au.com.tyo.json.FormMetaData;
import au.com.tyo.json.FormState;
import au.com.tyo.json.JsonForm;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 20/12/17.
 */

public abstract class PageForm<T extends Controller> extends Page<T>  implements JsonApi, FormHelper.TitleKeyConverter {

    private static final    String              TAG = "PageForm";

    protected               String              json;

    private                 JSONObject          mJSONObject;

    private                 int                 formContainerId;

    private                 boolean             editable;

    private                 boolean             dirty;

    /**
     * By default we save data in the background thread, so we can't exit activity
     * until data is saved
     */
    private                 boolean             exitAfterSaveAction = false;
    private                 boolean             errorHandled = false;
    private                 boolean             sortFormNeeded = false;

    private                 Object              form;
    private                 JsonForm            jsonForm;
    private                 FormMetaData        formMetaData;
    private                 String              formMetaAssetJsonFile;

    private                 boolean             menuEditRequired;

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
        this.menuEditRequired = true;
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

    public Object getForm() {
        return form;
    }

    public void setForm(Object form) {
        this.form = form;
    }

    public void setJsonForm(JsonForm jsonForm) {
        this.jsonForm = jsonForm;
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
                e.printStackTrace();
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
    public void writeValue(String stepName, String key, String value) throws JSONException {
        synchronized (mJSONObject) {
            try {
                JSONObject jsonObject = mJSONObject.getJSONObject(stepName);
                JSONArray fields = jsonObject.getJSONArray("fields");
                for (int i = 0; i < fields.length(); i++) {
                    JSONObject item = fields.getJSONObject(i);
                    String keyAtIndex = item.getString("key");
                    if (key.equals(keyAtIndex)) {
                        getJsonFormFragment().addUserInputValueToMetadata(key, null, value);

                        String oldValue = null;

                        if (item.has("value"))
                            oldValue = item.getString("value");

                        if (null == oldValue || !oldValue.equals(value)) {
                            item.put("value", value);
                            onFieldDataDirty(key, null, value);
                        }
                        return;
                    }
                }
            }
            catch (Exception e) {}
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
    public void writeValue(String stepName, String parentKey, String childObjectKey, String childKey, String value)
            throws JSONException {
        synchronized (mJSONObject) {
            try {
                JSONObject jsonObject = mJSONObject.getJSONObject(stepName);
                JSONArray fields = jsonObject.getJSONArray("fields");
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

                                String oldValue = null;

                                if (innerItem.has("value"))
                                    oldValue = innerItem.getString("value");

                                if (null == oldValue || !oldValue.equals(value)) {
                                    innerItem.put("value", value);
                                    onFieldDataDirty(parentKey, childKey, value);
                                }
                                return;
                            }
                        }
                    }
                }
            }
            catch (Exception e) {}
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
                if (map.containsKey(Constants.DATA) || map.containsKey(Constants.EXTRA_KEY_EDITABLE)) {
                    setForm(map.get(Constants.DATA));

                    editable = map.containsKey(Constants.EXTRA_KEY_EDITABLE) ? (boolean) map.get(Constants.EXTRA_KEY_EDITABLE) : true;
                }
                else
                    setForm(map);
            }
            else if (getController().getParcel() instanceof FormItem)
                setForm((FormItem) getController().getParcel());
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

            editable = intent.getBooleanExtra(Constants.EXTRA_KEY_EDITABLE, true);
        }
    }

    private void createJsonForm() {

        loadFormMetaData(getActivity());

        if (null != getForm()) {
            if (form instanceof FormItem)
                jsonForm = ((FormItem) form).toJsonForm();
            else if (form instanceof Map)
                jsonForm = FormHelper.createForm((Map) form, this, formMetaData, sortFormNeeded);
            else
                throw new IllegalStateException("Form data must be derived from a Map class or implemented FormItem interface");
        }

//        if (jsonForm == null) {
//            jsonForm = new JsonForm("");
//            jsonForm.createNewStep();
//        }
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

            if (editable && getJsonForm().getFormState() == FormState.State.NONE) {
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

        FormFragment jsonFormFragment = (FormFragment) FormFragment.getFormFragment(JsonFormConstants.FIRST_STEP_NAME);
        addFragmentToList(jsonFormFragment);
        return jsonFormFragment;
    }

    public void load(String json) {
        try {
            mJSONObject = new JSONObject(json);
        } catch (JSONException e) {
            Log.d(TAG, "Initialization error. Json passed is invalid : " + e.getMessage());
        }
    }

    public String getCurrentKey() {
        return getJsonFormFragment().getCurrentKey();
    }

    protected boolean checkBeforeSave() {
        return getJsonFormFragment().validateForm() && checkOthers();
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

    public void setFormEditable(boolean editable) {
        this.editable = !editable;
        getJsonFormFragment().setFormEditable(this.editable);
    }

    protected void onFormEditStateChange(boolean editable) {
        if (editable) {
            if (!saveAndFinish())
                return; // if we fail the saving, we are not gonna change the editing state
        }
        else
            getJsonForm().setFormState(FormState.State.UPDATING);

        //
        setFormEditable(editable);

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

            onFormSaved();

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
    protected void onPageBackgroundTaskFinished() {
        super.onPageBackgroundTaskFinished();

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
        return false;
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        //
        if (data != null) {
            String key = getCurrentKey();
            Object result = getActivityResult(data);
            if (null != result) {
                boolean ret = onValueReceived(key, result);
                if (ret)
                    return true;
            }
        }
        return super.onActivityResult(requestCode, resultCode, data);
    }

    protected boolean onValueReceived(String key, Object result) {
        return false;
    }

    @Override
    public String toKey(String key) {
        return key;
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
}
