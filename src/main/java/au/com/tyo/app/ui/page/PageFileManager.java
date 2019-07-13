/*
 * Copyright (c) 2019. TYONLINE TECHNOLOGY PTY. LTD. (TYOLAB)
 *
 */

package au.com.tyo.app.ui.page;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import au.com.tyo.android.adapter.QuickAccessListAdapter;
import au.com.tyo.app.Controller;
import au.com.tyo.app.R;
import au.com.tyo.app.adapter.FileListItemFactory;
import au.com.tyo.io.WildcardFileStack;

public class PageFileManager <T extends Controller> extends PageCommonList<T> implements AdapterView.OnItemClickListener {

    private String rootPath;

    private String currentPath;

    protected LinkedList paths;

    // private WildcardFileStack fileList;

    protected String currentFolderName;

    protected int currentFileCount;

    protected int currentFolderCount;

    /**
     * UI elements
     */
    private TextView tvEmptyListHint;

    private boolean refreshOnResume;

    public PageFileManager(T controller, Activity activity) {
        super(controller, activity);

        setContentViewResId(R.layout.file_list_viewer);
        // setOnItemClickListener(this);

        paths = new LinkedList();
        currentFolderCount = 0;
        currentFileCount = 0;
        setRefreshOnResume(true);
    }

    public boolean isRefreshOnResume() {
        return refreshOnResume;
    }

    public void setRefreshOnResume(boolean refreshOnResume) {
        this.refreshOnResume = refreshOnResume;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    @Override
    public void setupComponents() {
        super.setupComponents();

        tvEmptyListHint = findViewById(R.id.tv_empty_list_hint);
    }

    @Override
    public void onActivityStart() {
        super.onActivityStart();

        // currentFileList = getQuickAccessListAdapter().getItems();

        if (null == rootPath && getBaseAdapter().getCount() > 0) {
            if (getBaseAdapter().getItem(0) instanceof File) {
                File file = (File) getBaseAdapter().getItem(0);
                // if (file.isDirectory())  setFileManagerTitle(currentFolderName = file.getName());
                rootPath = file.getParent();
            }
            // more conditions
        }

        setupListAdapter();
    }

    protected void setupListAdapter() {
        // in case a custom adapter is needed
        QuickAccessListAdapter adapter = getQuickAccessListAdapter();
        setupListItemFactory(adapter);
    }

    protected void setupListItemFactory(QuickAccessListAdapter adapter) {
        if (null != adapter)
            adapter.setItemFactory(new FileListItemFactory(getActivity()));
    }

    protected void setFileManagerTitle(String title) {
        if (null == title)
            title = getTitle();

        if (null != title)
            setPageTitleOnToolbar(title);
    }

    public void setCurrentFolderName(String currentFolderName) {
        this.currentFolderName = currentFolderName;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object item = getListItem(position);

        if (item instanceof File) {
            File file = (File) item;
            if (file.isDirectory()) {
                onDirectoryClick(position, file.getName());
                return;
            }
        }

        super.onItemClick(parent, view, position, id);
    }

    protected void onDirectoryClick(int position, String name) {
        getListView().setItemChecked(position, false);

        pushPath(currentFolderName = name);

        setCurrentList(null);
        startBackgroundTask();
    }

    public void pushPath(Object pathItem) {
        paths.push(pathItem);
    }

    @Override
    protected void onPageBackgroundTaskFinished(int id) {
        super.onPageBackgroundTaskFinished(id);

        updateList(getCurrentList());

        updateEmptyListHintState();

        setFileManagerTitle(currentFolderName);
    }

    private void updateEmptyListHintState() {
        updateEmptyListHintState((null == getCurrentList() || getCurrentList().size() == 0));
    }

    public void updateEmptyListHintState(boolean visible) {
        if (visible) {
            if (null != tvEmptyListHint)
                tvEmptyListHint.setVisibility(View.VISIBLE);
            if (null != getListView())
                getListView().setVisibility(View.GONE);
        }
        else {
            if (null != tvEmptyListHint)
                tvEmptyListHint.setVisibility(View.GONE);
            if (null != getListView())
                getListView().setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void run() {
        list();
    }

    protected void list() {
        currentPath = createCompletePath();

        if (!TextUtils.isEmpty(currentPath) && null == getCurrentList()) {
            WildcardFileStack fileList = new WildcardFileStack(new File(currentPath));
            fileList.listFiles();

            for (int i = 0; i < fileList.size(); ++i) {
                File file = fileList.get(i);
                if (file.isDirectory())
                    ++currentFolderCount;
                else if (file.isFile())
                    ++currentFileCount;
            }

            setCurrentList(fileList);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isRefreshOnResume())
            startBackgroundTask();
    }

    @Override
    public boolean onBackPressed() {
        clearSelections();
        setCurrentFolderName(null);

        if (paths.size() > 0) {
            paths.pop();

            if (paths.size() > 0)
                setCurrentFolderName(paths.getFirst().toString());

            // getSelected().clear();
            // deselectAll();
            //
            // getQuickAccessListAdapter().clear();
            // getQuickAccessListAdapter().notifyDataSetChanged();
            // getQuickAccessListAdapter().notifyDataSetInvalidated();
            setCurrentList(null);
            startBackgroundTask();
            return true;
        }
        return super.onBackPressed();
    }

    // @Override
    // protected boolean onHomeButtonClick() {
    //     finish();
    //     return true;
    // }

    private String generatePaths() {
       StringBuffer sb = new StringBuffer();
       for (int i = paths.size() - 1; i > -1; --i) {
           String path = paths.get(i).toString();
           if (sb.length() > 0)
               sb.append(File.separatorChar);

           sb.append(path);
       }
       return sb.toString();
    }

    private String createCompletePath() {
        return (rootPath != null ? rootPath + File.separatorChar : "") + generatePaths();
    }

    @Override
    public void showProgressBar() {
        // super.showProgressBar();
    }

    @Override
    protected void clearSelections() {
        super.clearSelections();
        currentFolderCount = 0;
        currentFileCount = 0;
    }

    @Override
    public void updateList(List newList) {
        super.updateList(newList);
        updateEmptyListHintState();
    }

    @Override
    public void updateList(Object newItem) {
        super.updateList(newItem);
        updateEmptyListHintState();
    }

    public void notifiyDataSetChanged() {
        updateEmptyListHintState(null == getCurrentList() || getCurrentList().size() == 0);
        getQuickAccessListAdapter().notifyDataSetChanged();
    }
}
