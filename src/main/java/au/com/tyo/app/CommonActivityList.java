package au.com.tyo.app;


import android.os.Bundle;
import android.view.Menu;

import au.com.tyo.app.ui.PageCommonList;
import au.com.tyo.app.ui.UIList;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 27/7/17.
 *
 */

public class CommonActivityList extends CommonActivity {

    @Override
    protected void createPage() {
        setPage(new PageCommonList(getController(), this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

}
