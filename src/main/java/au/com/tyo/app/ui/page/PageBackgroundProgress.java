/*
 * Copyright (c) 2018. TYONLINE TECHNOLOGY PTY. LTD. (TYOLAB)
 *
 */

package au.com.tyo.app.ui.page;

import android.app.Activity;
import android.os.Message;
import android.widget.ProgressBar;
import android.widget.TextView;

import au.com.tyo.app.Constants;
import au.com.tyo.app.Controller;
import au.com.tyo.app.R;

public class PageBackgroundProgress<T extends Controller> extends Page<T> {

    public final static String PROGRESS_INFO_TEMPLATE = "Data processing \\%%d";

    private ProgressBar progressBar;

    private TextView tvPercent;

    private static final int[] PERCENTS_V1 = new int[] {0, 10, 60, 100};

    private static final int[] PERCENTS_V2 = new int[] {0, 30, 100};

    private int progress;

    private Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            progressBar.setProgress(progress);
            updateProgressInfo();
        }
    };

    protected void updateProgressInfo() {
        tvPercent.setText("Data processing... %" + progress + "");
    }

    /**
     * @param controller
     * @param activity
     */
    public PageBackgroundProgress(T controller, Activity activity) {
        super(controller, activity);

        setContentViewResId(R.layout.page_background_progress);
        setMessageReceiverRequired(true);
    }

    @Override
    public void setupComponents() {
        super.setupComponents();

        progressBar = (ProgressBar) findViewById(R.id.progress_bar_background_service);

        tvPercent = (TextView) findViewById(R.id.tv_data_unpack_percentage);
    }

    @Override
    protected void handleBroadcastMessage(Message msg) {
        if (msg.what == Constants.MESSAGE_BROADCAST_BACKGROUND_PROGRESS) {
            progress = (int) msg.obj;

            updateProgress();

            if (progress == 100) {
                getController().onBackgroundDataProcessingTaskFinished();
                finish();
            }
        }
    }

    private void updateProgress() {
        getActivity().runOnUiThread(progressRunnable);
    }
}
