/*
 * Copyright (C) 2017 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 */

package au.com.tyo.app.ui;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 17/7/17.
 */

public class ActionBarMenu {

    /**
     * System ActionBar
     */
    private Object actionBar;

    /**
     * AppCompat Toolbar
     */
    private Toolbar toolbar;

    private boolean initialized;

    public ActionBarMenu() {
        setInitialized(false);
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
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

    public void initializeMenuForActionBar(Object actionBar, Menu menu) {
        setActionBar(actionBar);
        setInitialized(true);
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public void setToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
    }
}
