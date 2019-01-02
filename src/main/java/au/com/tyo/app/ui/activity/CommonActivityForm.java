package au.com.tyo.app.ui.activity;

import au.com.tyo.app.CommonActivity;
import au.com.tyo.app.ui.page.PageAbout;
import au.com.tyo.app.ui.page.PageFormCommon;
import au.com.tyo.app.ui.page.PageFormEx;

public class CommonActivityForm extends CommonActivity {

    @Override
    protected void createPage() {
        // create page here
        agent.setPage(new PageFormCommon(getController(), this));

        super.createPage();
    }

    /**
     * originally
        @Override
        protected void loadPageClass() {
            getAgent().setPageClass(PageFormCommon.class);
        }
    */
}
