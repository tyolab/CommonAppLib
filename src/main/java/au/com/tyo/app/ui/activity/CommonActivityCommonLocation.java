package au.com.tyo.app.ui.activity;

import au.com.tyo.app.CommonAppCompatActivity;
import au.com.tyo.app.ui.page.PageCommonLocation;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 23/9/17.
 */

public class CommonActivityCommonLocation extends CommonAppCompatActivity {

    @Override
    protected void loadPageClass() {
        getAgent().setPageClass(PageCommonLocation.class);
    }
}
