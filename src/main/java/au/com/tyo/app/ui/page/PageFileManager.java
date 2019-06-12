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

import au.com.tyo.android.adapter.QuickAccessListAdapter;
import au.com.tyo.app.Controller;
import au.com.tyo.app.R;
import au.com.tyo.app.adapter.FileListItemFactory;
import au.com.tyo.io.WildcardFileStack;

public class PageFileManager <T extends Controller> extends PageCommonList<T> implements AdapterView.OnItemClickListener {

    private String rootPath;

    private String currentPath;

    private LinkedList<String> paths;

    private WildcardFileStack fileList;

    protected String currentFolderName;

    protected int currentFileCount;

    protected int currentFolderCount;

    /**
     * UI elements
     */
    private TextView tvEmptyListHint;

    public PageFileManager(T controller, Activity activity) {
        super(controller, activity);

        setContentViewResId(R.layout.page_file_manager);
        // setOnItemClickListener(this);

        paths = new LinkedList();
        currentFolderCount = 0;
        currentFileCount = 0;
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

        QuickAccessListAdapter adapter = getQuickAccessListAdapter();
        if (null != adapter)
            adapter.setItemFactory(new FileListItemFactory(getActivity()));
    }

    protected void setFileManagerTitle(String title) {
        setPageTitleOnToolbar(null != title ? title : getTitle());
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
                getListView().setItemChecked(position, false);

                paths.push(currentFolderName = file.getName());

                setCurrentList(null);
                startBackgroundTask();
                return;
            }
        }

        super.onItemClick(parent, view, position, id);
    }

    @Override
    protected void onPageBackgroundTaskFinished(int id) {
        super.onPageBackgroundTaskFinished(id);

        if (null != fileList) {
            getQuickAccessListAdapter().clear();
            getQuickAccessListAdapter().addAll(fileList);
            getQuickAccessListAdapter().notifyDataSetChanged();
        }

        if (null == getCurrentList() || getCurrentList().size() == 0) {
            tvEmptyListHint.setVisibility(View.VISIBLE);
            getListView().setVisibility(View.GONE);
        }
        else {
            tvEmptyListHint.setVisibility(View.GONE);
            getListView().setVisibility(View.VISIBLE);
        }

        setFileManagerTitle(currentFolderName);
    }

    @Override
    public void run() {
        currentPath = createCompletePath();

        if (!TextUtils.isEmpty(currentPath) && null == getCurrentList()) {
            fileList = new WildcardFileStack(new File(currentPath));
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

        startBackgroundTask();
    }

    @Override
    public boolean onBackPressed() {
        clearSelections();
        setCurrentFolderName(null);

        if (paths.size() > 0) {
            paths.pop();

            if (paths.size() > 0)
                setCurrentFolderName(paths.getFirst());

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
           String path = paths.get(i);
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
}
