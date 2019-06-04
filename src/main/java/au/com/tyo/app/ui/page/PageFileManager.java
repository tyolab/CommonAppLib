/*
 * Copyright (c) 2019. TYONLINE TECHNOLOGY PTY. LTD. (TYOLAB)
 *
 */

package au.com.tyo.app.ui.page;

import android.app.Activity;

import au.com.tyo.app.Controller;

public class PageFileManager <T extends Controller> extends PageCommonList<T> {

    public PageFileManager(T controller, Activity activity) {
        super(controller, activity);
    }

}
