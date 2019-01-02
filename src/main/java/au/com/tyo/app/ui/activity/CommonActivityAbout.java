package au.com.tyo.app.ui.activity;

import au.com.tyo.app.CommonActivity;
import au.com.tyo.app.ui.page.PageAbout;

public class CommonActivityAbout extends CommonActivity {

    @Override
    protected void loadPageClass() {
        getAgent().setPageClass(PageAbout.class);
    }

}
