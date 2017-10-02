/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD.
 *
 */
package au.com.tyo.app;

import au.com.tyo.app.ui.PageSplashScreen;

/**
 * @author Eric Tang <eric.tang@tyo.com.au>
 */
public class SplashScreen extends CommonAppCompatActivity {

	@Override
	protected void loadPageClass() {
		getAgent().setPageClass(PageSplashScreen.class);
	}
}
