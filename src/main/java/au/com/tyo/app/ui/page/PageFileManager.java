/*
 * Copyright (c) 2019. TYONLINE TECHNOLOGY PTY. LTD. (TYOLAB)
 *
 */

package au.com.tyo.app.ui.page;

import android.app.Activity;

import au.com.tyo.android.adapter.QuickAccessListAdapter;
import au.com.tyo.app.Controller;
import au.com.tyo.app.adapter.FileListItemFactory;

public class PageFileManager <T extends Controller> extends PageCommonList<T> {

    public PageFileManager(T controller, Activity activity) {
        super(controller, activity);
    }

    @Override
    public void onActivityStart() {
        super.onActivityStart();

        QuickAccessListAdapter adapter = getQuickAccessListAdapter();
        if (null != adapter)
            adapter.setItemFactory(new FileListItemFactory(getActivity()));
    }
}
