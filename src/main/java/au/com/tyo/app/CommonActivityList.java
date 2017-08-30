package au.com.tyo.app;


import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.BaseAdapter;

import au.com.tyo.android.adapter.ListViewItemAdapter;
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

    public UIList getListPage() {
        return (UIList) getPage();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

}
