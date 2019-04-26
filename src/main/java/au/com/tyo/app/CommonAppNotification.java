/*
 * Copyright (c) 2019. TYONLINE TECHNOLOGY PTY. LTD. (TYOLAB)
 *
 */

package au.com.tyo.app;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import au.com.tyo.android.CommonNotification;
import au.com.tyo.app.ui.activity.CommonActivityBackgroundProgress;

public abstract class CommonAppNotification extends CommonNotification {

    private static Class backgroundActivityClass = CommonActivityBackgroundProgress.class;

    public static void setBackgroundActivityClass(Class backgroundActivityClass) {
        CommonAppNotification.backgroundActivityClass = backgroundActivityClass;
    }

    public CommonAppNotification(Context ctx, CharSequence applicationLabel) {
        super(ctx, applicationLabel);
    }

    public PendingIntent createDataProcessingPageIntent(Context context, int notificationId, int state) {
        Intent intentToLaunchThisActivityFromNotification = new Intent(
                context, getPendingIntentClassByState(state));
        intentToLaunchThisActivityFromNotification.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                notificationId, intentToLaunchThisActivityFromNotification,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
        return pendingIntent;
    }

    /**
     * Override me when needing providing different activity class for returning for different states
     *
     * @param state
     * @return
     */
    public Class getPendingIntentClassByState(int state) {
        return backgroundActivityClass;
    }
}
