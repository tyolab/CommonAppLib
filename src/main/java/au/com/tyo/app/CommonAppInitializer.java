package au.com.tyo.app;

/**
 * Created by Eric Tang on 12/1/17.
 */

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import au.com.tyo.android.CommonInitializer;

public class CommonAppInitializer<T extends Controller> extends MultiDexApplication {

    private static final String TAG = "CommonAppInitializer";

    private static Controller controller;

    @Override
    public void onCreate() {
        super.onCreate();

        initialize(this);

        controller.setApplication(this);
    }

    public static<T> T getController() {
        return (T) controller;
    }

    public static<T> T getController(Context context) {
        return getController(context, false);
    }

    public static<T> T getController(Context context, boolean initializeBackground) {
        if (null == controller || controller.isAppQuit()) {
            controller = (Controller) CommonInitializer.initializeController(context, true, initializeBackground);

            if (controller == null)
                throw new IllegalStateException("Controller Impl class can't be detected");

            CommonApp.setInstance(controller);
            controller.initializeOnce();
        }

        return getController();
    }

    public static void initialize(Application application) {
        Context context = application.getApplicationContext();
        getController(context);
    }

    public static Controller getController(Application application) {
        if (null == controller) {
            initialize(application);
        }
        return getController();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        if (null != controller)
            controller.onTerminate();

        Log.d(TAG, "App terminated");
    }


}