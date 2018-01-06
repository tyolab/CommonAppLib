/*
 * Copyright (c) 2017 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package au.com.tyo.app.ui.page;

import android.app.Activity;
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
import au.com.tyo.app.ui.UIList;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 27/7/17.
 */

public class PageCommonList<T extends Controller> extends Page<T> implements UIList {

    private ListView listView;
    private BaseAdapter adapter;
    private AdapterView.OnItemClickListener onItemClickListener;

    public PageCommonList(T controller, Activity activity) {
        super(controller, activity);

        setContentViewResId(R.layout.list_view);

        createAdapter();
    }

    protected void createAdapter() {
        adapter = new ListViewItemAdapter();
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ListView getListView() {
        return listView;
    }

    @Override
    public void setupComponents() {
        super.setupComponents();

        listView = (ListView) findViewById(R.id.list_view);
    }

    @Override
    public void onActivityStart() {
        super.onActivityStart();

        if (null != listView) {
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(getOnItemClickListener());

            // not yet
            // showSuggestionView();
        }
    }

    public AdapterView.OnItemClickListener getOnItemClickListener() {
        if (null == onItemClickListener)
            return new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Object item = adapter.getItem(position);
                    setResult(item);
                    finish();
                }
            };
        return onItemClickListener;
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

        if (intent.hasExtra(Constants.DATA_LIST)) {
            List list = null;

            try {
                list = intent.getParcelableArrayListExtra(Constants.DATA_LIST);
            }
            catch (Exception ex) {}

            if (null == list) {
                try {
                    String[] array = intent.getStringArrayExtra(Constants.DATA_LIST);
                    list = Arrays.asList(array);
                }
                catch (Exception ex) {}
            }

            if (null == list)
                try {
                    list = intent.getStringArrayListExtra(Constants.DATA_LIST);
                }
                catch (Exception ex) {}

            addList(list);
        }
    }

    private void addList(List list) {
        if (adapter instanceof ListViewItemAdapter) {
            ListViewItemAdapter listAdapter = (ListViewItemAdapter) adapter;
            listAdapter.clear();
            if (null != list)
                listAdapter.setItems(list);
            else
                listAdapter.setItems(new ArrayList());
        }
        else if (adapter instanceof ArrayAdapter) {
            ArrayAdapter arrayAdapter = getArrayAdapter();
            arrayAdapter.clear();
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

    public void setAdapter(ListWithHeadersAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public BaseAdapter getBaseAdapter() {
        return adapter;
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

    @Override
    public boolean onBackPressed() {
        // clear result
        setResult(null);
        return super.onBackPressed();
    }
}
