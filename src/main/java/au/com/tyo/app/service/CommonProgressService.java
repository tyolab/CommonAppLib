package au.com.tyo.app.service;

import android.util.Log;

import au.com.tyo.android.services.CommonIntentService;
import au.com.tyo.app.CommonAppInitializer;
import au.com.tyo.app.Controller;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 11/1/19.
 */
public class CommonProgressService<T extends Controller> extends CommonIntentService {


    private static final String TAG = CommonProgressService.class.getSimpleName();

    protected T controller;
    protected int currentProgress;

    @Override
    public void onCreate() {
        super.onCreate();

        if (null == controller)
            controller = CommonAppInitializer.getController(this);
    }

    protected void updateProgress(int progress) {
        if (progress > currentProgress) {
            Log.d(TAG, "data processing progress: " + (currentProgress = progress));
            broadcastProgress();
        }
    }

    protected void broadcastProgress() {
        controller.broadcastMessageBackgroundTaskProgress(currentProgress);
    }

}
