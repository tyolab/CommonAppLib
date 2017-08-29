package au.com.tyo.app.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import au.com.tyo.android.adapter.ListViewItemAdapter;
import au.com.tyo.app.Constants;
import au.com.tyo.app.Controller;
import au.com.tyo.app.R;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 27/7/17.
 */

public class PageCommonList extends Page implements AdapterView.OnItemClickListener, UIList {

    private ListView listView;
    private ListViewItemAdapter adapter;

    public PageCommonList(Controller controller, Activity activity) {
        super(controller, activity);

        setContentViewResId(R.layout.list_view);

        adapter = new ListViewItemAdapter();
    }

    public ListView getListView() {
        return listView;
    }

    @Override
    public void setupComponents() {
        super.setupComponents();

        listView = (ListView) findViewById(R.id.list_view);

        if (null != listView) {
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(this);

            //showSuggestionView();
        }
    }

    @Override
    public void bindData(Intent intent) {
        super.bindData(intent);

        List list = intent.getParcelableArrayListExtra(Constants.DATA);
        if (null != list)
            adapter.setItems(list);
        else
            adapter.setItems(new ArrayList());

    }

    @Override
    public void bindData() {
        super.bindData();

        // when the parcel / parcel list is too big just don't do it
        if (null != getController().getParcel()) {
            Object object = getController().getParcel();
            Object data;
            if (object instanceof Map) {
                Map map = (Map) object;
                data = (map).get(Constants.DATA);
                String title = (String) map.get(Constants.PAGE_TITLE);
                if (null != title)
                    setTitle(title);
            }
            else
                data = object;

            if (data instanceof List)
                adapter.setItems((List) object);
            else
                adapter.add(object);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object item = adapter.get(position);
        setResult(item);
        getActivity().finish();
    }

    @Override
    public ListViewItemAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void onStop(Context currentActivity) {
        super.onStop(currentActivity);

        // we finish the parcel
        getController().setParcel(null);
    }

    @Override
    public void onFinish() {
        super.onFinish();
        if (null != getController().getParcel())
            getController().setParcel(null);
    }
}
