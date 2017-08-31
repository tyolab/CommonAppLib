package au.com.tyo.app;


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
        super.createPage();

        if (null == getPage())
            setPage(new PageCommonList(getController(), this));
    }

    public UIList getListPage() {
        return (UIList) getPage();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

}
