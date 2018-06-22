package au.com.tyo.app.ui.page;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        void handleMessage(Context context, Intent intent);
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
                ViewGroup vg = (ViewGroup) contentView.getParent();
                vg.removeAllViews();
                contentView = inflater.inflate(contentViewResId, vg, true);
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
            if (null == messageReceiver)
                createMessageReceiver();
            registerBroadcastReceivers();
        }
    }

    public void onStop() {
        unregisterBroadcastReceivers();
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
        if (null != messageHandler)
            messageHandler.handleMessage(context, intent);
    }

    public void registerBroadcastReceivers() {
        IntentFilter f = new IntentFilter(Constants.ACTION_MESSAGE_RECEIVER);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(messageReceiver, f);
    }

    public void unregisterBroadcastReceivers() {
        if (null != messageReceiver)
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(messageReceiver);
    }

    public Activity getActivity() {
        if (null != fragment)
            return fragment.getActivity();
        return null;
    }
}
