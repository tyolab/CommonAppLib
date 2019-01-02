/*
 * Copyright (c) 2018. TYONLINE TECHNOLOGY PTY. LTD. (TYOLAB)
 *
 */

package au.com.tyo.app.ui.activity;

import au.com.tyo.app.CommonActivity;
import au.com.tyo.app.ui.page.PageBackgroundProgress;

public class CommonActivityBackgroundProgress extends CommonActivity {

    @Override
    protected void loadPageClass() {
        getAgent().setPageClass(PageBackgroundProgress.class);
    }
}
