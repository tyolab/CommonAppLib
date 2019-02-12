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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import au.com.tyo.android.adapter.QuickAccessListAdapter;
import au.com.tyo.app.Constants;
import au.com.tyo.app.Controller;
import au.com.tyo.app.R;
import au.com.tyo.app.ui.UIList;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 27/7/17.
 */

public class PageCommonList<T extends Controller> extends Page<T> implements UIList {

    private ListView listView;
    private QuickAccessListAdapter adapter;
    private AdapterView.OnItemClickListener onItemClickListener;

    private int listItemResourceId;

    private String listKey;

    /**
     * The unique identifier
     */
    private String listId;

    public PageCommonList(T controller, Activity activity) {
        super(controller, activity);

        listItemResourceId = -1;

        setContentViewResId(R.layout.list_view);

        createAdapter();
    }

    public int getListItemResourceId() {
        return listItemResourceId;
    }

    public void setListItemResourceId(int listItemResourceId) {
        this.listItemResourceId = listItemResourceId;
    }

    protected void createAdapter() {
            createAdapter(listItemResourceId);
    }

    protected void createAdapter(int resId) {
        if (resId == -1)
            resId = android.R.layout.simple_list_item_1;
        adapter = new QuickAccessListAdapter(getActivity(), resId);
    }

    @Override
    public BaseAdapter getBaseAdapter() {
        return adapter;
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


        getSuggestionView().getSuggestionAdapter().setRequestFromId(listId);

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

    public boolean isArrayAdapter() {
        return adapter instanceof ArrayAdapter;
    }

    protected ArrayAdapter getArrayAdapter() {
        return (ArrayAdapter) adapter;
    }

    @Override
    public void bindData(Intent intent) {
        super.bindData(intent);

        if (intent.hasExtra(Constants.DATA_LIST)) {
            List list = null;
            createAdapter();

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

        if (intent.hasExtra(Constants.DATA_LIST_KEY))
            listKey = intent.getStringExtra(Constants.DATA_LIST_KEY);

        if (intent.hasExtra(Constants.DATA_SHOW_SEARCH))
            setToShowSearchView((Boolean) intent.getBooleanExtra(Constants.DATA_SHOW_SEARCH, false));

        if (intent.hasExtra(Constants.DATA_LIST_ID))
            listId = intent.getStringExtra(Constants.DATA_LIST_ID);
    }

    private void addList(List list) {
        if (adapter instanceof ArrayAdapter) {
            ArrayAdapter arrayAdapter = getArrayAdapter();
            arrayAdapter.clear();
            arrayAdapter.addAll(list);

            adapter.notifyDataSetChanged();
        }
        else
            throw new IllegalStateException("Unknown adapter type");
    }

    @Override
    public void bindData() {
        super.bindData();

        // when the parcel / parcel list is too big just don't do it
        if (null != getController().getParcel()) {
            Object object = getController().getParcel();

            //
            createAdapter();

            Object data = null;
            if (object instanceof Map) {
                Map map = (Map) object;

                if (map.containsKey(Constants.DATA_LIST))
                    data = (map).get(Constants.DATA_LIST);
                else {
                    adapter.initialize((String) map.get(Constants.DATA_LIST_FULL_LIST_TITLE),
                            (List) map.get(Constants.DATA_LIST_FULL_LIST_DATA),
                            (String) map.get(Constants.DATA_LIST_QUICK_ACCESS_TITLE),
                            (List) map.get(Constants.DATA_LIST_QUICK_ACCESS_LIST),
                            true,
                            (int[]) map.get(Constants.DATA_LIST_SELECTED));

                    setToShowSearchView((Boolean) map.get(Constants.DATA_SHOW_SEARCH));

                    if (map.containsKey(Constants.DATA_LIST_ID))
                        listId = (String) map.get(Constants.DATA_LIST_ID);
                }

                if (map.containsKey(Constants.DATA_LIST_KEY))
                    listKey = (String) map.get(Constants.DATA_LIST_KEY);

                String title = (String) map.get(Constants.PAGE_TITLE);
                if (null != title)
                    setTitle(title);
            }
            else
                data = object;

            if (null != data) {
                if (data instanceof List)
                    addList((List) data);
                else
                    addList(Arrays.asList(data));
            }
        }
    }

    @Override
    public void onDataBound() {
        super.onDataBound();

        if (null == adapter)
            createAdapter();

        if (listId == null)
            listId = this.getClass().getSimpleName();
    }

    public void addItem(Object obj) {
        if (adapter instanceof ArrayAdapter) {
            ArrayAdapter arrayAdapter = getArrayAdapter();
            arrayAdapter.add(obj);
                adapter.notifyDataSetChanged();
        }
        else
            throw new IllegalStateException("Unknown adapter type");

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
