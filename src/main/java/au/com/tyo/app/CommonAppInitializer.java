package au.com.tyo.app;

/**
 * Created by Eric Tang on 12/1/17.
 */

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import au.com.tyo.android.CommonInitializer;

public class CommonAppInitializer extends MultiDexApplication {

    private static Controller controller;

    @Override
    public void onCreate() {
        super.onCreate();

        Context context = this.getApplicationContext();

        getController(context);
    }

    public static Controller getController() {
        return controller;
    }

    public static Controller getController(Context context) {
        if (null == controller || controller.isAppQuit()) {
            controller = (Controller) CommonInitializer.initializeController(context);

            if (controller == null)
                throw new IllegalStateException("Controller Impl class can't be detected");

            CommonApp.setInstance(controller);
            controller.initializeOnce();
        }

        return  controller;
    }

}