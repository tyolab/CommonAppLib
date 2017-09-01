package au.com.tyo.app.ui;

import android.widget.BaseAdapter;
import android.widget.ListView;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 31/7/17.
 */

public interface UIList {

    ListView getListView();

    BaseAdapter getAdapter();

}
