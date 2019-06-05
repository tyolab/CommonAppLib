/*
 * Copyright (c) 2019. TYONLINE TECHNOLOGY PTY. LTD. (TYOLAB)
 *
 */

package au.com.tyo.app.ui.activity;

import au.com.tyo.app.Controller;
import au.com.tyo.app.ui.page.PageFileManager;

public class CommonActivityFileManager<ControllerType extends Controller> extends CommonActivityList<ControllerType> {

    @Override
    protected void createPage() {
        super.createPage();

        if (null == getPage())
            setPage(new PageFileManager(getController(), this));
    }
}
