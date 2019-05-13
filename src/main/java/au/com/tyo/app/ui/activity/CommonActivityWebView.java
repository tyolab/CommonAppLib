package au.com.tyo.app.ui.activity;

import au.com.tyo.app.CommonActivity;
import au.com.tyo.app.ui.page.PageSettings;
import au.com.tyo.app.ui.page.PageWebView;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 17/5/17.
 */

public class CommonActivityWebView extends CommonActivity {

    @Override
    protected void onCreatePage() {
        setPage(new PageWebView<>(getController(), this));
        super.onCreatePage();
    }

}
