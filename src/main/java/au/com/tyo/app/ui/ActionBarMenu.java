/*
 * Copyright (C) 2017 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 */

package au.com.tyo.app.ui;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

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

    private Menu menu;

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

    public boolean initializeMenuForActionBar(Object actionBar, Menu menu) {
        setActionBar(actionBar);
        setInitialized(true);
        return true;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public void setToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
    }

    public void setupMenu(Menu menu, MenuItem.OnMenuItemClickListener onMenuItemClickListener) {
        this.menu = menu;

        for (int i = 0; i < menu.size(); ++i) {
            MenuItem menuItem = menu.getItem(i);
            menuItem.setOnMenuItemClickListener(onMenuItemClickListener);
        }
    }

    public void hide() {
        if (null != getSupportActionBar())
            getSupportActionBar().hide();
        else if (null != getActionBar())
            getActionBar().hide();
    }

    public void show() {
        if (null != getSupportActionBar())
            getSupportActionBar().show();
        else if (null != getActionBar())
            getActionBar().show();
    }
}
