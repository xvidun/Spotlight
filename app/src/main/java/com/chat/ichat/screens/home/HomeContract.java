package com.chat.ichat.screens.home;

import com.chat.ichat.core.BasePresenter;
import com.chat.ichat.core.BaseView;

import org.jivesoftware.smackx.chatstates.ChatState;

import java.util.List;

/**
 * Created by vidhun on 13/07/16.
 */
public interface HomeContract {
    interface View extends BaseView {
        /*
            Delivery status:
                 - waiting to send
                 - sent to server
                 - delivered to recipient
                 - read by recipient
         */
        void setDeliveryStatus(int status, int chatId);

        void displayChatList(List<ChatItem> chatList);
        void showChatState(String from, ChatState chatState);
        void showUpdate(int versionCode, String versionName, boolean isMandatory);
        void removeChatItem(String chatId);
        void showContactAddedSuccess(String contactName, String username, boolean isExistingContact);
        void showInvalidIDError();
        void onSyncSuccess();
    }

    interface Presenter extends BasePresenter<HomeContract.View> {
        void loadChatList();
        void init(int currentVersionCode);
        void deleteChat(String chatId);
        void addContact(String userId);
        void performSync();
    }
}
