/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD.
 *
 */
package au.com.tyo.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

/**
 * @author Eric Tang <eric.tang@tyo.com.au>
 */


public class MessageReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		
		String action = intent.getAction();
		
		Log.d(MessageReceiver.class.getSimpleName(), "action: " + action);
		
		if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			
			/*
			 * info the main app, the message will sent to controller when an instance is running
			 */
			NetworkMonitor monitor = NetworkMonitor.getInstance();
	  		
			if (!intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)) {
				
				
				monitor.setNetworkConnected(true);
			//	 	monitor.checkInternetAvailability(monitor.hasInternet() );  // cannot run it in the main thread
				if (!monitor.hasInternet() && monitor.getJob() == NetworkMonitor.JOB_NONE) {
					monitor.setJob(NetworkMonitor.JOB_CHECK_INTERNET );
				}
					
//				WidgetManager.getInstance(context).checkFeed(context);
			} 
			else {
				monitor.setJob(NetworkMonitor.JOB_NONE);
				monitor.setNetworkConnected(false);
				monitor.setHasInternet(false);
				if (monitor.getController() != null)
					monitor.getController().sendMessage(Constants.MESSAGE_NETWORK_DISCONNECTED, null);
			}
		}
		/*
		* 
		mNetworkInfo = (NetworkInfo) intent
		  .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
		mOtherNetworkInfo = (NetworkInfo) intent
		  .getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);
		mReason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
		mIsFailover = intent.getBooleanExtra(
		  ConnectivityManager.EXTRA_IS_FAILOVER, false);
		if (DBG) {
		 Log.d(TAG,
		"onReceive(): mNetworkInfo="
		  + mNetworkInfo
		  + " mOtherNetworkInfo = "
		  + (mOtherNetworkInfo == null ? "[none]"
		 : mOtherNetworkInfo + " noConn="
		+ noConnectivity) + " mState="
		  + mState.toString());
		}
		// Notifiy any handlers.
		Iterator<Handler> it = mHandlers.keySet().iterator();
		while (it.hasNext()) {
		 Handler target = it.next();
		 Message message = Message.obtain(target, mHandlers.get(target));
		 // TODO add extra data
		 target.sendMessage(message);
		}
		*/
	}

}
