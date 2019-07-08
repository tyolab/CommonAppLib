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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import au.com.tyo.android.adapter.QuickAccessListAdapter;
import au.com.tyo.app.Constants;
import au.com.tyo.app.Controller;
import au.com.tyo.app.R;
import au.com.tyo.app.ui.UIList;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 27/7/17.
 */

public class PageCommonList<T extends Controller> extends Page<T> implements UIList, AdapterView.OnItemClickListener {

    private ListView listView;
    private QuickAccessListAdapter adapter;
    private AdapterView.OnItemClickListener onItemClickListener;

    private int listItemResourceId;

    private String listKey;

    /**
     * The unique identifier
     */
    private int listId;

    private boolean multipleSelectionsAllowed;

    private Collection selected;
    private Collection<Integer> selectedPosition;

    /**
     * Not the same addres as in the items
     */
    private List currentList;

    public PageCommonList(T controller, Activity activity) {
        super(controller, activity);

        listItemResourceId = -1;
        listId = -1;
        multipleSelectionsAllowed = false;
        selected = new HashSet();
        selectedPosition = new HashSet();

        setContentViewResId(R.layout.list_view);

        createAdapter();
    }

    public String getListKey() {
        return listKey;
    }

    public int getListId() {
        return listId;
    }

    public void setListId(int listId) {
        this.listId = listId;
    }

