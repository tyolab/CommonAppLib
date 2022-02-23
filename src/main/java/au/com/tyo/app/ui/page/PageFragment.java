package au.com.tyo.app.ui.page;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import au.com.tyo.app.CommonLog;
import au.com.tyo.app.Constants;
import au.com.tyo.app.ui.UIEntity;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 15/8/17.
 *
 * PageFragment is a only a part of page just like the way of Fragment
 */

public class PageFragment implements UIEntity {

    private View contentView;

    /**
     * The default content view layout resource id
     */
    private int contentViewResId = -1;

    /**
     * Message Receiver
     */
    private BroadcastReceiver messageReceiver;

    /**
     *
     */
    private Fragment fragment;

    /**
     *
     */
    private boolean messageReceiverRequired;

    /**
     *
     */
    private MessageHandler messageHandler;

    public interface MessageHandler {
        boolean handleMessage(Context context, Intent intent);
    }

    public PageFragment(Fragment fragment) {
        this.fragment = fragment;
        messageHandler = null;
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public boolean isMessageReceiverRequired() {
        return messageReceiverRequired;
    }

    public void setMessageReceiverRequired(boolean messageReceiverRequired) {
        this.messageReceiverRequired = messageReceiverRequired;
    }

    @Override
    public void onActivityStart() {

    }

    public void hideContentView() {
        contentView.setVisibility(View.GONE);
    }

    public void showContentView() {
        contentView.setVisibility(View.VISIBLE);
    }

    public void setContentView(View contentView) {
        this.contentView = contentView;
    }

    public View getContentView() {
        return contentView;
    }

    public int getContentViewResId() {
        return contentViewResId;
    }

    public void setContentViewResId(int contentViewResId) {
        this.contentViewResId = contentViewResId;
    }

    public View loadContentView(Context context) {
        return loadContentView(LayoutInflater.from(context));
    }

    public View loadContentView(LayoutInflater inflater) {
        return loadContentView(inflater, null);
    }

    /**
     * Create the content view:
     *  1) if it was null, then make the new layout as one
     *  20 or add the new content to the original content view
     *
     * @param inflater
     * @param parent
     * @return
     */
    public View loadContentView(LayoutInflater inflater, ViewGroup parent) {
        if (contentViewResId > -1) {
            if (null == contentView) {
                if (null != parent) {
                    parent.removeAllViews();
                    contentView = (ViewGroup) inflater.inflate(contentViewResId,
                            parent, true);
                }
                else
                    contentView = (ViewGroup) inflater.inflate(contentViewResId,
                            null);
            }
            else {
                ViewGroup vg = ((ViewGroup) contentView);
                vg.removeAllViews();
                inflater.inflate(contentViewResId, vg, true);
            }
        }
        return contentView;
    }

    @Override
    public void handleIntent(Intent intent) {
        // no ops
    }

    public void onResume() {
        // no ops
    }

    public void onPause() {
        // no ops
    }

    public void onStart() {
        if (isMessageReceiverRequired()) {
            if (null == messageReceiver) {
                createMessageReceiver();
                registerBroadcastReceivers();
            }
        }
    }

    public void onStop() {
        // no ops
    }

    public boolean onDestroy() {
        unregisterBroadcastReceivers();
        return false;
    }

    public void createMessageReceiver() {
        messageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleBroadcastMessage(context, intent);
            }
        };
    }

    protected void handleBroadcastMessage(Context context, Intent intent) {
        if (null != messageHandler && messageHandler.handleMessage(context, intent))
            return;

        if (intent.hasExtra(Constants.DATA_MESSAGE_BROADCAST)) {
            handleBroadcastMessage((Message) intent.getParcelableExtra(Constants.DATA_MESSAGE_BROADCAST));
        }
    }

    /**
     * By default, we have two basic message handling functions for loading data, and when data loading
     * is finished
     *
     * @param msg
     * @return
     */
    protected boolean handleBroadcastMessage(Message msg) {
        switch (msg.what) {
            case Constants.MESSAGE_BROADCAST_LOADING_DATA:
                onLoadingData();
                return true;

            case Constants.MESSAGE_BROADCAST_DATA_LOADED:
                onDataLoaded();
                return true;
        }
        return false;
    }

    protected void onDataLoaded() {
        // override me
    }

    protected void onLoadingData() {
        // override me
    }

    public void registerBroadcastReceivers() {
        CommonLog.d(this, "register broadcast receiver");
        IntentFilter f = new IntentFilter(Constants.ACTION_MESSAGE_RECEIVER);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(messageReceiver, f);
    }

    public void unregisterBroadcastReceivers() {
        if (null != messageReceiver) {
            Log.d(getClass().getSimpleName(), "unregister broadcast receiver");
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(messageReceiver);
            messageReceiver = null;
        }
    }

    public Activity getActivity() {
        if (null != fragment)
            return fragment.getActivity();
        return null;
    }

}
