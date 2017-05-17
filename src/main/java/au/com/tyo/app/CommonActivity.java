package au.com.tyo.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import au.com.tyo.android.CommonInitializer;

/**
 * Created by monfee on 17/5/17.
 */

public class CommonActivity extends AppCompatActivity {

    protected Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (controller == null) {
            if (CommonApp.getInstance() == null)
                CommonApp.setInstance(CommonInitializer.initializeInstance(CommonApp.class, this));
            controller = (Controller) CommonApp.getInstance();
        }

        controller.setCurrentActivity(this);
        controller.setContext(this);
    }
}
