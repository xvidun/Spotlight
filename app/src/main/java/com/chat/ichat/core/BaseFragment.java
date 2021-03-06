package com.chat.ichat.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;

import com.chat.ichat.MessageService;
import com.chat.ichat.UserSessionManager;
import com.chat.ichat.application.SpotlightApplication;
import com.chat.ichat.config.AnalyticsConstants;
import com.chat.ichat.models.ContactResult;
import com.chat.ichat.models.MessageResult;
import com.chat.ichat.models.UserSession;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.joda.time.DateTime;

import static com.chat.ichat.MessageController.LAST_SEEN_PREFS_FILE;


/**
 * Created by vidhun on 04/07/16.
 */
public abstract class BaseFragment extends Fragment {
    BroadcastReceiver receiver;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.sharedPreferences = SpotlightApplication.getContext().getSharedPreferences(LAST_SEEN_PREFS_FILE, Context.MODE_PRIVATE);
        UserSession userSession = UserSessionManager.getInstance().load();
        if(userSession ==null)
            throw new IllegalStateException("Base activity should be only initialized on user session");

        //Register MessageReceiver
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(MessageService.XMPP_ACTION_RCV_MSG)) {
                    MessageResult s = (MessageResult) intent.getSerializableExtra(MessageService.XMPP_RESULT_MESSAGE);
                    ContactResult from = (ContactResult) intent.getSerializableExtra(MessageService.XMPP_RESULT_CONTACT);
                    onMessageReceived(s, from);
                } else if(intent.getAction().equals(MessageService.XMPP_ACTION_RCV_STATE)) {
                    String from = intent.getStringExtra(MessageService.XMPP_RESULT_FROM);
                    ChatState chatState = (ChatState) intent.getSerializableExtra(MessageService.XMPP_RESULT_STATE);
                    onChatStateReceived(from, chatState);
                } else if(intent.getAction().equals(MessageService.XMPP_ACTION_RCV_RECEIPT)) {
                    String chatId = intent.getStringExtra(MessageService.XMPP_RESULT_CHAT_ID);
                    String deliveryReceiptId = intent.getStringExtra(MessageService.XMPP_RESULT_RECEIPT_ID);
                    String messageId = intent.getStringExtra(MessageService.XMPP_RESULT_MESSAGE_ID);
                    MessageResult.MessageStatus messageStatus = MessageResult.MessageStatus.valueOf(intent.getStringExtra(MessageService.XMPP_RESULT_MSG_STATUS));
                    onMessageStatusReceived(messageId, chatId, deliveryReceiptId, messageStatus);
                } else if(intent.getAction().equals(MessageService.ACTION_INTERNET_CONNECTION_STATUS)) {
                    boolean isConnectionAvailable = intent.getBooleanExtra(MessageService.ACTION_INTERNET_CONNECTION_STATUS, false);
                    onNetworkStatus(isConnectionAvailable);
                } else if(intent.getAction().equals(MessageService.XMPP_ACTION_RCV_PRESENCE)) {
                    Presence.Type type = (Presence.Type) intent.getSerializableExtra(MessageService.XMPP_RESULT_PRESENCE_TYPE);
                    String username = intent.getStringExtra(MessageService.XMPP_RESULT_PRESENCE_FROM);
                    onPresenceChanged(username, type);
                }
            }
        };
    }

    @Override
    public void onStart() {
        IntentFilter filter = new IntentFilter(MessageService.XMPP_ACTION_RCV_STATE);
        filter.addAction(MessageService.XMPP_ACTION_RCV_MSG);
        filter.addAction(MessageService.XMPP_ACTION_RCV_RECEIPT);
        filter.addAction(MessageService.ACTION_INTERNET_CONNECTION_STATUS);
        filter.addAction(MessageService.XMPP_ACTION_RCV_PRESENCE);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver((receiver), filter);
        super.onStart();
    }

    public void onMessageReceived(MessageResult messageId, ContactResult from) {
        Logger.d(this, "[Base]Message Received "+messageId);
        NotificationController.getInstance().showNotificationAndAlert(false);

        /*              Analytics           */
        Bundle bundle = new Bundle();
        bundle.putString(AnalyticsConstants.Param.RECIPIENT_USER_NAME, from.getUsername());
        bundle.putString(AnalyticsConstants.Param.MESSAGE, messageId.getMessage());
    }

    public void onPresenceChanged(String username, Presence.Type type) {
        Logger.d(this, "Received Presence: "+username+", Type: "+type);
        if(type == Presence.Type.available || type == Presence.Type.unavailable) {
            DateTime time = DateTime.now();
            sharedPreferences.edit().putLong(username, time.getMillis()).apply();
        }
    }

    public void onNetworkStatus(boolean isAvailable) {
        if(isAvailable)
            Logger.d(this, "Internet available");
        else
            Logger.d(this, "Internet not available");
    }

    public void onChatStateReceived(String from, ChatState chatState) {
        Logger.d(this, "chatState: "+chatState.name()+", from "+from);
    }

    public void onMessageStatusReceived(String messageId, String chatId, String deliveryReceiptId, MessageResult.MessageStatus messageStatus) {
    }
}
