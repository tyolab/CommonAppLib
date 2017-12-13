package au.com.tyo.app;

import au.com.tyo.app.ui.page.PageWebView;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 17/5/17.
 */

public class CommonActivityWebView extends CommonActivity {

    @Override
    protected void loadPageClass() {
        getAgent().setPageClass(PageWebView.class);
    }

}
