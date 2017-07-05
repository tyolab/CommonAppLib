/*
 * Copyright (C) 2017 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 */

package au.com.tyo.app.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 5/7/17.
 */

public class CommonFragmentView extends RelativeLayout {

    private int[] xyPosition = new int[2];
    private int[] xyPositionInWindow = new int[2];

    public CommonFragmentView(Context context) {
        super(context);
    }

    public CommonFragmentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CommonFragmentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CommonFragmentView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public int getScreenLocationY() {
        return xyPosition[1];
    }

    public void checkLocationOnScreen() {
        getLocationOnScreen(xyPosition);
        getLocationInWindow(xyPositionInWindow);
    }
}