    public Collection getSelected() {
        return selected;
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

            if (multipleSelectionsAllowed) {
                listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                listView.setItemsCanFocus(false);

                // for (int i = 0; i < listView.getCount(); ++i)
                //     listView.setItemChecked(0, false);
            }

            // not yet
            // showSuggestionView();
        }
    }

    @Override
    protected void createMenu(MenuInflater menuInflater, Menu menu) {
        super.createMenu(menuInflater, menu);

        createMenuSelect(menuInflater, menu);
    }

    private void createMenuSelect(MenuInflater menuInflater, Menu menu) {
        menuInflater.inflate(R.menu.menu_select, menu);
    }

    public AdapterView.OnItemClickListener getOnItemClickListener() {
        if (null == onItemClickListener)
            return this;
        return onItemClickListener;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.menuItemSelect)
            onMenuItemSelectClick();
        else if (item.getItemId() == R.id.menuItemSelectAll) {
            onMenuItemSelectAllClick();
        }

        return super.onMenuItemClick(item);
    }

    protected void onMenuItemSelectClick() {
        if (!getController().onMultipleListItemsSelected(listId, selected))
            setResultAndFinish(selected);
        else
            finish();
    }

    protected void onMenuItemSelectAllClick() {
        for (int i = 0; i < adapter.getCount(); ++i)
            selected.add(adapter.getItem(i));
        onMenuItemSelectClick();
    }

    public boolean isArrayAdapter() {
        return adapter instanceof ArrayAdapter;
    }

    protected ArrayAdapter getArrayAdapter() {
        return adapter;
    }

    public QuickAccessListAdapter getQuickAccessListAdapter() {
        return adapter;
    }

    @Override
    public void bindData(Intent intent) {
        super.bindData(intent);

        if (intent.hasExtra(Constants.DATA_LIST)) {
            createAdapter();

            // List currentList = null;
            try {
                currentList = intent.getParcelableArrayListExtra(Constants.DATA_LIST);
            }
            catch (Exception ex) {}

            if (null == currentList) {
                try {
                    String[] array = intent.getStringArrayExtra(Constants.DATA_LIST);
                    currentList = Arrays.asList(array);
                }
                catch (Exception ex) {}
            }

            if (null == currentList)
                try {
                    currentList = intent.getStringArrayListExtra(Constants.DATA_LIST);
                }
                catch (Exception ex) {}

            addList(currentList);
        }

        if (intent.hasExtra(Constants.DATA_LIST_KEY))
            listKey = intent.getStringExtra(Constants.DATA_LIST_KEY);

        if (intent.hasExtra(Constants.DATA_SHOW_SEARCH))
            setToShowSearchView((Boolean) intent.getBooleanExtra(Constants.DATA_SHOW_SEARCH, false));

        if (intent.hasExtra(Constants.DATA_LIST_ID))
            listId = intent.getIntExtra(Constants.DATA_LIST_ID, -1);

        if (intent.hasExtra(Constants.DATA_LIST_ALLOW_MULTIPLE_SELECTIONS))
            multipleSelectionsAllowed = intent.getBooleanExtra(Constants.DATA_LIST_ALLOW_MULTIPLE_SELECTIONS, false);
    }

    private void addList(List list) {
        if (adapter instanceof ArrayAdapter) {
            ArrayAdapter arrayAdapter = getArrayAdapter();
            arrayAdapter.clear();
            arrayAdapter.addAll(list);

            // adapter.notifyDataSetChanged();
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

                if (map.containsKey(Constants.DATA_LIST_ID))
                    listId = (int) map.get(Constants.DATA_LIST_ID);

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
                }

                if (map.containsKey(Constants.DATA_LIST_KEY))
                    listKey = (String) map.get(Constants.DATA_LIST_KEY);

                if (map.containsKey(Constants.DATA_LIST_ALLOW_MULTIPLE_SELECTIONS))
                    multipleSelectionsAllowed = (boolean) map.get(Constants.DATA_LIST_ALLOW_MULTIPLE_SELECTIONS);

                String title = (String) map.get(Constants.PAGE_TITLE);
                if (null != title)
                    setTitle(title);
            }
            else
                data = object;

            if (null != data) {
                // List currentList;

                if (data instanceof List) {
                    currentList = (List) data;
                }
                else {
                    currentList = Arrays.asList(data);
                }

                addList(currentList);
            }
        }
    }

    @Override
    public void onDataBound() {
        super.onDataBound();

        if (null == adapter)
            createAdapter();
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

    @OverridingMethodsMustInvokeSuper
    @Override
    public boolean onBackPressed() {
        // clear result
        setResult(null);
        clearSelections();

        return super.onBackPressed();
    }

    @Override
    protected void onSuggestionItemClick(Object obj) {
        if (!getController().onListItemClick(listKey, listId, obj))
            setResultAndFinish(obj);
        else
            finish();
    }

    @Override
    protected void onMenuPostCreated() {
        super.onMenuPostCreated();

        // No, when there is an item selected
        // if (multipleSelectionsAllowed)
        //     getActionBarMenu().showMenuItem(R.id.menuItemSelect);
    }

    protected Object getListItem(int position) {
        return adapter.getItem(position);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (isMultipleSelectionsAllowed()) {
            boolean isChecked = getListView().isItemChecked(position);
            Object item = getListItem(position);

            if (isChecked) {
                selected.add(item);
                selectedPosition.add(position);
            }
            else {
                selected.remove(item);
                selectedPosition.remove(position);
            }

            if (selected.size() > 0)
                getActionBarMenu().showMenuItem(R.id.menuItemSelect);
            else
                getActionBarMenu().hideMenuItem(R.id.menuItemSelect);

            // getListView().setItemChecked(position, !isChecked);
            // isChecked = getListView().isItemChecked(position);
            return;
        }

        Object item = adapter.getItem(position);
        if (!getController().onListItemClick(listKey, listId, item))
            setResultAndFinish(item);
        else
            finish();
    }

    public boolean isMultipleSelectionsAllowed() {
        return multipleSelectionsAllowed;
    }

    @OverridingMethodsMustInvokeSuper
    @Override
    public void onResume() {
        super.onResume();

        if (multipleSelectionsAllowed)
            deselectAll();
    }

    protected void deselectAll() {
        for (int i = 0; i < listView.getCount(); ++i)
            listView.setItemChecked(0, false);
    }

    @OverridingMethodsMustInvokeSuper
    protected void clearSelections() {
        for (Integer pos : selectedPosition) {
            listView.setItemChecked(pos, false);
        }
        selected.clear();
        selectedPosition.clear();
    }

    public List getCurrentList() {
        return currentList;
    }

    public void setCurrentList(List currentList) {
        this.currentList = currentList;
    }

    public void updateList(List newList) {
        adapter.clear();
        adapter.addAll(newList);
        adapter.notifyDataSetChanged();
    }

    public void updateList(Object newItem) {
        currentList.add(newItem);
        adapter.add(newItem);
        adapter.notifyDataSetChanged();
    }
}
