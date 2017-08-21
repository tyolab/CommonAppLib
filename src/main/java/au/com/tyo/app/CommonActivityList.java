package au.com.tyo.app;


import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

import au.com.tyo.app.ui.PageCommonList;
import au.com.tyo.app.ui.UIList;
import au.com.tyo.app.ui.UIPage;

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && null != data) {
            Object obj = data.getParcelableExtra(Constants.RESULT);
            getListPage().getAdapter().add(obj);
            getListPage().getAdapter().notifyDataSetChanged();
        }
    }
}
