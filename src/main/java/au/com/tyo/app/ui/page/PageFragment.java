package au.com.tyo.app.ui.page;

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

    private ViewGroup contentView;

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

    public void setContentView(ViewGroup contentView) {
        this.contentView = contentView;
    }

    public ViewGroup getContentView() {
        return contentView;
    }

    public int getContentViewResId() {
        return contentViewResId;
    }

    public void setContentViewResId(int contentViewResId) {
        this.contentViewResId = contentViewResId;
    }

    public void loadContentView(Context context) {
        loadContentView(LayoutInflater.from(context));
    }

    public void loadContentView(LayoutInflater inflater) {
        loadContentView(inflater, null);
    }

    public void loadContentView(LayoutInflater inflater, ViewGroup parent) {
        if (contentViewResId > -1) {
            if (null == contentView && null != parent)
                contentView = (ViewGroup) inflater.inflate(contentViewResId,
                        parent, true);
            else {
                contentView.removeAllViews();
                inflater.inflate(contentViewResId, contentView, true);
            }
        }
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
                handleMessage(context, intent);
            }
        };
    }

    protected void handleMessage(Context context, Intent intent) {
        if (null != messageHandler)
            messageHandler.handleMessage(context, intent);
    }

    public void registerBroadcastReceivers() {
        IntentFilter f = new IntentFilter(Constants.ACTION_MESSAGE_RECEIVER);
        LocalBroadcastManager.getInstance(fragment.getActivity()).registerReceiver(messageReceiver, f);
    }

    public void unregisterBroadcastReceivers() {
        if (null != messageReceiver)
            LocalBroadcastManager.getInstance(fragment.getActivity()).unregisterReceiver(messageReceiver);
    }
}
