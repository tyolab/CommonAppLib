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

    private LocationManager locationManager;

    private List<Location> locations;

    /**
     *
     */
    private Location lastKnownLocation;

    /**
     * @param controller
     * @param context
     */
    public PageLocation(Controller controller, Activity context) {
        super(controller, context);

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locations = new ArrayList<>();
    }

    private boolean checkLocation() {
        if(!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private void showAlert() {
        final AlertDialog.Builder builder = DialogFactory.createDialogBuilder(
                getActivity(),
                R.style.CommonAlertDialog_Light,
                getActivity().getResources().getString(R.string.enable_location_title),
                getActivity().getResources().getString(R.string.enable_location_message),
                null,
                null);

        builder.setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(getActivity(), intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });

        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void requestGPSLocationUpdates(LocationListener locationListener) {
        requestLocationUpdates(LocationManager.GPS_PROVIDER, locationListener);
    }

    public void requestLocationUpdates(String provider, LocationListener locationListener) {
        locationManager.requestLocationUpdates(
                provider, 5 * 60 * 1000 /* 5 minutes */, 10, locationListener);
    }

    public void startTracking(String provider, LocationListener locationListener) {
        if (null == lastKnownLocation)
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (null == lastKnownLocation)
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (null != locationListener)
            requestLocationUpdates(provider, locationListener);
    }
}
