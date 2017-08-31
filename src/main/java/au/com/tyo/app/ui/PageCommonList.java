package au.com.tyo.app.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import au.com.tyo.android.adapter.ListViewItemAdapter;
import au.com.tyo.android.adapter.ListWithHeadersAdapter;
import au.com.tyo.app.Constants;
import au.com.tyo.app.Controller;
import au.com.tyo.app.R;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 27/7/17.
 */

public class PageCommonList extends Page implements AdapterView.OnItemClickListener, UIList {

    private ListView listView;
    private BaseAdapter adapter;

    public PageCommonList(Controller controller, Activity activity) {
        super(controller, activity);

        setContentViewResId(R.layout.list_view);

        createAdapter();
    }

    protected void createAdapter() {
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

    public boolean isListAdapter() {
        return adapter instanceof ListViewItemAdapter;
    }

    public boolean isArrayAdapter() {
        return adapter instanceof ArrayAdapter;
    }

    protected ListViewItemAdapter getListAdapter() {
        return (ListViewItemAdapter) adapter;
    }

    protected ArrayAdapter getArrayAdapter() {
        return (ArrayAdapter) adapter;
    }

    @Override
    public void bindData(Intent intent) {
        super.bindData(intent);

        List list = intent.getParcelableArrayListExtra(Constants.DATA);

        addList(list);
    }

    private void addList(List list) {
        if (adapter instanceof ListViewItemAdapter) {
            ListViewItemAdapter listAdapter = (ListViewItemAdapter) adapter;
            if (null != list)
                listAdapter.setItems(list);
            else
                listAdapter.setItems(new ArrayList());
        }
        else if (adapter instanceof ArrayAdapter) {
            ArrayAdapter arrayAdapter = getArrayAdapter();
            arrayAdapter.addAll(list);
        }
        else
            throw new IllegalStateException("Unknown adapter type");
        adapter.notifyDataSetChanged();
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
                data = (map).get(Constants.DATA_LIST);
                String title = (String) map.get(Constants.PAGE_TITLE);
                if (null != title)
                    setTitle(title);
            }
            else
                data = object;

            if (data instanceof List)
                addList((List) data);
            else
                addList(Arrays.asList(data));

        }
    }

    public void addItem(Object obj) {
        if (adapter instanceof ListViewItemAdapter) {
            ListViewItemAdapter listAdapter = (ListViewItemAdapter) adapter;
            listAdapter.add(obj);
        }
        else if (adapter instanceof ArrayAdapter) {
            ArrayAdapter arrayAdapter = getArrayAdapter();
            arrayAdapter.add(obj);
        }
        else
            throw new IllegalStateException("Unknown adapter type");
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object item = adapter.getItem(position);
        setResult(item);
        getActivity().finish();
    }

    public void setAdapter(ListWithHeadersAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public BaseAdapter getAdapter() {
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

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && null != data && null != adapter) {
            Object obj = data.getParcelableExtra(Constants.RESULT);

            if (obj != null) addItem(obj);
            return true;
        }
        return false;
    }
}
