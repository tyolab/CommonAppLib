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

    public CommonAppNotification(Context ctx, CharSequence applicationLabel) {
        super(ctx, applicationLabel);
    }

    public static PendingIntent createDataProcessingPageIntent(Context context, int notificationId) {
        Intent intentToLaunchThisActivityFromNotification = new Intent(
                context, CommonActivityBackgroundProgress.class);
        intentToLaunchThisActivityFromNotification.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                notificationId, intentToLaunchThisActivityFromNotification,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
        return pendingIntent;
    }
}
