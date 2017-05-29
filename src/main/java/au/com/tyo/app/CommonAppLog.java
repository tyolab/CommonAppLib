package au.com.tyo.app;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by monfee on 17/4/17.
 */

public class CommonAppLog {

    public static void error(String tag, Exception e) {
        error(tag, e, "");
    }

    public static void error(String tag, Exception e, String alternativeMessage) {
        Log.e(tag, alternativeMessage + " due to " + e.getClass().getSimpleName());
        if (e.getMessage() != null)
            Log.e(tag, e.getMessage());
    }

    public static void makeToast(Context context, Exception e) {
        String message = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
        makeToast(context, message);
    }

    public static void makeToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG);
    }

    public static void makeToast(String message) {
        Context context = ((Controller) CommonApp.getInstance()).getCurrentActivity();
        makeToast(context, message);
    }
}
