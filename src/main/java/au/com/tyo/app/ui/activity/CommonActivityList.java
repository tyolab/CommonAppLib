package au.com.tyo.app.ui.activity;


import au.com.tyo.app.CommonActivity;
import au.com.tyo.app.ui.page.PageCommonList;
import au.com.tyo.app.ui.UIList;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 27/7/17.
 *
 */

public class CommonActivityList extends CommonActivity {

    @Override
    protected void createPage() {
        super.createPage();

        if (null == getPage())
            setPage(new PageCommonList(getController(), this));
    }

    public UIList getListPage() {
        return (UIList) getPage();
    }

}
