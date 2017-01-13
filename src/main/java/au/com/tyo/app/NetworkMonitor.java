/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD.
 *
 */
package au.com.tyo.app;

import android.content.Context;
import android.util.Log;
import au.com.tyo.android.AndroidSettings;
import au.com.tyo.services.Internet;

/**
 * @author Eric Tang <eric.tang@tyo.com.au>
 */


public class NetworkMonitor implements Runnable {
	
	public static final String LOG_TAG = "NetworkMonitor";
	
	public static final int JOB_NONE = -1;
	public static final int JOB_CHECK_INTERNET = 0;
	public static final int JOB_CHECK_NETWORK = 1;
	
	private int job;
	
	private boolean hasInternet;
	private boolean networkConnected;
	
	private static NetworkMonitor monitor;
	private static Thread thread;
	
	private Controller controller;

	public synchronized boolean hasInternet() {
		return hasInternet;
	}
	
	public NetworkMonitor(Controller controller) {
		this();
		this.controller = controller;
	}
	
	public NetworkMonitor() {
		job = JOB_CHECK_NETWORK; // the first thing when the job starts to run
		this.hasInternet = false;
	}
	
	public synchronized void setHasInternet(boolean haveIt) {
		this.hasInternet = haveIt;
	}

	public boolean isNetworkConnected() {
		return networkConnected;
	}

	public void setNetworkConnected(boolean networkConnected) {
		this.networkConnected = networkConnected;
	}
	
	public static NetworkMonitor getInstance(){
		return getInstance(null);
	}
	
	public static NetworkMonitor getInstance(Controller controller) {
		if (monitor == null) {
			monitor = new NetworkMonitor(controller);
		}
		return monitor;
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}

	public void start() {
		if (thread == null)
			thread = new Thread(getInstance());
		thread.setName(LOG_TAG);
		if (!thread.isAlive()) {
			thread.setDaemon(true);
			thread.start();
		}
	}

	public synchronized int getJob() {
		return job;
	}

	public synchronized void setJob(int job) {
		this.job = job;
	}
	
	@Override
	public void run() {
		while (true)
		{
    		try
    		{
        		switch (monitor.getJob()) {
        		case JOB_CHECK_NETWORK:
        			if (controller != null && controller.getContext() != null) {
        				boolean connected = checkNetworkState(controller.getContext());
        				monitor.setHasInternet(connected);  // assuming has network has internet
        				monitor.setNetworkConnected(connected);
        			}
        			job = JOB_CHECK_INTERNET;
        			break;
        		case  JOB_CHECK_INTERNET:
        			checkInternetAvailability(monitor.hasInternet() );
        			if (monitor.hasInternet() || !isNetworkConnected()) {
        				monitor.setJob(JOB_NONE );
//        				thread = null;
//        				return;
        			}
        			break;
        		case JOB_NONE:
        		default:
        			break;
        		}
        		
        		Thread.sleep(2000);  // pause 2 second
    		} catch (InterruptedException e) {
    			Log.e(LOG_TAG, "something tried to stop me");
			}
    		catch(Exception e)
    		{   
    			Log.e(LOG_TAG, e.getMessage());
    		}
    		finally {
    			
    		}
		}
	}
	
	public void checkInternetAvailability(boolean previousState) {
		
		monitor.setHasInternet(checkInternet());
		
		if (monitor.hasInternet()/* && monitor.hasInternet() != previousState*/ && controller != null) {
            controller.sendMessage(Constants.MESSAGE_NETWORK_READY, null);
		}
	}

	public Controller getController() {
		return controller;
	}
	
	public static boolean checkNetworkState(Context context) {
		return AndroidSettings.hasInternet(context);
	}
	
	public static boolean checkInternet() {
		try {
			return Internet.checkAvailability();
		} catch (Exception e) {
			Log.e(LOG_TAG, "oops, something wrong when connecting to the Internet.");
		}
		return false;
	}
}
