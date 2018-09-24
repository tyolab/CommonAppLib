package au.com.tyo.app.ui;

import android.content.Intent;

import au.com.tyo.app.PageAgent;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 15/8/17.
 */

public interface UIEntity {

    void onActivityStart();

    void handleIntent(Intent intent);
}
