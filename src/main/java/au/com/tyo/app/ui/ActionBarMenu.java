/*
 * Copyright (C) 2017 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 */

package au.com.tyo.app.ui;

import android.support.v7.app.ActionBar;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 17/7/17.
 */

public class ActionBarMenu {

    /**
     * System actionBar
     */
    private Object actionBar;


    public ActionBarMenu() {
    }

    public void setActionBar(Object actionBar) {
        this.actionBar = actionBar;
    }

    public android.support.v7.app.ActionBar getSupportActionBar() {
        if (actionBar instanceof android.support.v7.app.ActionBar)
            return (ActionBar) actionBar;
        return null;
    }

    public android.app.ActionBar getActionBar() {
        if (actionBar instanceof android.app.ActionBar)
            return (android.app.ActionBar) actionBar;
        return null;
    }
}
