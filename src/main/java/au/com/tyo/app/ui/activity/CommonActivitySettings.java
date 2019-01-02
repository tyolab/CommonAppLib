package au.com.tyo.app.ui.activity;

import au.com.tyo.app.CommonActivity;
import au.com.tyo.app.ui.page.PageAbout;
import au.com.tyo.app.ui.page.PageSettings;

public class CommonActivitySettings extends CommonActivity {

    @Override
    protected void loadPageClass() {
        if (null == getAgent().getPageClass())
        getAgent().setPageClass(PageSettings.class);
    }

}
