/*
 * Copyright (C) 2017 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 */

package au.com.tyo.app.ui;

import android.support.v7.app.ActionBar;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.lang.reflect.Method;

import static android.content.ContentValues.TAG;

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

    private Integer textColor = null;

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

    public boolean initializeMenuForActionBar(Menu menu) {
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

    public void setMenuTitle(int id, String title) {
        MenuItem menuITem = menu.findItem(id);
        if (null != menuITem) {
            setMenuItemTitle(menuITem, title);
        }
        else
            Log.e(TAG, "Menu item with id " + id + " can't not be found");
    }

    /**
     * or you can change the menu item text color in the theme
     * <item name="android:actionMenuTextColor">@color/menuTextColor</item>
     *
     * @param menuTextColor
     */
    public void setMenuTextColor(Integer menuTextColor) {
        if (null == menuTextColor || null == menu)
            return;

//        if (null != toolbar)
//            toolbar.setTitleTextColor(menuTextColor);

        textColor = menuTextColor;
        for (int i = 0; i < menu.size(); ++i) {
            MenuItem menuItem = menu.getItem(i);
            String title = menuItem.getTitle().toString();

            setMenuItemTitle(menuItem, title);
        }
    }

    public void setMenuItemTitle(int menuItemId, String title) {
        MenuItem item = this.menu.findItem(menuItemId);
        if (null != item)
            setMenuItemTitle(item, title);
    }

    public void setMenuItemTitle(MenuItem menuItem, String title) {
        if (title.length() == 0)
            return;

        if (null == textColor)
            menuItem.setTitle(title);
        else {
            SpannableString spanString = new SpannableString(title);
            spanString.setSpan(new ForegroundColorSpan(textColor), 0, spanString.length(), 0); //fix the color to white
            menuItem.setTitle(spanString);
        }
    }

    public void setMenuItemVisible(int itemId, boolean visible) {
        MenuItem item = this.menu.findItem(itemId);
        if (null != item)
            item.setVisible(visible);
    }

    public void showOptionMenuIcon() {
        if(menu instanceof MenuBuilder){
            MenuBuilder menuBuilder = (MenuBuilder) menu;
            try{
                Method m = menu.getClass().getDeclaredMethod(
                        "setOptionalIconsVisible", Boolean.TYPE);
                m.setAccessible(true);
                m.invoke(menu, true);
            }
            catch(NoSuchMethodException e){
                Log.e(TAG, "showOptionMenuIcon", e);
            }
            catch(Exception e){
                throw new RuntimeException(e);
            }
        }
    }

    public void hideMenuItem(int menuItemId) {
        setMenuItemVisible(menuItemId, false);
    }

    public boolean isMenuInitialised() {
        return menu != null;
    }
}
