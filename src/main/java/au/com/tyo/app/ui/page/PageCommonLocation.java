package au.com.tyo.app.ui.page;

import android.app.Activity;

import au.com.tyo.android.CommonPermission;
import au.com.tyo.app.Controller;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 23/9/17.
 */

public class PageCommonLocation<ControllerType extends Controller> extends Page<ControllerType> {


    /**
     * @param controller
     * @param activity
     */
    public PageCommonLocation(ControllerType controller, Activity activity) {
        super(controller, activity);
    }

    @Override
    public void onActivityStart() {
        super.onActivityStart();

        CommonPermission.checkLocationPermissions(getActivity());
    }
}
