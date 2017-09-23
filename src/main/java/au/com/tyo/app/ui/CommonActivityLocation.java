package au.com.tyo.app.ui;

import au.com.tyo.app.CommonAppCompatActivity;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 23/9/17.
 */

public class CommonActivityLocation extends CommonAppCompatActivity {

    @Override
    protected void loadPageClass() {
        getAgent().setPageClass(PageLocation.class);
    }
}
