package au.com.tyo.app.ui.page;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import au.com.tyo.app.ui.UIEntity;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 15/8/17.
 *
 * PageFragment is a only a part of page just like the way of Fragment
 */

public class PageFragment implements UIEntity {

    private ViewGroup contentView;

    /**
     * The default content view layout resource id
     */
    private int contentViewResId = -1;

    @Override
    public void onActivityStart() {

    }

    public void hideContentView() {
        contentView.setVisibility(View.GONE);
    }

    public void showContentView() {
        contentView.setVisibility(View.VISIBLE);
    }

    public void setContentView(ViewGroup contentView) {
        this.contentView = contentView;
    }

    public ViewGroup getContentView() {
        return contentView;
    }

    public int getContentViewResId() {
        return contentViewResId;
    }

    public void setContentViewResId(int contentViewResId) {
        this.contentViewResId = contentViewResId;
    }

    public void loadContentView(Context context) {
        loadContentView(LayoutInflater.from(context));
    }

    public void loadContentView(LayoutInflater inflater) {
        loadContentView(inflater, null);
    }

    public void loadContentView(LayoutInflater inflater, ViewGroup parent) {
        if (contentViewResId > -1) {
            if (null == contentView && null != parent)
                contentView = (ViewGroup) inflater.inflate(contentViewResId,
                        parent, true);
            else {
                contentView.removeAllViews();
                inflater.inflate(contentViewResId, contentView, true);
            }
        }
    }
}
