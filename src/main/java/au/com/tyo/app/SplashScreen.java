/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD.
 *
 */
package au.com.tyo.app;

import android.os.Bundle;

import au.com.tyo.android.AndroidHelper;
import au.com.tyo.app.ui.page.PageSplashScreen;

/**
 * @author Eric Tang <eric.tang@tyo.com.au>
 */
public class SplashScreen extends CommonAppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		AndroidHelper.setFullScreenMode(this);

		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onCreatePage() {
		CommonLog.i(this, "Creating splashscreen page");

		setPage(new PageSplashScreen(getController(), this));

		super.onCreatePage();
	}

	/*
	@Override
	protected void loadPageClass() {
		getAgent().setPageClass(PageSplashScreen.class);
	}
	*/
}
