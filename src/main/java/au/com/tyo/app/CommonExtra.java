package au.com.tyo.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import java.util.ArrayList;

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

    private Intent intent;

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

        if (null != intent)
            setParcelExtra();
    }

    private void setParcelExtra() {
        setParcelExtra(Constants.DATA, parcel);
        this.parcel = null;
    }

    public void setParcelExtra(String key, Object parcel) {
        putExtra(intent, key, parcel);
        this.parcel = null;
    }

    public void createIntent(Context context) {
        if (activityClass != null)
            intent = new Intent(context, activityClass);
        else
            throw new IllegalArgumentException("Activity class must be set before setting an extra");
    }

    public void setExtra(String key, Object extra) {
        putExtra(intent, key, extra);
    }

    public void setExtra(Context context, String key, Object extra) {
        if (intent == null) {
            createIntent(context);
        }
        putExtra(intent, key, extra);
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

    public static void putExtra(Intent intent, String key, Object data) {
        if (data != null) {
            if (data instanceof String) {
                String value = (String) data;
                intent.putExtra(key, value);
            } else if (data instanceof Boolean) {
                Boolean value = (Boolean) data;
                intent.putExtra(key, value);
            } else if (data instanceof Integer) {
                Integer value = (Integer) data;
                intent.putExtra(key, value);
            } else if (data instanceof Long) {
                Long value = (Long) data;
                intent.putExtra(key, value);
            } else if (data instanceof Parcelable) {
                Parcelable value = (Parcelable) data;
                intent.putExtra(key, value);
            } else if (data instanceof Bundle) {
                Bundle bundle = (Bundle) data;
                intent.putExtra(key, bundle);
            } else if (data instanceof Object[]) {
                Object[] array = (Object[]) data; // ((List) data).toArray();
                intent.putExtra(key, array);
            } else if (data instanceof ArrayList) {
                ArrayList list = (ArrayList) data;
                intent.putExtra(key, list);
            } else {
                // noting
                throw new IllegalArgumentException("Unsupported data type: " + data.getClass().getSimpleName());
            }
        }
    }

    public Intent getIntent() {
        return intent;
    }
}
