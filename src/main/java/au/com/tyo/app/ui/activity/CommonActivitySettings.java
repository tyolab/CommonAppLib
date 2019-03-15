package au.com.tyo.app.ui.activity;

import au.com.tyo.app.CommonActivity;
import au.com.tyo.app.Controller;
import au.com.tyo.app.ui.page.PageSettings;

public class CommonActivitySettings extends CommonActivity {

    @Override
    protected void onCreatePage() {
        setPage(new PageSettings<Controller>(getController(), this));
        super.onCreatePage();
    }

}
