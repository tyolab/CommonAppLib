package au.com.tyo.app.ui.form;

import android.text.Editable;
import android.view.View;

import au.com.tyo.app.CommonApp;
import au.com.tyo.app.Controller;
import au.com.tyo.json.android.customviews.GenericTextWatcher;
import au.com.tyo.json.android.interfaces.JsonApi;

public class FormTextWatcher extends GenericTextWatcher {

    public FormTextWatcher(String stepName, View view) {
        super(stepName, view);
    }

    @Override
    public void afterTextChanged(Editable editable) {
        // a simple fix
        if (null == api) {
            Controller controller = (Controller) CommonApp.getInstance();
            if (controller.getUi().getCurrentPage() instanceof JsonApi) {
                api = (JsonApi) controller.getUi().getCurrentPage();
            }
        }

        super.afterTextChanged(editable);
    }
}
