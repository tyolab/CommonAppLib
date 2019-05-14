/*
 * Copyright (c) 2018. TYONLINE TECHNOLOGY PTY. LTD. (TYOLAB)
 *
 */

package au.com.tyo.app.ui.page;

import android.app.Activity;
import android.os.Message;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import au.com.tyo.app.Constants;
import au.com.tyo.app.Controller;
import au.com.tyo.app.R;

import static au.com.tyo.app.Constants.MESSAGE_BROADCAST_BACKGROUND_PAGE_HIDING;

public class PageBackgroundProgress<T extends Controller> extends Page<T> {

    private static final String TAG = "PageBackgroundProgress";

    public final static String PROGRESS_INFO_TEMPLATE = "Data processing \\%%d";

    private ProgressBar progressBar;

    protected TextView tvPercent;
    protected TextView tvProgressInfo;

    private static final int[] PERCENTS_V1 = new int[] {0, 10, 60, 100};

    private static final int[] PERCENTS_V2 = new int[] {0, 30, 100};

    protected int progress;

    private int taskId;

    private Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            progressBar.setProgress(progress);
            updateProgressInfo();
        }
    };

    /**
     * @param controller
     * @param activity
     */
    public PageBackgroundProgress(T controller, Activity activity) {
        super(controller, activity);

        setContentViewResId(R.layout.page_background_progress);
        setMessageReceiverRequired(true);
        taskId = -1;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    /**
     * Update the progress text,
     *
     * NOTE:
     *
     * ' ' + progress = 32 + progress
     * if progress = 98, so the result is 130
     */
    protected void updateProgressInfo() {
        updateProgressInfo(progress + "%" );
    }

    protected void updateProgressInfo(String text) {
        tvPercent.setText(text);
    }

    @Override
    public void setupComponents() {
        super.setupComponents();

        progressBar = (ProgressBar) findViewById(R.id.progress_bar_background_service);

        tvPercent = (TextView) findViewById(R.id.tv_progress_percentage);

        tvProgressInfo = (TextView) findViewById(R.id.tv_progress_info);
    }

    /**
     * Should only handle the message related to the UI updates
     *
     * @param msg
     */
    @Override
    protected void handleBroadcastMessage(Message msg) {
        if (msg.what == Constants.MESSAGE_BROADCAST_BACKGROUND_PROGRESS) {

            int lp = (int) msg.obj;

            if (lp > 100) {
                Log.w(TAG, "Progress just has jumped over 100");
                return;
            }

            if (lp > progress) {
                progress = lp;
                updateProgress();
            }
        }
        // else if (msg.what == Constants.MESSAGE_BROADCAST_BACKGROUND_TASK_RESULT) {
        // no need to do anything
        // let controller deal with the data processing result and broadcast the message
        // }
        else if (msg.what == Constants.MESSAGE_BROADCAST_BACKGROUND_TASK_DONE) {
            finish();
        }
        else
            super.handleBroadcastMessage(msg);
    }

    private void updateProgress() {
        getActivity().runOnUiThread(progressRunnable);
    }

    @Override
    public boolean onBackPressed() {
        // disable back key
        if (getController().getUi().onBackPressedOnProgressPage())
            return true;
        return super.onBackPressed();
    }

    @Override
    public void onStop() {
        super.onStop();

        getController().broadcastMessage(MESSAGE_BROADCAST_BACKGROUND_PAGE_HIDING);
    }

    @Override
    public void onActivityStart() {
        super.onActivityStart();

        setPageTitleOnToolbar(getActivity().getString(R.string.processing_data));
    }
}
