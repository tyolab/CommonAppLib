package au.com.tyo.app.ui.activity;

import au.com.tyo.app.CommonActivity;
import au.com.tyo.app.ui.page.PageAbout;
import au.com.tyo.app.ui.page.PageFormEx;

public class CommonActivityForm extends CommonActivity {

    @Override
    protected void loadPageClass() {
        getAgent().setPageClass(PageFormEx.class);
    }

}
