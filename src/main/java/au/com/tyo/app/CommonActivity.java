package au.com.tyo.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by monfee on 17/5/17.
 */

public class CommonActivity extends AppCompatActivity {

    protected Controller controller;
    protected CommonActivityAgent initializer = new CommonActivityAgent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initializer.preInitialize(savedInstanceState);

        super.onCreate(savedInstanceState);

        initializer.onCreate(this, savedInstanceState);
    }
}
