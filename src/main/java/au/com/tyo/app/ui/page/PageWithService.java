package au.com.tyo.app.ui.page;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import au.com.tyo.android.Constants;
import au.com.tyo.android.services.CommonIntentService;
import au.com.tyo.app.Controller;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 12/2/18.
 */

public class PageWithService extends Page {

    private Messenger serviceMessenger = null;

    private boolean sIsRunning = false;

    /**
     * @param controller
     * @param activity
     */
    public PageWithService(Controller controller, Activity activity) {
        super(controller, activity);


    }

    private ServiceConnection connection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            serviceMessenger = new Messenger(service);
            sendMessage(au.com.tyo.app.Constants.MESSAGE_SERVICE_REGISTER_CLIENT);
        }

        public void onServiceDisconnected(ComponentName className) {
            serviceMessenger = null;
        }
    };

    protected void sendMessage(int message) {
        if (null != serviceMessenger)
            try {
                Message msg = Message.obtain(null, message);
                // msg.replyTo = mMessenger;
                serviceMessenger.send(msg);
            }
            catch (RemoteException e) {
            }
    }

    public void handlerService(Activity context, Class cls, PendingIntent pendingIntent, boolean toStart) {
        Intent locationIntent = new Intent(context, cls);

        // Build PendingIntent used to open this activity from
        // Notification

        if (toStart) {
            if (!sIsRunning) {
                if (null != pendingIntent)
                    locationIntent.putExtra(CommonIntentService.EXTRA_PENDING_INTENT, pendingIntent);

                context.startService(locationIntent);
                context.bindService(locationIntent, connection, Context.BIND_AUTO_CREATE);
                sIsRunning = true;
            }
        }
        else {
            if (null != serviceMessenger) {
                sendMessage(Constants.MESSAGE_SERVICE_UNREGISTER_CLIENT);
                context.unbindService(connection);
            }

            context.stopService(locationIntent);
        }
    }
}
