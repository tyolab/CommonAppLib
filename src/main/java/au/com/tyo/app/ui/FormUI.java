package au.com.tyo.app.ui;

import au.com.tyo.app.ui.UI;

public interface FormUI extends UI {

    void editForm(Class activityClass, Object data, boolean editable, boolean needResult);

    void showForm(Class activityClass, Object data, boolean needResult);

    void editForm(Class activityClass, Object data, boolean editable);

}
