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

    public static Controller getController(Context context) {
        if (null == controller) {
            controller = (Controller) CommonInitializer.initializeController(context);
            CommonApp.setInstance(controller);
        }

        return  controller;
    }

}