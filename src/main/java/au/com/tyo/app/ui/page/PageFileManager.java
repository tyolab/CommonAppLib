/*
 * Copyright (c) 2019. TYONLINE TECHNOLOGY PTY. LTD. (TYOLAB)
 *
 */

package au.com.tyo.app.ui.page;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import au.com.tyo.android.adapter.QuickAccessListAdapter;
import au.com.tyo.app.Controller;
import au.com.tyo.app.adapter.FileListItemFactory;
import au.com.tyo.io.WildcardFileStack;

public class PageFileManager <T extends Controller> extends PageCommonList<T> implements AdapterView.OnItemClickListener {

    private String rootPath;

    private String currentPath;

    private LinkedList<String> paths;

    private WildcardFileStack fileList;

    public PageFileManager(T controller, Activity activity) {
        super(controller, activity);

        // setOnItemClickListener(this);

        paths = new LinkedList();
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    @Override
    public void onActivityStart() {
        super.onActivityStart();

        // currentFileList = getQuickAccessListAdapter().getItems();

        if (null == rootPath && getBaseAdapter().getCount() > 0) {
            if (getBaseAdapter().getItem(0) instanceof File) {
                File file = (File) getBaseAdapter().getItem(0);
                setFileManagerTitle(file.getName());
                rootPath = file.getParent();
            }
            // more conditions
        }

        QuickAccessListAdapter adapter = getQuickAccessListAdapter();
        if (null != adapter)
            adapter.setItemFactory(new FileListItemFactory(getActivity()));
    }

    protected void setFileManagerTitle(String title) {
        setPageTitleOnToolbar(title);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object item = getListItem(position);

        if (item instanceof File) {
            File file = (File) item;
            if (file.isDirectory()) {
                getListView().setItemChecked(position, false);

                paths.push(file.getName());
                setFileManagerTitle(file.getName());
                startBackgroundTask();
                return;
            }
        }

        super.onItemClick(parent, view, position, id);
    }

    @Override
    protected void onPageBackgroundTaskFinished(int id) {
        super.onPageBackgroundTaskFinished(id);

        getQuickAccessListAdapter().clear();
        getQuickAccessListAdapter().addAll(fileList);
        getQuickAccessListAdapter().notifyDataSetChanged();
    }

    @Override
    public void run() {
        currentPath = createCompletePath();

        if (!TextUtils.isEmpty(currentPath)) {
            fileList = new WildcardFileStack(new File(currentPath));
            fileList.listFiles();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        startBackgroundTask();
    }

    @Override
    public boolean onBackPressed() {
        if (paths.size() > 0) {
            paths.pop();
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
}
