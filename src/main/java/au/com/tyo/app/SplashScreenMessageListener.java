/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD.
 *
 */
package au.com.tyo.app;

/**
 * @author Eric Tang <eric.tang@tyo.com.au>
 */


public interface SplashScreenMessageListener {

	void onAppInitialized();

	void onAdLoaded();

	void startBackgroundTasks();

}
