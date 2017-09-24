package au.com.tyo.app.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

import au.com.tyo.android.DialogFactory;
import au.com.tyo.app.Controller;
import au.com.tyo.app.R;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 23/9/17.
 */

public class PageLocation extends Page {


    /**
     * @param controller
     * @param context
     */
    public PageLocation(Controller controller, Activity context) {
        super(controller, context);

    }

}
