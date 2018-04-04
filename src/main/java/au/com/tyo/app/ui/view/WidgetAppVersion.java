package au.com.tyo.app.ui.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import au.com.tyo.android.AndroidUtils;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 25/3/18.
 */

public class WidgetAppVersion extends android.support.v7.widget.AppCompatTextView {

    private String appVersion;

    public WidgetAppVersion(Context context) {
        super(context);
        init(context);
    }

    public WidgetAppVersion(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WidgetAppVersion(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    private void init(Context context) {
        setAppVersion("v" + AndroidUtils.getPackageVersionName(context));
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        Context context = getContext();
        String info = getAppVersion(); // + "-build" + AndroidUtils.getPackageVersionCode(context) + "" + (BuildConfig.DEBUG ? "(Debug)" : "");
        setText(info);
    }
}
