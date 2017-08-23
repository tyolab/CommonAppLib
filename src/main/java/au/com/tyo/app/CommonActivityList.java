package au.com.tyo.app;


import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.Adapter;

import au.com.tyo.android.adapter.ListViewItemAdapter;
import au.com.tyo.app.ui.PageCommonList;
import au.com.tyo.app.ui.UI;
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

    private ListViewItemAdapter getAdapter() {
        return getPage() instanceof UIList && null != getListPage().getAdapter() ?
                getListPage().getAdapter()
                :
                null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ListViewItemAdapter adapter = getAdapter();
        if (resultCode == Activity.RESULT_OK && null != data && null != adapter) {
            Object obj = data.getParcelableExtra(Constants.RESULT);

            if (obj != null) {
                adapter.add(obj);
                adapter.notifyDataSetChanged();
            }
            return;
        }
    }
}
