package au.com.tyo.app;

import android.content.Intent;
import android.view.View;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 17/7/17.
 */

public class CommonExtra {

    public static final int DEFAULT_REQUEST_CODE = 88;

    private Class activityClass;

    private String key;

    private Object parcel;

    private int requestCode;

    private int resultCode;

    private int flags;

    private View fromView;

    public CommonExtra(Class activityClass) {
        this(activityClass, Constants.DATA, null);
    }

    public CommonExtra(Class activityClass, String key, Object extra) {
        this(activityClass, key, extra, -1);
    }

    public CommonExtra(Class activityClass, String key, Object extra, int requestCode) {
        this.activityClass = activityClass;
        this.key = key;
        this.parcel = extra;
        this.requestCode = requestCode;
        this.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP;
    }

    public CommonExtra() {
        this(null);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getParcel() {
        return parcel;
    }

    public void setParcel(Object parcel) {
        this.parcel = parcel;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public View getFromView() {
        return fromView;
    }

    public void setFromView(View fromView) {
        this.fromView = fromView;
    }

    public Class getActivityClass() {
        return activityClass;
    }

    public void setActivityClass(Class activityClass) {
        this.activityClass = activityClass;
    }

    public void setRequestResultWithDefaultCode() {
        this.requestCode = DEFAULT_REQUEST_CODE;
    }
}
