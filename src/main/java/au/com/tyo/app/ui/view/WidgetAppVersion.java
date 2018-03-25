package au.com.tyo.app.ui.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import au.com.tyo.android.AndroidUtils;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 25/3/18.
 */

public class WidgetAppVersion extends android.support.v7.widget.AppCompatTextView {

    public WidgetAppVersion(Context context) {
        super(context);
    }

    public WidgetAppVersion(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public WidgetAppVersion(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        Context context = getContext();
        String info =  "v" + AndroidUtils.getPackageVersionName(context); // + "-build" + AndroidUtils.getPackageVersionCode(context) + "" + (BuildConfig.DEBUG ? "(Debug)" : "");
        setText(info);
    }
}
